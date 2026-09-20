package dev.flanzomc.bbslightfixer;

import dev.lambdaurora.lambdynlights.DynamicLightSource;
import dev.lambdaurora.lambdynlights.LambDynLights;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class CmlDynamicLights
{
    private record Anchor(BlockEntity owner, Matrix4f inverse, Set<Form> seen) {}

    private static final Deque<Anchor> ANCHORS = new ArrayDeque<>();
    private static final IdentityHashMap<Object, IdentityHashMap<Form, Source>> SOURCES = new IdentityHashMap<>();
    private static long clock;

    private CmlDynamicLights() {}

    public static void begin(BlockEntity owner, MatrixStack stack)
    {
        ANCHORS.push(new Anchor(owner, new Matrix4f(stack.peek().getPositionMatrix()).invert(), Collections.newSetFromMap(new IdentityHashMap<>())));
    }

    public static void end()
    {
        if (ANCHORS.isEmpty()) return;

        Anchor anchor = ANCHORS.pop();
        Map<Form, Source> map = SOURCES.get(anchor.owner);

        if (map != null)
        {
            map.entrySet().removeIf(entry ->
            {
                if (anchor.seen.contains(entry.getKey())) return false;
                entry.getValue().setDynamicLightEnabled(false);
                return true;
            });
        }
    }

    public static int luminance(BlockForm form)
    {
        CmlEffectValues values = CmlEffectValues.of(form);
        if (!values.emitLight.get()) return 0;
        return Math.min(form.blockState.get().getLuminance(), values.lightIntensity.get());
    }

    public static void capture(Form form, FormRenderingContext context)
    {
        if (!(form instanceof BlockForm block) || luminance(block) <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        Anchor anchor = ANCHORS.peek();
        Object owner = anchor == null ? context.entity : anchor.owner;
        if (owner == null) return;

        Vector3f position;

        if (anchor != null)
        {
            anchor.seen.add(form);
            position = new Matrix4f(anchor.inverse).mul(context.stack.peek().getPositionMatrix()).transformPosition(new Vector3f(0.5F, 0.5F, 0.5F));
            position.add(anchor.owner.getPos().getX(), anchor.owner.getPos().getY(), anchor.owner.getPos().getZ());
        }
        else
        {
            if (context.world == null) return;
            position = context.world.peek().getPositionMatrix().transformPosition(new Vector3f(0.5F, 0.5F, 0.5F));
        }

        IdentityHashMap<Form, Source> map = SOURCES.computeIfAbsent(owner, key -> new IdentityHashMap<>());
        Source source = map.get(form);

        if (source == null)
        {
            source = new Source(client.world, owner, block);
            map.put(form, source);
            source.x = position.x;
            source.y = position.y;
            source.z = position.z;
            source.setDynamicLightEnabled(true);
        }

        source.x = position.x;
        source.y = position.y;
        source.z = position.z;
        source.seen = clock;

        if (!LambDynLights.get().containsLightSource(source)) source.setDynamicLightEnabled(true);
    }

    public static void tick()
    {
        clock++;
        World world = MinecraftClient.getInstance().world;

        SOURCES.entrySet().removeIf(entry ->
        {
            entry.getValue().values().removeIf(source ->
            {
                boolean expired = source.world != world
                    || (source.owner instanceof BlockEntity blockEntity
                        ? blockEntity.isRemoved() || blockEntity.getWorld() != world
                        : clock - source.seen > 2);

                if (expired) source.setDynamicLightEnabled(false);
                return expired;
            });

            return entry.getValue().isEmpty();
        });
    }

    private static final class Source implements DynamicLightSource
    {
        final World world;
        final Object owner;
        final BlockForm form;
        double x, y, z, lastX = Double.NaN, lastY, lastZ;
        long seen;
        int lastLevel = -1;
        final Set<BlockPos> sections = new HashSet<>();

        Source(World world, Object owner, BlockForm form)
        {
            this.world = world;
            this.owner = owner;
            this.form = form;
        }

        public double getDynamicLightX() { return this.x; }
        public double getDynamicLightY() { return this.y; }
        public double getDynamicLightZ() { return this.z; }
        public World getDynamicLightWorld() { return this.world; }
        public int getLuminance() { return CmlDynamicLights.luminance(this.form); }
        public void resetDynamicLight() { this.lastLevel = -1; }
        public void dynamicLightTick() {}
        public boolean shouldUpdateDynamicLight() { return true; }

        public boolean lambdynlights$updateDynamicLight(WorldRenderer renderer)
        {
            int level = this.getLuminance();

            if (level == this.lastLevel && Math.abs(this.x - this.lastX) < 0.1 && Math.abs(this.y - this.lastY) < 0.1 && Math.abs(this.z - this.lastZ) < 0.1)
            {
                return false;
            }

            Set<BlockPos> next = new HashSet<>();

            if (level > 0)
            {
                for (int sx = (int) Math.floor((this.x - 8) / 16); sx <= (int) Math.floor((this.x + 8) / 16); sx++)
                for (int sy = (int) Math.floor((this.y - 8) / 16); sy <= (int) Math.floor((this.y + 8) / 16); sy++)
                for (int sz = (int) Math.floor((this.z - 8) / 16); sz <= (int) Math.floor((this.z + 8) / 16); sz++)
                {
                    next.add(new BlockPos(sx, sy, sz));
                }
            }

            this.sections.addAll(next);
            this.lambdynlights$scheduleTrackedChunksRebuild(renderer);
            this.sections.clear();
            this.sections.addAll(next);

            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;
            this.lastLevel = level;
            return true;
        }

        public void lambdynlights$scheduleTrackedChunksRebuild(WorldRenderer renderer)
        {
            for (BlockPos pos : this.sections)
            {
                LambDynLights.scheduleChunkRebuild(renderer, pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }
}
