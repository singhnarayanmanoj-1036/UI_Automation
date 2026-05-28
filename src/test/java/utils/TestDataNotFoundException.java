package utils;

/**
 * Exception thrown by {@link TestDataProvider#getData(String)} when the requested key
 * is absent from the active test data file.
 *
 * <p>Surfaces immediately at test setup rather than causing a {@link NullPointerException}
 * mid-test, making failures easier to diagnose.</p>
 */
public class TestDataNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code TestDataNotFoundException} with a descriptive message
     * that includes both the missing key and the file path that was searched.
     *
     * @param key      the test data key that was not found
     * @param filePath the path of the data file that was searched
     */
    public TestDataNotFoundException(String key, String filePath) {
        super("Test data key '" + key + "' not found in file: " + filePath);
    }
}
