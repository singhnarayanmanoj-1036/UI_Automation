package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.SearchPage;
import utils.RetryAnalyzer;
import utils.TestDataProvider;

public class SearchTest extends BaseTest {

    private final TestDataProvider testData = new TestDataProvider();
    private static final String BASE_URL = "https://advantageonlineshopping.com";

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Search for an existing product should return results")
    public void testSearchExistingProduct() {
        driver.get(BASE_URL);
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor(testData.getData("searchTermExisting"));
        Assert.assertTrue(searchPage.hasResults(), "Search should return results for existing product");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Search for non-existent product should show no-results message")
    public void testSearchNonExistentProduct() {
        driver.get(BASE_URL);
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor(testData.getData("searchTermNonExistent"));
        Assert.assertFalse(searchPage.hasResults(), "Search should return no results for non-existent product");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Clicking a search result should navigate to the product page")
    public void testClickSearchResult() {
        driver.get(BASE_URL);
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor(testData.getData("searchTermExisting"));
        Assert.assertTrue(searchPage.hasResults(), "Results must exist before clicking");
        searchPage.clickResult(testData.getData("searchTermExisting"));
        Assert.assertFalse(driver.getTitle().isEmpty(), "Product page should have a title");
    }
}
