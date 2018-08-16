package determinism;

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class TestArrays {
    // Tests whether array parameters have correct defaults when passed.
    void testArrParam(@PolyDet int @PolyDet [] arr) {
        int[] a = new int[] {10};
        takeArr(arr);
    }

    // Tests whether array return values have correct defaults.
    void testArrRet() {
        // :: error: (assignment.type.incompatible)
        @Det int i = returnArr()[0];
        @Det int j = returnArrExplicit()[0];
    }
    // Tests whether returned arrays have correct defaults within methods.
    int[] testArrRetInternal() {
        @PolyDet int @PolyDet [] arr = new @PolyDet int @PolyDet [0];
        // This should fail if @Det int [] @PolyDet is expected, which it
        // should't be.
        return new @PolyDet int @PolyDet [0];
    }

    int[] returnArr() {
        return new @PolyDet int @PolyDet [] {0};
    }

    @Det
    int @Det [] returnArrExplicit() {
        return new @Det int @Det [] {0};
    }

    void takeArr(int[] a) {}

    void testArr(int[] a) {
        // :: error: (assignment.type.incompatible)
        @Det int i = a[0];
    }

    void testArr1(@Det int @Det [] a) {
        @Det int i = a[0];
        @Det String str = "rash";
    }

    void testArrayLength(@Det int @Det [] a) {
        System.out.println(a.length);
    }

    void testArrayLength1(@Det int @NonDet [] a) {
        // :: error: (argument.type.incompatible)
        System.out.println(a.length);
    }

    void testArrayLength2(@Det int @OrderNonDet [] a) {
        System.out.println(a.length);
    }
}
