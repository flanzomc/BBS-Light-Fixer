package dev.bbslight;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

/** Render-thread sources: placed models survive culling; actor samples last one frame. */
final class LightRegistry<K,V> {
    private final int limit;
    private final Map<K,V> frame = new HashMap<>();
    private final Map<Object,Map<K,V>> placed = new IdentityHashMap<>();
    LightRegistry(int limit) { this.limit=limit; }
    void beginFrame() { frame.clear(); }
    void beginPlaced(Object owner) { placed.remove(owner); }
    void put(Object owner,K key,V value) {
        Map<K,V> target=owner==null ? frame : placed.computeIfAbsent(owner,k->new HashMap<>());
        int size=frame.size();
        for(var values:placed.values()) size+=values.size();
        if(target.containsKey(key) || size<limit) target.put(key,value);
    }
    Map<K,V> snapshot(Predicate<Object> validOwner) {
        placed.keySet().removeIf(owner->!validOwner.test(owner));
        Map<K,V> result=new HashMap<>(frame);
        for(var values:placed.values()) result.putAll(values);
        return result;
    }
    void clear() { frame.clear(); placed.clear(); }
}
