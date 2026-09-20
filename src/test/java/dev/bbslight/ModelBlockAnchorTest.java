package dev.bbslight;

import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelBlockAnchorTest {
    @Test void placedBlockKeepsWorldPositionThroughCameraAndBoneTransforms() {
        Matrix4f entry=new Matrix4f().rotateX(.6f).rotateY(-1.2f).translate(12,-7,29);
        ModelBlockAnchor.begin(entry,10000,80,-20000);
        try {
            Matrix4f child=new Matrix4f(entry).translate(.5f,0,.5f)
                .translate(2,3,4).rotateY((float)Math.PI/2).translate(0,0,2);
            var p=ModelBlockAnchor.resolve(child);
            assertEquals(10004.5,p.x(),.0001);
            assertEquals(83,p.y(),.0001);
            assertEquals(-19995.5,p.z(),.0001);
        } finally { ModelBlockAnchor.end(); }
        assertNull(ModelBlockAnchor.resolve(entry));
    }
    @Test void nestedRenderRestoresParentAnchor() {
        Matrix4f entry=new Matrix4f();
        ModelBlockAnchor.begin(entry,1,2,3);
        try {
            ModelBlockAnchor.begin(entry,9,8,7);
            try { assertEquals(9,ModelBlockAnchor.resolve(entry).x()); }
            finally { ModelBlockAnchor.end(); }
            assertEquals(1,ModelBlockAnchor.resolve(entry).x());
        } finally { ModelBlockAnchor.end(); }
    }
}
