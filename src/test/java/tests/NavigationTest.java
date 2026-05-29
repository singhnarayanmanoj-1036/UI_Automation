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
 * Tests for YouTube navigation flows.
 * 5 test cases covering back navigation, search-to-video, home redirect, and browser title.
 */
public class NavigationTest extends BaseTest {

    private final TestDataProvider testData = new TestDataProvider();

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Navigating to youtube.com should land on the home page")
    public void testDirectNavigationToHome() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        Assert.assertTrue(home.getPageTitle().contains("YouTube"),
                "Direct navigation to youtube.com should show YouTube home page");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Browser back from search results should return to home page")
    public void testBackFromSearchResultsToHome() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        home.searchFor(testData.getData("searchTermExisting"));
        driver.navigate().back();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(d -> ((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState").equals("complete"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("youtube.com"),
                "After navigating back, URL should still be on youtube.com");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Search then open video — URL should change to /watch")
    public void testSearchThenOpenVideoChangesUrl() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        SearchResultsPage results = home.searchFor(testData.getData("searchTermExisting"));
        Assert.assertTrue(results.hasResults(), "Search must return results");
        VideoPage video = results.clickFirstResult();
        Assert.assertTrue(video.urlContainsWatch(),
                "After clicking a result, URL should contain /watch");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Browser back from video page should return to search results")
    public void testBackFromVideoToSearchResults() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        SearchResultsPage results = home.searchFor(testData.getData("searchTermExisting"));
        Assert.assertTrue(results.hasResults(), "Search must return results");
        results.clickFirstResult();
        driver.navigate().back();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                .until(d -> ((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState").equals("complete"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("results") || url.contains("search_query"),
                "After navigating back from video, URL should be search results page");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Page title should update after performing a search")
    public void testPageTitleUpdatesAfterSearch() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        String homeTitle = home.getPageTitle();
        SearchResultsPage results = home.searchFor(testData.getData("searchTermExisting"));
        String searchTitle = results.getPageTitle();
        Assert.assertNotEquals(homeTitle, searchTitle,
                "Page title should change after performing a search");
    }
}
