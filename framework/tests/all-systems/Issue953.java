// Test case for issue 953
// https://github.com/typetools/checker-framework/issues/953

import java.util.List;

class Issue953 {
    class MyCollector<A, B, C> {}

    class MyStream<E> {
        <F, G> F collect(MyCollector<? super E, G, F> param) {
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("determinism")
    public static void test(MyStream<Integer> y) {
        // Type argument inference fails, so a checker may report a type checking error.
        @SuppressWarnings("all")
        List<Integer> counts = y.collect(toList());
    }

    @SuppressWarnings("determinism")
    static <H> MyCollector<H, ?, List<H>> toList() {
        throw new RuntimeException();
    }
}
