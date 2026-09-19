package dev.bbslight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.*;

/** Render-thread collection with immutable snapshots for chunk workers. No world blocks are modified. */
public final class Lights {
    private record Source(double x, double y, double z, int level) {}
    private record Section(int x, int y, int z) {}
    private static final Map<Key, Source> frame = new HashMap<>();
    private static Map<Key, Source> applied = Map.of();
    private static volatile List<Source> snapshot = List.of();
    private static final LinkedHashSet<Section> dirty = new LinkedHashSet<>();
    private static Vec3d camera;
    private static boolean collecting;
    private static Object world;
    private static int ticks;
    private static long frameNumber, publishedFrame = -1;
    private static final int MAX_LIGHTS = 64, MAX_DIRTY = 8192;
    private static final class Key {
        final Object form, entity;
        Key(Object form, Object entity) { this.form=form; this.entity=entity; }
        @Override public int hashCode() { return System.identityHashCode(form)*31+System.identityHashCode(entity); }
        @Override public boolean equals(Object other) { return other instanceof Key k && k.form==form && k.entity==entity; }
    }
    private Lights() {}
    public static int count() { return snapshot.size(); }
    public static void begin(MatrixStack matrices, Vec3d position) {
        frame.clear();
        collecting = LightFixer.enabled;
        camera = position;
    }
    public static void capture(Object form, Object entity, MatrixStack stack, int level) {
        if (!collecting || stack == null || level == 0 || frame.size() >= MAX_LIGHTS) return;
        // BBS context.world already includes entity, form and animated parent-bone transforms.
        Vector3f p = stack.peek().getPositionMatrix().getTranslation(new Vector3f());
        if (!Float.isFinite(p.x) || !Float.isFinite(p.y) || !Float.isFinite(p.z) || camera.squaredDistanceTo(p.x,p.y,p.z) > 128*128) return;
        frame.put(new Key(form, entity), new Source(p.x,p.y,p.z,level));
    }
    public static void end() { collecting = false; frameNumber++; }
    public static String diagnostic() {
        if(snapshot.isEmpty()) return "No captured sources.";
        Source s=snapshot.get(0);
        return String.format(java.util.Locale.ROOT,"First source: %.2f, %.2f, %.2f; level %d",s.x,s.y,s.z,s.level);
    }
    public static void tick(MinecraftClient client) {
        if (client.world != world) {
            world = client.world; frame.clear(); applied = Map.of(); snapshot = List.of(); dirty.clear(); collecting=false;
        }
        if (client.world == null) return;
        // 10 Hz publication avoids rebuilding terrain on every video frame.
        if (++ticks % 2 == 0 && publishedFrame != frameNumber) {
            publishedFrame = frameNumber;
            Map<Key, Source> next = LightFixer.enabled ? new HashMap<>(frame) : Map.of();
            for (var e : applied.entrySet()) {
                Source n = next.get(e.getKey()), old=e.getValue();
                if (n == null || changed(old,n)) { invalidate(old); if(n!=null) invalidate(n); }
                else next.put(e.getKey(), old);
            }
            for (var e : next.entrySet()) if (!applied.containsKey(e.getKey())) invalidate(e.getValue());
            applied=next; snapshot=List.copyOf(next.values());
        }
        // Fixed upper budget, including old positions so moved/deleted lights do not leave ghosts.
        var it=dirty.iterator();
        for(int n=0; n<32 && it.hasNext(); n++) {
            Section s=it.next(); it.remove();
            client.worldRenderer.scheduleBlockRenders(s.x*16,s.y*16,s.z*16,s.x*16+15,s.y*16+15,s.z*16+15);
        }
    }
    private static boolean changed(Source a, Source b) {
        double dx=a.x-b.x,dy=a.y-b.y,dz=a.z-b.z;
        return a.level!=b.level || dx*dx+dy*dy+dz*dz>=0.0625;
    }
    private static void invalidate(Source s) {
        for(int x=(int)Math.floor((s.x-s.level)/16);x<=(int)Math.floor((s.x+s.level)/16);x++)
            for(int y=(int)Math.floor((s.y-s.level)/16);y<=(int)Math.floor((s.y+s.level)/16);y++)
                for(int z=(int)Math.floor((s.z-s.level)/16);z<=(int)Math.floor((s.z+s.level)/16);z++) {
                    if(dirty.size()>=MAX_DIRTY) { MinecraftClient.getInstance().worldRenderer.reload(); dirty.clear(); return; }
                    dirty.add(new Section(x,y,z));
                }
    }
    public static void clear() {
        for(Source s:snapshot) invalidate(s);
        frame.clear(); applied=Map.of(); snapshot=List.of(); collecting=false;
    }
    public static int sample(BlockPos pos, int vanilla) {
        if(!LightFixer.enabled) return vanilla;
        double intensity=0;
        for(Source s:snapshot) intensity=Math.max(intensity,LightSpec.intensity(s.x,s.y,s.z,s.level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5));
        return LightSpec.merge(vanilla,intensity);
    }
}
