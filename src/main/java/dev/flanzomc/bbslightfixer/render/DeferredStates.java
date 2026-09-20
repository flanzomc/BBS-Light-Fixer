package dev.flanzomc.bbslightfixer.render;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
public final class DeferredStates{
 private static final Map<Object,EffectState> STATES=Collections.synchronizedMap(new WeakHashMap<>());
 private DeferredStates(){}
 public static void capture(Object command){EffectState s=EffectRenderContext.current();if(command!=null&&s!=null)STATES.put(command,s.copy());}
 public static EffectState get(Object command){return STATES.get(command);}
 public static void forget(Object command){if(command!=null)STATES.remove(command);}
}
