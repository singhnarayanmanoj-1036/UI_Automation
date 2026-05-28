package listeners;

import com.aventstack.extentreports.Status;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentReporter;
import utils.ScreenshotUtility;

import java.io.ByteArrayInputStream;

public class TestNGListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestNGListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        log.info("Test started: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Test passed: {}", result.getName());
        ExtentReporter.log(Status.PASS, "Test passed: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String msg = result.getThrowable() != null
                ? result.getThrowable().getMessage() : "Test failed";
        log.error("Test failed: {} — {}", result.getName(), msg);

        WebDriver driver = getDriver(result);

        // Capture screenshot for ExtentReports
        if (driver != null) {
            String path = ScreenshotUtility.capture(driver, result.getName());
            if (path != null) {
                ExtentReporter.embedScreenshot(path);
            }

            // Attach screenshot to Allure report
            try {
                byte[] screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(
                        result.getName() + "_failure",
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png"
                );
            } catch (Exception e) {
                log.warn("Allure screenshot attachment failed: {}", e.getMessage());
            }
        }

        ExtentReporter.log(Status.FAIL, "Test failed: " + msg);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("Test skipped: {}", result.getName());
        ExtentReporter.log(Status.SKIP, "Test skipped: " + result.getName());
    }

    private WebDriver getDriver(ITestResult result) {
        try {
            Object instance = result.getInstance();
            if (instance instanceof base.BaseTest) {
                return ((base.BaseTest) instance).driver;
            }
        } catch (Exception e) {
            log.warn("Could not get driver from test instance: {}", e.getMessage());
        }
        return null;
    }
}
