package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page object for the YouTube home page (https://www.youtube.com).
 *
 * NOTE on search box: In Chrome 148+, YouTube's search input lives inside a
 * shadow DOM under <ytd-searchbox>. The selector "input#search" cannot pierce
 * shadow roots via regular CSS. We use JavaScript to locate and interact with
 * the input instead, which works regardless of shadow DOM depth.
 */
public class HomePage extends BasePage {

    // Logo — in the light DOM, reliable across versions
    private final By logoLink = By.cssSelector("a#logo, ytd-logo a, #logo a");

    // Video thumbnails on the home feed
    private final By videoThumbnails = By.cssSelector(
            "ytd-rich-item-renderer, ytd-video-renderer, ytd-grid-video-renderer");

    // Cookie / consent dialog dismiss buttons (EU regions)
    private final By rejectCookiesBtn = By.cssSelector(
            "button[aria-label='Reject all'], " +
            "button[aria-label='Reject the use of cookies and other data for the purposes described'], " +
            "tp-yt-paper-button[aria-label*='Reject'], " +
            "ytd-button-renderer button[aria-label*='Reject'], " +
            "button.yt-spec-button-shape-next[aria-label*='Reject']");

    // Sign-in dismiss / "No thanks" for sign-in prompts
    private final By signInDismissBtn = By.cssSelector(
            "button[aria-label='No thanks'], " +
            "ytd-button-renderer:last-child tp-yt-paper-button, " +
            "#dismiss-button");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /** Navigate to YouTube home and dismiss any dialogs that block the page. */
    public HomePage open(String baseUrl) {
        driver.get(baseUrl);
        waitForPageLoad();
        pause(2000); // allow Angular bootstrap and any dialog to appear
        dismissCookieConsent();
        dismissSignInPrompt();
        return this;
    }

    /** Dismiss cookie/consent dialog if present (EU regions). */
    public void dismissCookieConsent() {
        try {
            WebElement btn = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(rejectCookiesBtn));
            btn.click();
            waitForPageLoad();
            pause(1000);
        } catch (Exception ignored) {
            // No consent dialog — proceed normally
        }
    }

    /** Dismiss sign-in prompt if present. */
    public void dismissSignInPrompt() {
        try {
            WebElement btn = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(4))
                    .until(ExpectedConditions.elementToBeClickable(signInDismissBtn));
            btn.click();
            waitForPageLoad();
            pause(1000);
        } catch (Exception ignored) {
            // No sign-in prompt — proceed normally
        }
    }

    @Override
    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isLogoVisible() {
        try {
            return waitForVisible(logoLink).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if the search box is accessible via JavaScript (shadow DOM safe).
     */
    public boolean isSearchBoxVisible() {
        try {
            WebElement input = getSearchInputViaJS();
            return input != null && input.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasVideoThumbnails() {
        try {
            // Give the feed up to 20 s to load — YouTube can be slow
            List<WebElement> items = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(20))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(videoThumbnails));
            return !items.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public int getVideoThumbnailCount() {
        try {
            return driver.findElements(videoThumbnails).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Search using JavaScript to interact with the shadow-DOM search input.
     * Falls back to direct URL navigation if JS interaction fails.
     */
    public SearchResultsPage searchFor(String query) {
        try {
            WebElement input = getSearchInputViaJS();
            if (input != null) {
                input.click();
                pause(300);
                input.clear();
                input.sendKeys(query);
                pause(300);
                input.sendKeys(Keys.ENTER);
                waitForPageLoad();
                waitForAngular();
                return new SearchResultsPage(driver);
            }
        } catch (Exception ignored) {
            // fall through to URL-based search
        }
        // Fallback: navigate directly to search results URL
        String encoded = query.replace(" ", "+");
        driver.get("https://www.youtube.com/results?search_query=" + encoded);
        waitForPageLoad();
        waitForAngular();
        return new SearchResultsPage(driver);
    }

    /**
     * Locate the search input via JavaScript, piercing shadow DOM if needed.
     * YouTube embeds the input inside <ytd-searchbox> shadow root in Chrome 148+.
     */
    private WebElement getSearchInputViaJS() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Try light DOM first (older YouTube versions / some regions)
        Object result = js.executeScript(
                "return document.querySelector('input#search') " +
                "|| document.querySelector('input[name=\"search_query\"]') " +
                "|| (document.querySelector('ytd-searchbox') && " +
                "    document.querySelector('ytd-searchbox').shadowRoot && " +
                "    document.querySelector('ytd-searchbox').shadowRoot.querySelector('input'));");
        if (result instanceof WebElement) {
            return (WebElement) result;
        }
        return null;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
