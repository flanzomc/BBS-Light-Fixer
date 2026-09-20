package dev.flanzomc.bbslightfixer.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.joml.Matrix3f;

import java.util.ArrayDeque;
import java.util.Deque;

public final class EffectRenderContext {
    private static final ThreadLocal<Deque<EffectState>> STACK=ThreadLocal.withInitial(ArrayDeque::new);
    private EffectRenderContext(){}
    public static EffectState current(){return STACK.get().peek();}
    public static boolean active(){EffectState s=current();return s!=null&&s.active;}
    public static void push(EffectState s){STACK.get().push(s);}
    public static void pop(){if(!STACK.get().isEmpty())STACK.get().pop();}
    public static void replace(EffectState s){if(!STACK.get().isEmpty())STACK.get().pop();STACK.get().push(s);}

    public static void upload(ShaderProgram shader){
        if(shader!=EffectShaders.model && shader!=EffectShaders.block)return;
        EffectState s=current();if(s==null)return;
        for(int i=0;i<12;i++)shader.addSampler("Sampler"+i,RenderSystem.getShaderTexture(i));
        if(shader.viewRotationMat!=null)shader.viewRotationMat.set(RenderSystem.getInverseViewRotationMatrix());
        if(shader.fogStart!=null)shader.fogStart.set(RenderSystem.getShaderFogStart());
        if(shader.fogEnd!=null)shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        if(shader.fogColor!=null)shader.fogColor.set(RenderSystem.getShaderFogColor());
        if(shader.fogShape!=null)shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        if(shader.gameTime!=null)shader.gameTime.set(RenderSystem.getShaderGameTime());
        if(shader.textureMat!=null)shader.textureMat.set(RenderSystem.getTextureMatrix());
        RenderSystem.setupShaderLights(shader);
        set4(shader,"PaintColor",s.paint);set4(shader,"GlowingColor",s.glow);set4(shader,"FormColorGrade",s.grade);set4(shader,"FormColorTint",s.tint);
        set1(shader,"ColorTintMasked",s.colorTintMasked);set1(shader,"GlowPaintOnly",s.glowPaintOnly);
        GlUniform root=shader.getUniform("FormRootInverse");if(root!=null)root.set(s.rootInverse);
        uploadMask(shader,"Paint",s.paintMask);uploadMask(shader,"Glow",s.glowMask);uploadMask(shader,"Color",s.colorMask);
        uploadMask(shader,"GradeBrightness",s.brightnessMask);uploadMask(shader,"GradeContrast",s.contrastMask);
        uploadMask(shader,"GradeHue",s.hueMask);uploadMask(shader,"GradeSaturation",s.saturationMask);
        if(shader==EffectShaders.block){GlUniform n=shader.getUniform("NormalMat");if(n!=null)n.set(new Matrix3f());}
    }
    private static void uploadMask(ShaderProgram sh,String p,EffectState.Mask m){
        GlUniform i=sh.getUniform(p+"EffectInverse"); if(i==null)i=sh.getUniform(p+"Inverse"); if(i!=null)i.set(m.inverse);
        set1(sh,p+"EffectActive",m.active);set1(sh,p+"Active",m.active);
        GlUniform h=sh.getUniform(p+"MaskHalf");if(h==null)h=sh.getUniform(p+"Half");if(h!=null)h.set(m.half.x,m.half.y,m.half.z);
        set1(sh,p+"MaskBottomAnchored",m.bottom);set1(sh,p+"BottomAnchored",m.bottom);
        set1(sh,p+"MaskShape",m.shape);set1(sh,p+"Shape",m.shape);
    }
    private static void set4(ShaderProgram s,String n,float[] v){GlUniform u=s.getUniform(n);if(u!=null)u.set(v);}
    private static void set1(ShaderProgram s,String n,float v){GlUniform u=s.getUniform(n);if(u!=null)u.set(v);}
}
