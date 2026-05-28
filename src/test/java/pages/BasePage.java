package pages;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WebDriverWait longWait;  // 20s for slow pages
    private static final Logger log = LogManager.getLogger(BasePage.class);

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    /** Wait for element to be visible */
    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Wait for element to be clickable */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Wait for element with longer timeout */
    protected WebElement waitForVisibleLong(By locator) {
        return longWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Wait for Angular app to finish rendering */
    protected void waitForAngular() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until((ExpectedCondition<Boolean>) d -> {
                JavascriptExecutor js = (JavascriptExecutor) d;
                try {
                    Object result = js.executeScript(
                        "return (typeof angular !== 'undefined') ? " +
                        "angular.element(document.body).injector().get('$http').pendingRequests.length === 0 : true;"
                    );
                    return Boolean.TRUE.equals(result);
                } catch (Exception e) {
                    return true; // not an Angular page or Angular not loaded yet
                }
            });
        } catch (Exception ignored) {}
    }

    /** Wait for page load to complete */
    protected void waitForPageLoad() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) d -> {
                JavascriptExecutor js = (JavascriptExecutor) d;
                return "complete".equals(js.executeScript("return document.readyState"));
            });
        } catch (Exception ignored) {}
    }

    /** Safe sleep — use sparingly, only when Angular needs time to render */
    protected void pause(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }

    protected boolean isElementVisibleAndEnabled(By locator) {
        try {
            WebElement el = waitForVisible(locator);
            return el.isDisplayed() && el.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
