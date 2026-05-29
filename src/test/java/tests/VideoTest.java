package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchResultsPage;
import pages.VideoPage;
import utils.RetryAnalyzer;
import utils.TestDataProvider;

/**
 * Tests for YouTube video watch page.
 * 5 test cases covering player, title, channel, related videos, and URL.
 */
public class VideoTest extends BaseTest {

    private final TestDataProvider testData = new TestDataProvider();

    private VideoPage openFirstVideoFromSearch() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        SearchResultsPage results = home.searchFor(testData.getData("searchTermExisting"));
        Assert.assertTrue(results.hasResults(), "Search must return results before opening a video");
        return results.clickFirstResult();
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Video player should be visible on the watch page")
    public void testVideoPlayerIsVisible() {
        VideoPage video = openFirstVideoFromSearch();
        Assert.assertTrue(video.isVideoPlayerVisible(),
                "Video player should be visible on the watch page");
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Video watch page URL should contain /watch")
    public void testVideoPageUrl() {
        VideoPage video = openFirstVideoFromSearch();
        Assert.assertTrue(video.urlContainsWatch(),
                "Video page URL should contain '/watch', but was: " + video.getCurrentUrl());
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Video title should be visible on the watch page")
    public void testVideoTitleIsVisible() {
        VideoPage video = openFirstVideoFromSearch();
        video.pauseVideo();
        Assert.assertTrue(video.isVideoTitleVisible(),
                "Video title should be visible on the watch page");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Channel name should be visible on the watch page")
    public void testChannelNameIsVisible() {
        VideoPage video = openFirstVideoFromSearch();
        video.pauseVideo();
        Assert.assertTrue(video.isChannelNameVisible(),
                "Channel name should be visible on the watch page");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Related videos should appear on the watch page")
    public void testRelatedVideosAreShown() {
        VideoPage video = openFirstVideoFromSearch();
        video.pauseVideo();
        Assert.assertTrue(video.hasRelatedVideos(),
                "Related videos should be displayed on the watch page");
    }
}
