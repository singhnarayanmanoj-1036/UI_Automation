package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.RetryAnalyzer;
import utils.TestDataProvider;

/**
 * Tests for YouTube search functionality.
 * 5 test cases covering search results, URL, titles, channel names, and re-search.
 */
public class SearchTest extends BaseTest {

    private final TestDataProvider testData = new TestDataProvider();

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Searching for an existing term should return results")
    public void testSearchReturnsResults() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        SearchResultsPage results = home.searchFor(testData.getData("searchTermExisting"));
        Assert.assertTrue(results.hasResults(),
                "Search should return results for: " + testData.getData("searchTermExisting"));
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Search results URL should contain the search query parameter")
    public void testSearchResultsUrl() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        SearchResultsPage results = home.searchFor(testData.getData("searchTermExisting"));
        Assert.assertTrue(results.urlContainsSearchQuery(testData.getData("searchTermExisting")),
                "Results URL should contain search_query parameter");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Search results page title should contain the search term")
    public void testSearchResultsPageTitle() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        String query = testData.getData("searchTermExisting");
        SearchResultsPage results = home.searchFor(query);
        String title = results.getPageTitle();
        Assert.assertTrue(title.toLowerCase().contains(query.split(" ")[0].toLowerCase()),
                "Page title should contain part of the search term. Title was: " + title);
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Search results should display channel names")
    public void testSearchResultsHaveChannelNames() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        SearchResultsPage results = home.searchFor(testData.getData("searchTermTrending"));
        Assert.assertTrue(results.hasChannelNames(),
                "Search results should display channel names");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Searching a second term from results page should update results")
    public void testSearchFromResultsPage() {
        HomePage home = new HomePage(driver);
        home.open(BASE_URL);
        SearchResultsPage results = home.searchFor(testData.getData("searchTermExisting"));
        Assert.assertTrue(results.hasResults(), "First search should return results");

        results.searchFor(testData.getData("searchTermMusic"));
        Assert.assertTrue(results.hasResults(),
                "Second search from results page should also return results");
    }
}
