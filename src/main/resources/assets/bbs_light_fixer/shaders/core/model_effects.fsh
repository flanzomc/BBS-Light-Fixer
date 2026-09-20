#version 150
#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform int PassMode;

uniform vec4 PaintColor;
uniform vec4 GlowingColor;
uniform float GlowPaintOnly;
uniform vec4 FormColorTint;
uniform float ColorTintMasked;
uniform vec4 FormColorGrade;

uniform mat4 PaintEffectInverse;
uniform float PaintEffectActive;
uniform vec3 PaintMaskHalf;
uniform float PaintMaskBottomAnchored;
uniform float PaintMaskShape;

uniform mat4 GlowEffectInverse;
uniform float GlowEffectActive;
uniform vec3 GlowMaskHalf;
uniform float GlowMaskBottomAnchored;
uniform float GlowMaskShape;

uniform mat4 ColorEffectInverse;
uniform float ColorEffectActive;
uniform vec3 ColorMaskHalf;
uniform float ColorMaskBottomAnchored;
uniform float ColorMaskShape;

uniform mat4 GradeBrightnessInverse;
uniform float GradeBrightnessActive;
uniform vec3 GradeBrightnessHalf;
uniform float GradeBrightnessBottomAnchored;
uniform float GradeBrightnessShape;

uniform mat4 GradeContrastInverse;
uniform float GradeContrastActive;
uniform vec3 GradeContrastHalf;
uniform float GradeContrastBottomAnchored;
uniform float GradeContrastShape;

uniform mat4 GradeHueInverse;
uniform float GradeHueActive;
uniform vec3 GradeHueHalf;
uniform float GradeHueBottomAnchored;
uniform float GradeHueShape;

uniform mat4 GradeSaturationInverse;
uniform float GradeSaturationActive;
uniform vec3 GradeSaturationHalf;
uniform float GradeSaturationBottomAnchored;
uniform float GradeSaturationShape;

in float vertexDistance;
in vec4 vertexColor;
in vec4 rawVertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec3 formRootPos;
out vec4 fragColor;

vec3 bbsRgb2Hsl(vec3 c)
{
    float maxc=max(c.r,max(c.g,c.b));
    float minc=min(c.r,min(c.g,c.b));
    float l=(maxc+minc)*0.5;
    float d=maxc-minc;
    if(d<1e-5)return vec3(0.0,0.0,l);
    float s=l>0.5?d/(2.0-maxc-minc):d/(maxc+minc);
    float h;
    if(maxc==c.r)h=(c.g-c.b)/d+(c.g<c.b?6.0:0.0);
    else if(maxc==c.g)h=(c.b-c.r)/d+2.0;
    else h=(c.r-c.g)/d+4.0;
    return vec3(h/6.0,s,l);
}

float bbsHue2Rgb(float p,float q,float t)
{
    if(t<0.0)t+=1.0;if(t>1.0)t-=1.0;
    if(t<1.0/6.0)return p+(q-p)*6.0*t;
    if(t<0.5)return q;
    if(t<2.0/3.0)return p+(q-p)*(2.0/3.0-t)*6.0;
    return p;
}

vec3 bbsHsl2Rgb(vec3 hsl)
{
    if(hsl.y<1e-5)return vec3(hsl.z);
    float q=hsl.z<0.5?hsl.z*(1.0+hsl.y):hsl.z+hsl.y-hsl.z*hsl.y;
    float p=2.0*hsl.z-q;
    return vec3(bbsHue2Rgb(p,q,hsl.x+1.0/3.0),bbsHue2Rgb(p,q,hsl.x),bbsHue2Rgb(p,q,hsl.x-1.0/3.0));
}

float bbsSdTriangle2D(vec2 p,vec2 a,vec2 b,vec2 c)
{
    vec2 e0=b-a,e1=c-b,e2=a-c;
    vec2 v0=p-a,v1=p-b,v2=p-c;
    vec2 pq0=v0-e0*clamp(dot(v0,e0)/max(dot(e0,e0),0.0001),0.0,1.0);
    vec2 pq1=v1-e1*clamp(dot(v1,e1)/max(dot(e1,e1),0.0001),0.0,1.0);
    vec2 pq2=v2-e2*clamp(dot(v2,e2)/max(dot(e2,e2),0.0001),0.0,1.0);
    float s=sign(e0.x*e2.y-e0.y*e2.x);
    vec2 d=min(min(vec2(dot(pq0,pq0),s*(v0.x*e0.y-v0.y*e0.x)),vec2(dot(pq1,pq1),s*(v1.x*e1.y-v1.y*e1.x))),vec2(dot(pq2,pq2),s*(v2.x*e2.y-v2.y*e2.x)));
    return -sqrt(max(d.x,0.0))*sign(d.y);
}

float bbsEffectMask(vec3 rootPos,mat4 inv,float active,vec3 halfExtents,float bottomAnchored,float shape)
{
    if(active<0.5)return 1.0;
    vec3 local=(inv*vec4(rootPos,1.0)).xyz;
    if(bottomAnchored>0.5)local.y-=halfExtents.y;
    float maxHalf=max(halfExtents.x,max(halfExtents.y,halfExtents.z));
    if(maxHalf<0.001)return 0.0;
    float dist;
    if(shape>1.5){
        vec2 halfXY=max(halfExtents.xy,vec2(0.001));
        float dTri=bbsSdTriangle2D(local.xy,vec2(0.0,halfXY.y),vec2(-halfXY.x,-halfXY.y),vec2(halfXY.x,-halfXY.y));
        float dZ=abs(local.z)-halfExtents.z;
        dist=length(max(vec2(dTri,dZ),0.0))+min(max(dTri,dZ),0.0);
    }else if(shape>0.5){
        vec3 safeHalf=max(halfExtents,vec3(0.001));
        float radius=length(local/safeHalf);
        dist=(radius-1.0)*maxHalf;
    }else{
        vec3 d=abs(local)-halfExtents;
        dist=length(max(d,0.0))+min(max(max(d.x,d.y),d.z),0.0);
    }
    float falloff=max(maxHalf*0.15,0.001);
    return 1.0-smoothstep(0.0,falloff,dist);
}

