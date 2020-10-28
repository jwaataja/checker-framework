package org.checkerframework.framework.test.junit.wpirunners;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.checkerframework.framework.testchecker.wholeprograminference.WholeProgramInferenceTestChecker;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests whole-program inference with the aid of ajava files. This test is the first pass on the
 * test data, which generates the ajava files.
 *
 * <p>IMPORTANT: The errors captured in the tests located in tests/whole-program-inference/ are not
 * relevant. The meaning of this test class is to test if the generated ajava files are similar to
 * the expected ones. The errors on .java files must be ignored.
 */
@Category(WholeProgramInferenceAjavasTest.class)
public class WholeProgramInferenceAjavasTest extends CheckerFrameworkPerDirectoryTest {

    /** @param testFiles the files containing test code, which will be type-checked */
    public WholeProgramInferenceAjavasTest(List<File> testFiles) {
        super(
                testFiles,
                WholeProgramInferenceTestChecker.class,
                "whole-program-inference/non-annotated",
                "-Anomsgtext",
                "-Ainfer=ajavas");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"whole-program-inference/non-annotated"};
    }
}
