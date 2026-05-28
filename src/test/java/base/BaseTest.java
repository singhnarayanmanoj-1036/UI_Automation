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

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // Public so TestNGListener can access the driver for screenshots
    public WebDriver driver;
    public WebDriverWait wait;

    @BeforeSuite(alwaysRun = true)
    public void initSuite() {
        // Clear screenshots folder before each run so only current run's screenshots remain
        clearScreenshots();
        ExtentReporter.init();
        log.info("Test suite started");
    }

    private void clearScreenshots() {
        java.io.File dir = new java.io.File("screenshots");
        if (dir.exists() && dir.isDirectory()) {
            java.io.File[] files = dir.listFiles(
                    f -> f.isFile() && f.getName().endsWith(".png"));
            if (files != null) {
                for (java.io.File f : files) {
                    f.delete();
                }
                log.info("Cleared {} screenshot(s) from previous run", files.length);
            }
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        log.info("Launching {} for: {}", browser, method.getName());

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
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications", "--disable-popup-blocking");
                if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
                    options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
                }
                driver = new ChromeDriver(options);
        }

        driver.manage().window().maximize();
        // Implicit wait as safety net — explicit waits in page objects take priority
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        ExtentReporter.startTest(method.getName());
        log.info("Browser ready for: {}", method.getName());
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
