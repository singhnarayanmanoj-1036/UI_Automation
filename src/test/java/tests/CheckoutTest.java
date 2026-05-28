package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.ProductPage;
import pages.SearchPage;
import utils.RetryAnalyzer;
import utils.TestDataProvider;

public class CheckoutTest extends BaseTest {

    private final TestDataProvider testData = new TestDataProvider();
    private static final String BASE_URL = "https://advantageonlineshopping.com";

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Proceeding to checkout should show the order summary")
    public void testProceedToCheckoutShowsOrderSummary() {
        driver.get(BASE_URL);

        // Add a product to cart
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor(testData.getData("searchTermExisting"));
        searchPage.clickResult(testData.getData("searchTermExisting"));

        ProductPage productPage = new ProductPage(driver);
        productPage.addToCart();

        // Go to cart and proceed to checkout
        CartPage cartPage = new CartPage(driver);
        cartPage.openCart();
        cartPage.proceedToCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        Assert.assertTrue(checkoutPage.isCheckoutPageLoaded(), "Checkout page should load successfully");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Checkout page should have all required form sections visible")
    public void testCheckoutPageLoaded() {
        driver.get(BASE_URL);

        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor(testData.getData("searchTermExisting"));
        searchPage.clickResult(testData.getData("searchTermExisting"));

        new ProductPage(driver).addToCart();

        CartPage cartPage = new CartPage(driver);
        cartPage.openCart();
        cartPage.proceedToCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        Assert.assertTrue(checkoutPage.isCheckoutPageLoaded(),
                "All required checkout form sections should be visible");
    }
}
