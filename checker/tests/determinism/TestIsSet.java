import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

// :: error: (invalid.upper.bound.on.type.argument) :: error: (super.invocation.invalid) :: error:
// (missing.has.qual.param)
public class TestIsSet<Integer> extends HashSet<Integer> {
    void test(@OrderNonDet TestIsSet<@Det Integer> a, @OrderNonDet HashSet<@Det Integer> h) {
        boolean res = a.equals(h);
        System.out.println(res);
    }
}
