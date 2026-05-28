package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.ProductPage;
import pages.SearchPage;
import utils.RetryAnalyzer;
import utils.TestDataProvider;

public class CartTest extends BaseTest {

    private final TestDataProvider testData = new TestDataProvider();
    private static final String BASE_URL = "https://advantageonlineshopping.com";

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Adding a product to cart should increment the cart count")
    public void testAddToCartIncrementsCount() {
        driver.get(BASE_URL);
        CartPage cartPage = new CartPage(driver);
        int countBefore = cartPage.getCartItemCount();

        // Search and add product
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor(testData.getData("searchTermExisting"));
        searchPage.clickResult(testData.getData("searchTermExisting"));

        ProductPage productPage = new ProductPage(driver);
        productPage.addToCart();

        int countAfter = cartPage.getCartItemCount();
        Assert.assertTrue(countAfter > countBefore, "Cart count should increase after adding a product");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Cart should display the correct item after adding a product")
    public void testCartDisplaysCorrectItem() {
        driver.get(BASE_URL);
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor(testData.getData("searchTermExisting"));
        searchPage.clickResult(testData.getData("searchTermExisting"));

        ProductPage productPage = new ProductPage(driver);
        productPage.addToCart();

        CartPage cartPage = new CartPage(driver);
        cartPage.openCart();

        Assert.assertFalse(cartPage.getCartItems().isEmpty(), "Cart should contain at least one item");
    }
}
