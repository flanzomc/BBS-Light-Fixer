package dev.bbslight;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import java.util.ArrayDeque;
import java.util.Deque;

/** Cancels the renderer's entry camera transform while retaining all local/bone transforms. */
public final class ModelBlockAnchor {
    public record Position(double x, double y, double z) {}
    private record Anchor(Matrix4f inverseEntry, double x, double y, double z, Object owner) {}
    private static final ThreadLocal<Deque<Anchor>> ANCHORS = ThreadLocal.withInitial(ArrayDeque::new);
    private ModelBlockAnchor() {}
    public static void begin(Matrix4fc entry, double x, double y, double z) {
        begin(entry,x,y,z,null);
    }
    public static void begin(Matrix4fc entry, double x, double y, double z, Object owner) {
        ANCHORS.get().push(new Anchor(new Matrix4f(entry).invert(), x, y, z, owner));
    }
    public static Object owner() { Anchor a=ANCHORS.get().peek(); return a==null?null:a.owner; }
    public static void end() { if (!ANCHORS.get().isEmpty()) ANCHORS.get().pop(); }
    public static Position resolve(Matrix4fc current) {
        Anchor a = ANCHORS.get().peek();
        if (a == null) return null;
        Vector3f local = new Matrix4f(a.inverseEntry).mul(current).getTranslation(new Vector3f());
        return new Position(a.x + local.x, a.y + local.y, a.z + local.z);
    }
}
