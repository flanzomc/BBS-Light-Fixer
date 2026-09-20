package dev.bbslight;

import java.util.List;

/** Immutable data shared with terrain rebuild workers. Never alters Minecraft light storage. */
final class WorldLightField {
    record Source(double x,double y,double z,int level) {}
    final Object world;
    final List<Source> sources;
    WorldLightField(Object world,List<Source> sources) { this.world=world; this.sources=List.copyOf(sources); }
    double intensity(double x,double y,double z) {
        double result=0;
        for(Source s:sources) result=Math.max(result,LightSpec.intensity(s.x,s.y,s.z,s.level,x,y,z));
        return result;
    }
    int blockLight(Object queriedWorld,double x,double y,double z,int vanilla) {
        if(queriedWorld==null || queriedWorld!=world || vanilla>=15) return vanilla;
        return Math.max(vanilla,Math.min(15,(int)Math.ceil(intensity(x,y,z))));
    }
}
