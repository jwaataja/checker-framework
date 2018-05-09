import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class MissingBoundAnnotations {
    @SuppressWarnings({"nullness:type.argument.type.incompatible", "determinism"})
    public static <K extends Comparable<? super K>, V> Collection<K> sortedKeySet(Map<K, V> m) {
        ArrayList<K> theKeys = new ArrayList<K>(m.keySet());
        Collections.sort(theKeys);
        return theKeys;
    }
}
