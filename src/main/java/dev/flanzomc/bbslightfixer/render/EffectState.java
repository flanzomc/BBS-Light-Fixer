package dev.flanzomc.bbslightfixer.render;

import dev.flanzomc.bbslightfixer.access.CmlColorAccess;
import dev.flanzomc.bbslightfixer.access.CmlFormAccess;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.ModelForm;
import mchorse.bbs_mod.forms.forms.utils.EffectTransform;
import mchorse.bbs_mod.forms.forms.utils.EffectTransformMath;
import mchorse.bbs_mod.forms.forms.utils.GlowSettings;
import mchorse.bbs_mod.forms.forms.utils.PaintSettings;
import mchorse.bbs_mod.utils.colors.Color;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class EffectState {
    public static final class Mask {
        public final Matrix4f inverse=new Matrix4f();
        public final Vector3f half=new Vector3f();
        public float active,shape,bottom=1F;
        Mask(EffectTransform t, boolean block){
            EffectTransformMath.buildInverseMatrix(t,inverse);
            if(block) EffectTransformMath.resolveBlockMaskHalfExtents(t,half);
            else EffectTransformMath.resolveModelMaskHalfExtents(t,half);
            active=t!=null&&t.isActive()?1F:0F;
            shape=t==null||t.shape==null?0F:t.shape.id;
        }
        Mask(Mask m){inverse.set(m.inverse);half.set(m.half);active=m.active;shape=m.shape;bottom=m.bottom;}
    }

    public final float[] grade=new float[4];
    public final float[] glow=new float[4];
    public final float[] paint=new float[4];
    public final float[] tint=new float[]{1F,1F,1F,1F};
    public final Matrix4f rootInverse=new Matrix4f();
    public final Mask paintMask,glowMask,colorMask,brightnessMask,contrastMask,hueMask,saturationMask;
    public final float colorTintMasked,glowPaintOnly;
    public final boolean active;

    private EffectState(Form form, MatrixStack stack, boolean block, boolean cpu, boolean picking){
        CmlFormAccess fa=(CmlFormAccess)(Object)form;
        PaintSettings ps=fa.bbsLight$getPaintSettings().get();
        GlowSettings gs=fa.bbsLight$getGlowSettings().get();
        Color legacyPaint=fa.bbsLight$getPaintColor().get(), legacyGlow=fa.bbsLight$getGlowingColor().get();
        Color pc=new Color(),gc=new Color();
        ps.resolveColor(legacyPaint,pc); gs.resolveColor(legacyGlow,gc);
        paint[0]=pc.r;paint[1]=pc.g;paint[2]=pc.b;paint[3]=ps.resolveIntensity(legacyPaint);
        glow[0]=gc.r;glow[1]=gc.g;glow[2]=gc.b;glow[3]=gs.resolveIntensity(legacyGlow);
        glowPaintOnly=gs.resolvePaintOnly()?1F:0F;

        Color main=form instanceof ModelForm m?m.color.get():form instanceof BlockForm b?b.color.get():new Color(1F,1F,1F,1F);
        CmlColorAccess ca=(CmlColorAccess)(Object)main;
        grade[0]=finite(ca.bbsLight$getBrightness());grade[1]=finite(ca.bbsLight$getContrast());
        grade[2]=finite(ca.bbsLight$getHue());grade[3]=finite(ca.bbsLight$getSaturation());
        tint[0]=main.r;tint[1]=main.g;tint[2]=main.b;tint[3]=main.a;
        colorTintMasked=ca.bbsLight$getColorTransform().isActive()?1F:0F;

        paintMask=new Mask(ps.transform,block);
        glowMask=new Mask(gs.transform,block);
        colorMask=new Mask(ca.bbsLight$getColorTransform(),block);
        brightnessMask=new Mask(ca.bbsLight$getBrightnessTransform(),block);
        contrastMask=new Mask(ca.bbsLight$getContrastTransform(),block);
        hueMask=new Mask(ca.bbsLight$getHueTransform(),block);
        saturationMask=new Mask(ca.bbsLight$getSaturationTransform(),block);

        if(cpu && stack!=null){
            rootInverse.set(stack.peek().getPositionMatrix());
            if(Math.abs(rootInverse.determinant())>1e-10F)rootInverse.invert();else rootInverse.identity();
        } else rootInverse.identity();

        active=!picking && (Math.abs(paint[3])>.001F||Math.abs(glow[3])>.001F||
            Math.abs(grade[0])>.001F||Math.abs(grade[1])>.001F||Math.abs(grade[2])>.001F||Math.abs(grade[3])>.001F||
            colorTintMasked>.5F||paintMask.active>.5F||glowMask.active>.5F||
            brightnessMask.active>.5F||contrastMask.active>.5F||hueMask.active>.5F||saturationMask.active>.5F);
    }

    private EffectState(EffectState s){
        System.arraycopy(s.grade,0,grade,0,4);System.arraycopy(s.glow,0,glow,0,4);System.arraycopy(s.paint,0,paint,0,4);System.arraycopy(s.tint,0,tint,0,4);
        rootInverse.set(s.rootInverse);paintMask=new Mask(s.paintMask);glowMask=new Mask(s.glowMask);colorMask=new Mask(s.colorMask);
        brightnessMask=new Mask(s.brightnessMask);contrastMask=new Mask(s.contrastMask);hueMask=new Mask(s.hueMask);saturationMask=new Mask(s.saturationMask);
        colorTintMasked=s.colorTintMasked;glowPaintOnly=s.glowPaintOnly;active=s.active;
    }

    public static EffectState model(Form f,MatrixStack stack,boolean cpu,boolean picking){return new EffectState(f,stack,false,cpu,picking);}
    public static EffectState block(Form f,MatrixStack stack,boolean picking){return new EffectState(f,stack,true,true,picking);}
    public EffectState copy(){return new EffectState(this);}
    private static float finite(float v){return Float.isFinite(v)?v:0F;}
}
