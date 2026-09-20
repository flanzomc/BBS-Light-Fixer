package dev.bbslight;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorldLightingTest {
    @Test void lightsNearbyGroundAndWallsWithFalloff() {
        Object world=new Object();
        var field=new WorldLightField(world,List.of(new WorldLightField.Source(100.5,65,-200.5,15)));
        assertEquals(14,field.blockLight(world,100.5,64,-200.5,0));
        assertEquals(10,field.blockLight(world,103.5,65,-196.5,0));
        assertEquals(0,field.blockLight(world,120.5,65,-200.5,0));
        assertEquals(15,field.blockLight(world,100.5,65,-200.5,0));
    }
    @Test void preservesExistingLightAndIsolatesOtherWorlds() {
        Object clientWorld=new Object(),serverWorld=new Object(),oldDimension=new Object();
        var field=new WorldLightField(clientWorld,List.of(new WorldLightField.Source(0,0,0,8)));
        assertEquals(14,field.blockLight(clientWorld,0,0,0,14));
        assertEquals(2,field.blockLight(serverWorld,0,0,0,2));
        assertEquals(2,field.blockLight(oldDimension,0,0,0,2));
        assertEquals(2,field.blockLight(null,0,0,0,2));
    }
    @Test void overlappingLightsUseStrongestAndWorkerSnapshotIsImmutable() {
        Object world=new Object();
        var mutable=new ArrayList<>(List.of(new WorldLightField.Source(0,0,0,10),new WorldLightField.Source(2,0,0,15)));
        var field=new WorldLightField(world,mutable);
        mutable.clear();
        assertEquals(13,field.blockLight(world,0,0,0,0));
        assertEquals(0,new WorldLightField(world,List.of()).blockLight(world,0,0,0,0));
    }
    @Test void placedLightsSurviveLookingAwayButActorSamplesExpire() {
        var sources=new LightRegistry<String,Integer>(64);
        Object block=new Object();
        sources.put(block,"torch",15);
        sources.put(null,"actor",10);
        sources.beginFrame();
        var result=sources.snapshot(owner->true);
        assertEquals(15,result.get("torch"));
        assertFalse(result.containsKey("actor"));
    }
    @Test void editedOrRemovedModelsClearOldLights() {
        var sources=new LightRegistry<String,Integer>(64);
        Object block=new Object();
        sources.put(block,"old-child",15);
        sources.beginPlaced(block);
        assertTrue(sources.snapshot(owner->true).isEmpty());
        sources.put(block,"new-child",8);
        assertEquals(8,sources.snapshot(owner->true).get("new-child"));
        assertTrue(sources.snapshot(owner->false).isEmpty());
    }
    @Test void combinedSourcesStayWithinBudgetAndClearOnWorldChange() {
        var sources=new LightRegistry<String,Integer>(2);
        Object block=new Object();
        sources.put(block,"torch",15);
        sources.put(null,"actor",8);
        sources.put(null,"extra",10);
        assertEquals(2,sources.snapshot(owner->true).size());
        sources.clear();
        assertTrue(sources.snapshot(owner->true).isEmpty());
    }
}
