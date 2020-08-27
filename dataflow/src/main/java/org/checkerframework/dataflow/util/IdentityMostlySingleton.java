package org.checkerframework.dataflow.util;

import java.util.Collections;
import java.util.IdentityHashMap;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.interning.qual.FindDistinct;
import org.checkerframework.javacutil.BugInCF;

/**
 * An arbitrary-size set that is very efficient (more efficient than HashSet) for 0 and 1 elements.
 * Uses object identity for object comparison.
 */
@SuppressWarnings("determinism") // not type checking this collection
public final class IdentityMostlySingleton<T extends Object> extends AbstractMostlySingleton<T> {

    /** Create an IdentityMostlySingleton. */
    public @OrderNonDet IdentityMostlySingleton() {
        super(State.EMPTY);
    }

    /** Create an IdentityMostlySingleton. */
    public @OrderNonDet IdentityMostlySingleton(T value) {
        super(State.SINGLETON, value);
    }

    @Override
    @SuppressWarnings("fallthrough")
    public @PolyDet("down") boolean add(
            @PolyDet IdentityMostlySingleton<T> this, @PolyDet @FindDistinct T e) {
        switch (state) {
            case EMPTY:
                state = State.SINGLETON;
                value = e;
                return true;
            case SINGLETON:
                if (value == e) {
                    return false;
                }
                state = State.ANY;
                set = Collections.newSetFromMap(new IdentityHashMap<>());
                assert value != null : "@AssumeAssertion(nullness): previous add is non-null";
                set.add(value);
                value = null;
                // fallthrough
            case ANY:
                assert set != null : "@AssumeAssertion(nullness): set initialized before";
                return set.add(e);
            default:
                throw new BugInCF("Unhandled state " + state);
        }
    }

    @SuppressWarnings("interning:not.interned") // this class uses object identity
    @Override
    public @PolyDet("down") boolean contains(
            @PolyDet IdentityMostlySingleton<T> this, @PolyDet Object o) {
        switch (state) {
            case EMPTY:
                return false;
            case SINGLETON:
                return o == value;
            case ANY:
                assert set != null : "@AssumeAssertion(nullness): set initialized before";
                return set.contains(o);
            default:
                throw new BugInCF("Unhandled state " + state);
        }
    }
}
