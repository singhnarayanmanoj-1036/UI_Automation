package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import utils.ExtentReporter;
import utils.ScreenshotUtility;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);
    private static volatile boolean suiteInitialized = false;

    public WebDriver driver;
    public WebDriverWait wait;

    public static final String BASE_URL = loadBaseUrl();

    private static String loadBaseUrl() {
        try {
            java.util.Properties props = new java.util.Properties();
            props.load(new java.io.FileInputStream("testdata/config.properties"));
            return props.getProperty("baseUrl", "https://www.youtube.com");
        } catch (Exception e) {
            return "https://www.youtube.com";
        }
    }

    @BeforeSuite(alwaysRun = true)
    public void initSuite() {
        if (suiteInitialized) return;
        suiteInitialized = true;
        clearScreenshots();
        ExtentReporter.init();
        log.info("Test suite started");
    }

    private void clearScreenshots() {
        java.io.File dir = new java.io.File("screenshots");
        if (dir.exists() && dir.isDirectory()) {
            java.io.File[] files = dir.listFiles(f -> f.isFile() && f.getName().endsWith(".png"));
            if (files != null) {
                for (java.io.File f : files) f.delete();
                log.info("Cleared {} screenshot(s) from previous run", files.length);
            }
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        log.info("Launching {} for: {}", browser, method.getName());

        try {
            switch (browser) {
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;
                default:
                    driver = buildChromeDriver();
            }

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            ExtentReporter.startTest(method.getName());
            log.info("Browser ready for: {}", method.getName());

        } catch (Exception e) {
            log.error("setUp failed for {}: {}", method.getName(), e.getMessage(), e);
            throw e; // rethrow so TestNG marks as FAILED not SKIPPED
        }
    }

    private ChromeDriver buildChromeDriver() {
        ChromeOptions options = new ChromeOptions();

        // Suppress WebDriver detection flags
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--disable-infobars");
        options.addArguments("--lang=en-US,en");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // Realistic user-agent
        options.addArguments(
            "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/148.0.0.0 Safari/537.36"
        );

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        WebDriverManager.chromedriver().setup();
        ChromeDriver chromeDriver = new ChromeDriver(options);

        // CDP stealth: inject JS before every page load to hide navigator.webdriver
        // Uses executeCdpCommand — no versioned import, works with any Chrome version
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("source",
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +
                "Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3,4,5]});" +
                "Object.defineProperty(navigator, 'languages', {get: () => ['en-US','en']});" +
                "window.chrome = {runtime: {}};"
            );
            chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);
            log.info("CDP stealth patch applied");
        } catch (Exception e) {
            log.warn("CDP stealth patch skipped: {}", e.getMessage());
        }

        return chromeDriver;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            try {
                String path = ScreenshotUtility.capture(driver, result.getName());
                if (path != null) ExtentReporter.embedScreenshot(path);
            } catch (Exception e) {
                log.warn("Screenshot failed: {}", e.getMessage());
            }
        }
        ExtentReporter.endTest(result);
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
            wait = null;
            log.info("Browser closed: {}", result.getName());
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        ExtentReporter.flush();
        log.info("Suite finished");
    }
}
