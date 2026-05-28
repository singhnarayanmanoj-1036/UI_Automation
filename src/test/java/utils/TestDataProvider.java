package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TestDataProvider {

    private static final Logger log = LogManager.getLogger(TestDataProvider.class);
    private static final String DATA_FILE = "testdata/testdata.json";
    private final Map<String, String> data;

    public TestDataProvider() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.readValue(new File(DATA_FILE), new TypeReference<Map<String, String>>() {});
            log.info("Test data loaded from {}", DATA_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data from: " + DATA_FILE, e);
        }
    }

    public String getData(String key) {
        if (!data.containsKey(key)) {
            throw new TestDataNotFoundException(key, DATA_FILE);
        }
        return data.get(key);
    }

    public boolean hasKey(String key) {
        return data.containsKey(key);
    }

    @org.testng.annotations.DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][] {
            { getData("validUsername"), getData("validPassword") },
            { getData("invalidUsername"), getData("invalidPassword") }
        };
    }
}
