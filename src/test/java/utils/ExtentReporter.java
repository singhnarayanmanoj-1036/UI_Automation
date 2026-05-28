package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReporter {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    public static void init() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = "reports/ExtentReport_" + timestamp + ".html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("AI Ecommerce Automation Report");
        spark.config().setReportName("Test Execution Report");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    public static void startTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        testThread.set(test);
    }

    public static void log(Status status, String message) {
        if (testThread.get() != null) {
            testThread.get().log(status, message);
        }
    }

    public static void embedScreenshot(String screenshotPath) {
        if (testThread.get() != null) {
            try {
                testThread.get().addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                log(Status.WARNING, "Could not embed screenshot: " + e.getMessage());
            }
        }
    }

    public static void endTest(ITestResult result) {
        if (testThread.get() == null) return;
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                log(Status.PASS, "Test passed");
                break;
            case ITestResult.FAILURE:
                log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
                break;
            case ITestResult.SKIP:
                log(Status.SKIP, "Test skipped");
                break;
        }
        testThread.remove();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
