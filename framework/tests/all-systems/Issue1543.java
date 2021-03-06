// Test case for Issue 1543
// https://github.com/typetools/checker-framework/issues/1543
@SuppressWarnings("all") // check for crashes only
public class Issue1543 {
  static class BClass<T> {}

  interface AInterface<T> {}

  static class GClass<T extends BClass<?> & AInterface<?>> {}

  static class Test {
    GClass gClassRaw;
    GClass<?> gClassWC;
  }
}
