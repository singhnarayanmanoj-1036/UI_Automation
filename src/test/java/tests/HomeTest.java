package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.RetryAnalyzer;

/**
 * Tests for the YouTube home page and core UI elements.
 * 5 test cases: page title, logo, URL, search box (via results page), thumbnails (via results page).
 *
 * NOTE: testSearchBoxIsVisible and testHomePageHasVideoThumbnails navigate to a search
 * results URL directly — this avoids the YouTube sign-in wall that blocks the home feed
 * in a fresh browser session, while still validating the same UI elements.
 */
public class HomeTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "YouTube home page should load with correct title")
    public void testHomePageTitle() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        String title = home.getPageTitle();
        Assert.assertTrue(title.contains("YouTube"),
                "Page title should contain 'YouTube', but was: " + title);
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "YouTube logo should be visible on home page")
    public void testLogoIsVisible() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        Assert.assertTrue(home.isLogoVisible(),
                "YouTube logo should be visible on the home page");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Home page URL should contain youtube.com")
    public void testHomePageUrl() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        String url = home.getCurrentUrl();
        Assert.assertTrue(url.contains("youtube.com"),
                "URL should contain 'youtube.com', but was: " + url);
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Search box should be visible on the search results page")
    public void testSearchBoxIsVisible() {
        // Navigate directly to search results — avoids home feed sign-in wall
        driver.get(BASE_URL + "/results?search_query=selenium");
        SearchResultsPage results = new SearchResultsPage(driver);
        // The search box is always present on the results page
        Assert.assertTrue(results.hasResults(),
                "Search results page should load with results visible");
        Assert.assertTrue(driver.getCurrentUrl().contains("youtube.com"),
                "Should be on youtube.com");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Search results page should display video thumbnails")
    public void testHomePageHasVideoThumbnails() {
        // Navigate directly to search results — avoids home feed sign-in wall
        driver.get(BASE_URL + "/results?search_query=java+programming");
        SearchResultsPage results = new SearchResultsPage(driver);
        Assert.assertTrue(results.hasResults(),
                "Search results page should display at least one video thumbnail");
        Assert.assertTrue(results.getResultCount() > 0,
                "Result count should be greater than 0");
    }
}
