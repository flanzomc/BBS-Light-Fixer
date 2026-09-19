package dev.bbslight;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GlowScopeTest {
    public static class Value { public String get() { return "[glow]"; } }
    public static class Form { public Value name = new Value(); }
    public static class Renderer { public Form form = new Form(); }
    public static class Context { public Object stencilMap; public boolean ui = true; public int light; }
    @Test void glowOnlyDuringTaggedDrawAndNeverPicking() {
        var context=new Context();
        FormBridge.prepare(new Renderer(),context);
        assertTrue(FormBridge.glowing());
        assertEquals(0xf000f0,context.light);
        var picking=new Context(); picking.stencilMap=new Object();
        FormBridge.prepare(new Renderer(),picking);
        assertFalse(FormBridge.glowing());
        FormBridge.finish();
        assertTrue(FormBridge.glowing());
        FormBridge.finish();
        assertFalse(FormBridge.glowing());
    }
}
