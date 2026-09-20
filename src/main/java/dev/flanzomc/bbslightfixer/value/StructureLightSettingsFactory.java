package dev.flanzomc.bbslightfixer.value;

import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import mchorse.bbs_mod.utils.interps.IInterp;
import mchorse.bbs_mod.utils.keyframes.factories.IKeyframeFactory;

public final class StructureLightSettingsFactory implements IKeyframeFactory<StructureLightSettings> {
    public StructureLightSettings fromData(BaseType data){StructureLightSettings v=new StructureLightSettings();if(data!=null&&data.isMap())v.fromData(data);return v;}
    public BaseType toData(StructureLightSettings value){return value==null?new MapType():value.toData();}
    public StructureLightSettings createEmpty(){return new StructureLightSettings();}
    public StructureLightSettings copy(StructureLightSettings value){return value==null?null:value.copy();}
    public StructureLightSettings interpolate(StructureLightSettings preA,StructureLightSettings a,StructureLightSettings b,StructureLightSettings postB,IInterp i,float x){
        preA=or(preA);a=or(a);b=or(b);postB=or(postB);
        int y=(int)Math.round(i.interpolate(IInterp.context.set(preA.intensity,a.intensity,b.intensity,postB.intensity,x)));
        return new StructureLightSettings(a.enabled,Math.max(0,Math.min(15,y)));
    }
    private static StructureLightSettings or(StructureLightSettings v){return v==null?new StructureLightSettings():v;}
}
