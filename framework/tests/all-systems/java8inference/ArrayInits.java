package inference;

import java.util.Arrays;

@SuppressWarnings("determinism")
public class ArrayInits {
    void method() {
        Object[] objects = new Object[] {Arrays.asList(1, 2, 3)};
    }
}
