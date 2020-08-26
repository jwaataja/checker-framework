package org.checkerframework.dataflow.util;

import java.util.HashSet;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.javacutil.BugInCF;

/**
 * A set that is more efficient than HashSet for 0 and 1 elements. Uses {@code Objects.equals} for
 * object comparison and a {@link HashSet} for backing storage.
 */
@SuppressWarnings("determinism") // not type checking collections
public final class MostlySingleton<T extends Object> extends AbstractMostlySingleton<T> {

    /** Create a MostlySingleton. */
    public @OrderNonDet MostlySingleton() {
        super(State.EMPTY);
    }

    /** Create a MostlySingleton. */
    public @OrderNonDet MostlySingleton(T value) {
        super(State.SINGLETON, value);
    }

    @Override
    @SuppressWarnings("fallthrough")
    public @PolyDet("down") boolean add(@PolyDet MostlySingleton<T> this, @PolyDet("use") T e) {
        switch (state) {
            case EMPTY:
                state = State.SINGLETON;
                value = e;
                return true;
            case SINGLETON:
                state = State.ANY;
                set = new HashSet<>();
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

    @Override
    public @PolyDet("down") boolean contains(@PolyDet MostlySingleton<T> this, @PolyDet Object o) {
        switch (state) {
            case EMPTY:
                return false;
            case SINGLETON:
                return Objects.equals(o, value);
            case ANY:
                assert set != null : "@AssumeAssertion(nullness): set initialized before";
                return set.contains(o);
            default:
                throw new BugInCF("Unhandled state " + state);
        }
    }
}
