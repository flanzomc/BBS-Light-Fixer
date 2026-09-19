package dev.bbslight;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class LightSpecTest {
 @Test void tags() { assertEquals(new LightSpec(15,true),LightSpec.parse("Torch [LIGHT=99] [glow]")); assertEquals(0,LightSpec.parse("normal").level()); assertEquals(0,LightSpec.parse("[light=no]").level()); }
 @Test void falloff() { assertEquals(15,LightSpec.intensity(0,0,0,15,0,0,0)); assertEquals(10,LightSpec.intensity(0,0,0,15,3,4,0)); assertEquals(0,LightSpec.intensity(0,0,0,15,20,0,0)); }
 @Test void preserveSkyAndBrighterVanilla() { assertEquals(0x00b000f0,LightSpec.merge(0x00b00020,15)); assertEquals(0x00a000e0,LightSpec.merge(0x00a000e0,3)); }
}
