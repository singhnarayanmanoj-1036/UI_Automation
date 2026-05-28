package tests;

import base.BaseTest;
import models.ProductDetails;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductPage;
import pages.SearchPage;
import utils.RetryAnalyzer;
import utils.TestDataProvider;

import java.util.List;

public class ProductTest extends BaseTest {

    private final TestDataProvider testData = new TestDataProvider();
    private static final String BASE_URL = "https://advantageonlineshopping.com";

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Category page should display a list of products")
    public void testCategoryDisplaysProducts() {
        // Navigate to Tablets category (category id 2 on AOS)
        driver.get(BASE_URL + "/#/category/2");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        ProductPage productPage = new ProductPage(driver);
        List<String> products = productPage.getCategoryProducts();
        Assert.assertTrue(!products.isEmpty(), "Category page should display at least one product");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Selected product should display all required details")
    public void testProductDetailsDisplayed() {
        driver.get(BASE_URL);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor(testData.getData("searchTermExisting"));
        searchPage.clickResult(testData.getData("searchTermExisting"));
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        ProductPage productPage = new ProductPage(driver);
        ProductDetails details = productPage.getProductDetails();

        Assert.assertNotNull(details.getName(), "Product name should not be null");
        Assert.assertFalse(details.getName().isEmpty(), "Product name should not be empty");
        Assert.assertTrue(details.getPrice() > 0, "Product price should be greater than 0");
    }
}
