package dev.bbslight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.*;
import dev.bbslight.WorldLightField.Source;

/** Render-thread collection with immutable snapshots for chunk workers. No world blocks are modified. */
public final class Lights {
    private record Section(int x, int y, int z) {}
    private static final LightRegistry<Key,Source> sources = new LightRegistry<>(64);
    private static Map<Key, Source> applied = Map.of();
    private static volatile WorldLightField field = new WorldLightField(null,List.of());
    private static final LinkedHashSet<Section> dirty = new LinkedHashSet<>();
    private static Vec3d camera;
    private static boolean collecting;
    private static Object world;
    private static int ticks;
    private static long frameNumber, publishedFrame = -1;
    private static final int MAX_DIRTY = 8192;
    private static final class Key {
        final Object form, entity;
        Key(Object form, Object entity) { this.form=form; this.entity=entity; }
        @Override public int hashCode() { return System.identityHashCode(form)*31+System.identityHashCode(entity); }
        @Override public boolean equals(Object other) { return other instanceof Key k && k.form==form && k.entity==entity; }
    }
    private Lights() {}
    public static int count() { return field.sources.size(); }
    public static void begin(MatrixStack matrices, Vec3d position) {
        sources.beginFrame();
        collecting = LightFixer.enabled;
        camera = position;
    }
    public static void capture(Object form, Object entity, MatrixStack stack, int level) {
        if (!collecting || stack == null || level == 0) return;
        // BBS context.world already includes entity, form and animated parent-bone transforms.
        Vector3f p = stack.peek().getPositionMatrix().getTranslation(new Vector3f());
        capture(form, entity, p.x, p.y, p.z, level);
    }
    public static void capture(Object form, Object entity, double x, double y, double z, int level) {
        if (!collecting || level == 0) return;
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z) || camera.squaredDistanceTo(x,y,z) > 128*128) return;
        sources.put(entity instanceof BlockEntity ? entity : null,new Key(form, entity),new Source(x,y,z,level));
    }
    public static void beginPlaced(Object owner) { if(collecting) sources.beginPlaced(owner); }
    public static void end() { collecting = false; frameNumber++; }
    public static String diagnostic() {
        WorldLightField current=field;
        if(current.sources.isEmpty()) return "No captured sources.";
        Source s=current.sources.get(0);
        return String.format(java.util.Locale.ROOT,"First source: %.2f, %.2f, %.2f; level %d; %d terrain sections pending",s.x(),s.y(),s.z(),s.level(),dirty.size());
    }
    public static void tick(MinecraftClient client) {
        if (client.world != world) {
            world = client.world; sources.clear(); applied = Map.of(); field = new WorldLightField(world,List.of()); dirty.clear(); collecting=false;
        }
        if (client.world == null) return;
        // 10 Hz publication avoids rebuilding terrain on every video frame.
        if (++ticks % 2 == 0 && publishedFrame != frameNumber) {
            publishedFrame = frameNumber;
            Map<Key, Source> next = LightFixer.enabled ? sources.snapshot(owner ->
                owner instanceof BlockEntity block && !block.isRemoved() && block.getWorld()==client.world) : Map.of();
            for (var e : applied.entrySet()) {
                Source n = next.get(e.getKey()), old=e.getValue();
                if (n == null || changed(old,n)) { invalidate(old); if(n!=null) invalidate(n); }
                else next.put(e.getKey(), old);
            }
            for (var e : next.entrySet()) if (!applied.containsKey(e.getKey())) invalidate(e.getValue());
            applied=next; field=new WorldLightField(world,List.copyOf(next.values()));
        }
        // Fixed upper budget, including old positions so moved/deleted lights do not leave ghosts.
        var it=dirty.iterator();
        for(int n=0; n<32 && it.hasNext(); n++) {
            Section s=it.next(); it.remove();
            // scheduleBlockRender takes section coordinates. The range overload walks
            // every block, redundantly scheduling the same section thousands of times.
            client.worldRenderer.scheduleBlockRender(s.x,s.y,s.z);
        }
    }
    private static boolean changed(Source a, Source b) {
        double dx=a.x()-b.x(),dy=a.y()-b.y(),dz=a.z()-b.z();
        return a.level()!=b.level() || dx*dx+dy*dy+dz*dz>=0.0625;
    }
    private static void invalidate(Source s) {
        // One extra block covers neighboring samples used by smooth terrain lighting.
        int radius=s.level()+1;
        for(int x=(int)Math.floor((s.x()-radius)/16);x<=(int)Math.floor((s.x()+radius)/16);x++)
            for(int y=(int)Math.floor((s.y()-radius)/16);y<=(int)Math.floor((s.y()+radius)/16);y++)
                for(int z=(int)Math.floor((s.z()-radius)/16);z<=(int)Math.floor((s.z()+radius)/16);z++) {
                    if(dirty.size()>=MAX_DIRTY) { MinecraftClient.getInstance().worldRenderer.reload(); dirty.clear(); return; }
                    dirty.add(new Section(x,y,z));
                }
    }
    public static void clear() {
        for(Source s:field.sources) invalidate(s);
        sources.clear(); applied=Map.of(); field=new WorldLightField(world,List.of()); collecting=false;
    }
    public static int sample(BlockPos pos, int vanilla) {
        if(!LightFixer.enabled) return vanilla;
        return LightSpec.merge(vanilla,field.intensity(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5));
    }
    public static int sampleWorldBlockLight(Object queriedWorld,BlockPos pos,int vanilla) {
        if(!LightFixer.enabled) return vanilla;
        return field.blockLight(queriedWorld,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,vanilla);
    }
}