vec3 bbsApplyGlow(vec3 color,float strength)
{
    if(abs(strength)<0.001)return color;
    if(strength>0.0){
        if(strength>=1.0)return color+GlowingColor.rgb*strength;
        return mix(color,color+GlowingColor.rgb,strength);
    }
    return color*max(0.0,1.0+strength);
}

vec3 bbsApplyGrade(vec3 rgb)
{
    if(abs(FormColorGrade.x)<0.001&&abs(FormColorGrade.y)<0.001&&abs(FormColorGrade.z)<0.001&&abs(FormColorGrade.w)<0.001)return rgb;
    vec3 outRgb=rgb;
    if(abs(FormColorGrade.x)>=0.001){
        float m=bbsEffectMask(formRootPos,GradeBrightnessInverse,GradeBrightnessActive,GradeBrightnessHalf,GradeBrightnessBottomAnchored,GradeBrightnessShape);
        outRgb=mix(outRgb,outRgb+FormColorGrade.x,m);
    }
    if(abs(FormColorGrade.y)>=0.001){
        float m=bbsEffectMask(formRootPos,GradeContrastInverse,GradeContrastActive,GradeContrastHalf,GradeContrastBottomAnchored,GradeContrastShape);
        outRgb=mix(outRgb,vec3(0.5)+(1.0+FormColorGrade.y)*(outRgb-vec3(0.5)),m);
    }
    if(abs(FormColorGrade.w)>=0.001){
        float m=bbsEffectMask(formRootPos,GradeSaturationInverse,GradeSaturationActive,GradeSaturationHalf,GradeSaturationBottomAnchored,GradeSaturationShape);
        vec3 hsl=bbsRgb2Hsl(clamp(outRgb,0.0,1.0));hsl.y=clamp(hsl.y*(1.0+FormColorGrade.w),0.0,1.0);
        outRgb=mix(outRgb,bbsHsl2Rgb(hsl),m);
    }
    if(abs(FormColorGrade.z)>0.01){
        float m=bbsEffectMask(formRootPos,GradeHueInverse,GradeHueActive,GradeHueHalf,GradeHueBottomAnchored,GradeHueShape);
        vec3 hsl=bbsRgb2Hsl(clamp(outRgb,0.0,1.0));hsl.x=fract(hsl.x+FormColorGrade.z/360.0);
        outRgb=mix(outRgb,bbsHsl2Rgb(hsl),m);
    }
    return clamp(outRgb,0.0,1.0);
}

void main()
{
    vec4 texSample=texture(Sampler0,texCoord0);
    if(texSample.a<0.1)discard;

    vec4 tint=vec4(1.0);
    if(ColorTintMasked>0.5){
        float cm=bbsEffectMask(formRootPos,ColorEffectInverse,ColorEffectActive,ColorMaskHalf,ColorMaskBottomAnchored,ColorMaskShape);
        tint.rgb=mix(vec3(1.0),FormColorTint.rgb,cm);
        tint.a=mix(1.0,FormColorTint.a,cm);
    }

    vec4 color=texSample*vertexColor*tint*ColorModulator;
    float modelAlpha=texSample.a*rawVertexColor.a*tint.a*ColorModulator.a;
    color.a=modelAlpha;

    if(PassMode==1&&color.a<0.999)discard;
    if(PassMode==2&&color.a>=0.999)discard;

    color.rgb=mix(overlayColor.rgb,color.rgb,overlayColor.a);
    color.rgb*=lightMapColor.rgb;

    float paintStrength=clamp(abs(PaintColor.a),0.0,1.0);
    paintStrength*=bbsEffectMask(formRootPos,PaintEffectInverse,PaintEffectActive,PaintMaskHalf,PaintMaskBottomAnchored,PaintMaskShape);
    if(PaintColor.a>0.001)color.rgb=mix(color.rgb,PaintColor.rgb,paintStrength);
    else if(PaintColor.a<-0.001){
        float factor=max(0.0,1.0+PaintColor.a);
        float pm=bbsEffectMask(formRootPos,PaintEffectInverse,PaintEffectActive,PaintMaskHalf,PaintMaskBottomAnchored,PaintMaskShape);
        color.rgb=mix(color.rgb,color.rgb*factor,pm);
    }

    float glowStrength=GlowingColor.a;
    if(GlowPaintOnly>0.5)glowStrength*=paintStrength;
    glowStrength*=bbsEffectMask(formRootPos,GlowEffectInverse,GlowEffectActive,GlowMaskHalf,GlowMaskBottomAnchored,GlowMaskShape);
    color.rgb=bbsApplyGlow(color.rgb,glowStrength);
    color.rgb=bbsApplyGrade(color.rgb);
    fragColor=linear_fog(color,vertexDistance,FogStart,FogEnd,FogColor);
}
