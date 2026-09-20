package dev.bbslight.cml;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Keeps the effect state that was active when a BBS deferred draw command was queued.
 *
 * <p>Capturing at FormTranslucentQueue.add(...) is intentionally independent of the
 * command constructor signatures. BBS FS has changed those constructors between builds,
 * and constructor injections made the whole client fail during mixin transformation.</p>
 */
public final class DeferredEffectStates
{
    private static final Map<Object, EffectScope.State> STATES =
        Collections.synchronizedMap(new WeakHashMap<>());

    private DeferredEffectStates()
    {}

    public static void capture(Object command)
    {
        if (command != null)
        {
            STATES.put(command, EffectScope.current());
        }
    }

    public static EffectScope.State get(Object command)
    {
        EffectScope.State state = STATES.get(command);

        return state == null ? EffectScope.NEUTRAL : state;
    }

    public static void forget(Object command)
    {
        if (command != null)
        {
            STATES.remove(command);
        }
    }
}
