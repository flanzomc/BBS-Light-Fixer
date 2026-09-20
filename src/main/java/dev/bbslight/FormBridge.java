package dev.bbslight;

import net.minecraft.client.util.math.MatrixStack;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public final class FormBridge {
    private record Member(Class<?> owner, String name) {}
    private static final ConcurrentHashMap<Member, Field> FIELDS=new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Method> GETTERS=new ConcurrentHashMap<>();
    private static boolean warned;
    private static final ThreadLocal<java.util.Deque<Boolean>> GLOW = ThreadLocal.withInitial(java.util.ArrayDeque::new);
    public static boolean glowing() { return !GLOW.get().isEmpty() && GLOW.get().peek(); }
    public static void finish() { if(!GLOW.get().isEmpty()) GLOW.get().pop(); }
    private FormBridge() {}
    public static void prepare(Object renderer, Object context) {
        GLOW.get().push(false);
        if(!LightFixer.enabled) return;
        try {
            Object form=field(renderer,"form");
            Object name=field(form,"name");
            Method get=GETTERS.computeIfAbsent(name.getClass(),c->{try { return c.getMethod("get"); } catch(Exception e) { throw new IllegalStateException(e); }});
            LightSpec spec=LightSpec.parse(String.valueOf(get.invoke(name)));
            if(spec.level()==0 && !spec.glow()) return;
            if(field(context,"stencilMap")!=null) return;
            if(spec.glow()) {
                lookup(context.getClass(),"light").setInt(context,0x00f000f0);
                GLOW.get().pop(); GLOW.get().push(true);
            }
            if(!Boolean.TRUE.equals(field(context,"ui"))) {
                MatrixStack stack=(MatrixStack)field(context,"stack");
                ModelBlockAnchor.Position anchored=ModelBlockAnchor.resolve(stack.peek().getPositionMatrix());
                if(anchored!=null) Lights.capture(form,ModelBlockAnchor.owner(),anchored.x(),anchored.y(),anchored.z(),spec.level());
                else Lights.capture(form,field(context,"entity"),(MatrixStack)field(context,"world"),spec.level());
            }
        } catch(ReflectiveOperationException | RuntimeException e) {
            if(!warned) { warned=true; LoggerFactory.getLogger("BBS Light Fixer").warn("BBS form lighting hook is incompatible with this build",e); }
        }
    }
    private static Object field(Object o,String name) throws ReflectiveOperationException { return lookup(o.getClass(),name).get(o); }
    private static Field lookup(Class<?> c,String name) throws NoSuchFieldException {
        Member key=new Member(c,name); Field cached=FIELDS.get(key); if(cached!=null)return cached;
        for(Class<?> type=c;type!=null;type=type.getSuperclass()) {
            try { Field f=type.getDeclaredField(name); f.setAccessible(true); FIELDS.put(key,f); return f; }
            catch(NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException(c.getName()+"."+name);
    }
}
