package dev.bbslight.cml;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.forms.forms.Form;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import java.util.ArrayDeque;
import java.util.Deque;

public final class EffectScope {
    public record State(float[] grade,float[] glow,float[] paint,Matrix4f rootInverse,Matrix4f[] masks,float[] enabled,float[] shapes,boolean active) {}
    public static final State NEUTRAL=new State(new float[4],new float[4],new float[4],new Matrix4f(),
        new Matrix4f[]{new Matrix4f(),new Matrix4f(),new Matrix4f(),new Matrix4f(),new Matrix4f(),new Matrix4f()},new float[6],new float[6],false);
    private static final ThreadLocal<Deque<State>> STACK=ThreadLocal.withInitial(ArrayDeque::new);
    public static State current() { return STACK.get().isEmpty()?NEUTRAL:STACK.get().peek(); }
    public static void push(State state) { STACK.get().push(state); }
    public static void pop() { if(!STACK.get().isEmpty())STACK.get().pop(); }
    public static State capture(Form form,MatrixStack stack,boolean picking) {
        if(picking)return NEUTRAL;
        EffectValues v=EffectValues.of(form);
        var g=v.glowColor.get();var p=v.paintColor.get();
        Matrix4f[] masks=new Matrix4f[6];float[] enabled=new float[6],shapes=new float[6];
        for(int i=0;i<6;i++) {
            masks[i]=v.masks[i].transform.get().createMatrix();
            boolean invertible=Math.abs(masks[i].determinant())>1e-10;
            if(invertible)masks[i].invert();else masks[i].identity().scaling(1e10F);
            enabled[i]=v.masks[i].enabled.get()?1:0;shapes[i]=v.masks[i].shape.get();
        }
        Matrix4f root=stack==null?new Matrix4f():new Matrix4f(RenderSystem.getModelViewMatrix()).mul(stack.peek().getPositionMatrix());
        if(Math.abs(root.determinant())>1e-10)root.invert();else root.identity();
        return new State(new float[]{v.brightness.get(),v.contrast.get(),v.hue.get(),v.saturation.get()},
            new float[]{g.r,g.g,g.b,v.glow.get()},new float[]{p.r,p.g,p.b,v.paint.get()},root,masks,enabled,shapes,v.active());
    }
    public static void uniforms(ShaderProgram shader) {
        if(shader!=PortShaders.model && shader!=PortShaders.block)return;
        State s=current();
        // Buffered block/non-VAO vertices already have their normals transformed.
        if(shader==PortShaders.block)shader.getUniform("NormalMat").set(new org.joml.Matrix3f());
        shader.getUniform("FormColorGrade").set(s.grade);
        shader.getUniform("GlowingColor").set(s.glow);
        shader.getUniform("PaintColor").set(s.paint);
        shader.getUniform("RootInverse").set(s.rootInverse);
        String[] prefixes={"Glow","Paint","GradeBrightness","GradeContrast","GradeSaturation","GradeHue"};
        for(int i=0;i<6;i++) {
            String prefix=prefixes[i];
            shader.getUniform(prefix+"Inverse").set(s.masks[i]);
            shader.getUniform(prefix+"Active").set(s.enabled[i]);
            shader.getUniform(prefix+"Shape").set(s.shapes[i]);
        }
    }
}
