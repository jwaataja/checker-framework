package hardcoded;

import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.tainting.qual.PolyTainted;
import org.checkerframework.checker.tainting.qual.Tainted;
import org.checkerframework.checker.tainting.qual.Untainted;
import org.checkerframework.framework.qual.HasQualifierParameter;

@HasQualifierParameter(Tainted.class)
public class Buffer {
    final List<@PolyTainted String> list = new ArrayList<>();
    @PolyTainted String someString = "";

    public @PolyTainted Buffer append(@PolyTainted Buffer this, @PolyTainted String s) {
        list.add(s);
        someString = s;
        return this;
    }

    public @PolyTainted String prettyPrint(@PolyTainted Buffer this) {
        String prettyString = "";
        for (String s : list) {
            prettyString += s + " ~~ ";
        }
        return prettyString;
    }

    public @PolyTainted String unTaintedOnly(@Untainted Buffer this, @PolyTainted String s) {
        // :: error: (argument.type.incompatible)
        list.add(s);
        // :: error: (assignment.type.incompatible)
        someString = s;
        return s;
    }
}

class Use {
    void passingUses(@Untainted String untainted, @Untainted Buffer buffer) {
        buffer.list.add(untainted);
        buffer.someString = untainted;
        buffer.append(untainted);
    }

    void failingUses(@Tainted String tainted, @Untainted Buffer buffer) {
        // :: error: (argument.type.incompatible)
        buffer.list.add(tainted);
        // :: error: (assignment.type.incompatible)
        buffer.someString = tainted;
        // TODO: Should be argument type incompatible.
        // :: error: (method.invocation.invalid)
        buffer.append(tainted);
    }

    void casts(@Untainted Object untainted, @Tainted Object tainted) {
        @Untainted Buffer b1 = (@Untainted Buffer) untainted; // ok
        // :: warning: (invariant.cast)
        @Untainted Buffer b2 = (@Untainted Buffer) tainted;

        // :: warning: (invariant.cast)
        @Tainted Buffer b3 = (@Tainted Buffer) untainted; // error
        // :: warning: (invariant.cast)
        @Tainted Buffer b4 = (@Tainted Buffer) tainted; // error

        @Untainted Buffer b5 = (Buffer) untainted; // ok
        // :: warning: (invariant.cast)
        @Tainted Buffer b6 = (Buffer) tainted;
    }
}
