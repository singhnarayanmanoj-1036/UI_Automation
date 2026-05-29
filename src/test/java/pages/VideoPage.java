package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page object for a YouTube video watch page (/watch?v=...).
 */
public class VideoPage extends BasePage {

    // The HTML5 video element — present on all watch pages
    private final By videoPlayer = By.cssSelector("video.html5-main-video, video");

    // Video title — try multiple selectors for resilience across YouTube versions
    private final By videoTitle = By.cssSelector(
            "h1.ytd-watch-metadata yt-formatted-string, " +
            "h1#title yt-formatted-string, " +
            "ytd-watch-metadata h1 yt-formatted-string, " +
            "#title h1 yt-formatted-string, " +
            "h1.style-scope.ytd-watch-metadata");

    // Channel name
    private final By channelName = By.cssSelector(
            "ytd-channel-name #channel-name a, " +
            "#owner #channel-name a, " +
            "ytd-video-owner-renderer ytd-channel-name a, " +
            "#channel-name a.yt-simple-endpoint");

    // Related videos in the sidebar
    private final By relatedVideos = By.cssSelector(
            "ytd-compact-video-renderer, ytd-watch-next-secondary-results-renderer ytd-compact-video-renderer");

    // Player controls
    private final By progressBar = By.cssSelector("div.ytp-progress-bar, .ytp-progress-bar-container");
    private final By muteButton  = By.cssSelector("button.ytp-mute-button");

    public VideoPage(WebDriver driver) {
        super(driver);
    }

    public boolean isVideoPlayerVisible() {
        try {
            // Give the player up to 20 s to appear
            WebElement player = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(20))
                    .until(ExpectedConditions.presenceOfElementLocated(videoPlayer));
            return player.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getVideoTitle() {
        try {
            // Title can take a moment to render after player loads
            WebElement el = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(20))
                    .until(ExpectedConditions.visibilityOfElementLocated(videoTitle));
            String text = el.getText();
            if (text == null || text.isEmpty()) {
                // Fallback: read via JavaScript innerText
                text = (String) ((JavascriptExecutor) driver).executeScript(
                        "var el = document.querySelector('h1.ytd-watch-metadata yt-formatted-string, h1#title yt-formatted-string'); " +
                        "return el ? el.innerText : '';");
            }
            return text == null ? "" : text.trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isVideoTitleVisible() {
        return !getVideoTitle().isEmpty();
    }

    public String getChannelName() {
        try {
            WebElement el = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(channelName));
            return el.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isChannelNameVisible() {
        return !getChannelName().isEmpty();
    }

    public boolean hasRelatedVideos() {
        try {
            List<WebElement> related = new org.openqa.selenium.support.ui.WebDriverWait(driver,
                    java.time.Duration.ofSeconds(20))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(relatedVideos));
            return !related.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isProgressBarVisible() {
        try {
            return waitForVisible(progressBar).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMuteButtonVisible() {
        try {
            return waitForVisible(muteButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean urlContainsWatch() {
        return driver.getCurrentUrl().contains("/watch");
    }

    /** Pause the video via JavaScript to prevent autoplay interference during assertions. */
    public void pauseVideo() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "var v = document.querySelector('video'); if(v) { v.pause(); v.currentTime = 0; }");
        } catch (Exception ignored) {}
    }
}
