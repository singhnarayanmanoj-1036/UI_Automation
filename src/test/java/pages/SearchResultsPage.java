package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page object for YouTube search results page (/results?search_query=...).
 *
 * NOTE: The search input on the results page is also inside a shadow DOM in
 * Chrome 148+. We use JavaScript to interact with it, same as HomePage.
 */
public class SearchResultsPage extends BasePage {

    // Video result containers — reliable across YouTube versions
    private final By videoResults = By.cssSelector("ytd-video-renderer");
    private final By videoTitles  = By.cssSelector("ytd-video-renderer #video-title");
    private final By channelNames = By.cssSelector("ytd-video-renderer ytd-channel-name yt-formatted-string");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean hasResults() {
        try {
            List<WebElement> results = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(20))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(videoResults));
            return !results.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public int getResultCount() {
        try {
            return driver.findElements(videoResults).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getFirstVideoTitle() {
        try {
            List<WebElement> titles = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(20))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(videoTitles));
            return titles.isEmpty() ? "" : titles.get(0).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean firstResultTitleContains(String keyword) {
        return getFirstVideoTitle().toLowerCase().contains(keyword.toLowerCase());
    }

    public VideoPage clickFirstResult() {
        List<WebElement> titles = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                java.time.Duration.ofSeconds(20))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(videoTitles));
        if (titles.isEmpty()) throw new RuntimeException("No search results to click");
        titles.get(0).click();
        waitForPageLoad();
        waitForAngular();
        pause(2000); // allow video player to initialise
        return new VideoPage(driver);
    }

    @Override
    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean urlContainsSearchQuery(String query) {
        String url = driver.getCurrentUrl();
        return url.contains("search_query=") || url.contains("/results");
    }

    /**
     * Re-search from the results page using JavaScript to reach the shadow-DOM input.
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
                return this;
            }
        } catch (Exception ignored) {
            // fall through to URL-based search
        }
        // Fallback: navigate directly to search results URL
        String encoded = query.replace(" ", "+");
        driver.get("https://www.youtube.com/results?search_query=" + encoded);
        waitForPageLoad();
        waitForAngular();
        return this;
    }

    public boolean hasChannelNames() {
        try {
            List<WebElement> channels = driver.findElements(channelNames);
            // Filter to non-empty text only
            return channels.stream().anyMatch(e -> {
                try { return !e.getText().isEmpty(); } catch (Exception ex) { return false; }
            });
        } catch (Exception e) {
            return false;
        }
    }

    /** Locate the search input via JavaScript, piercing shadow DOM if needed. */
    private WebElement getSearchInputViaJS() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
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
}
