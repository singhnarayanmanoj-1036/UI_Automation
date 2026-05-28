package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.RetryAnalyzer;
import utils.TestDataProvider;

public class LoginTest extends BaseTest {

    private final TestDataProvider testData = new TestDataProvider();
    private static final String BASE_URL = "https://advantageonlineshopping.com";

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Valid credentials should log in successfully")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo(BASE_URL);
        loginPage.login(testData.getData("validUsername"), testData.getData("validPassword"));
        // After valid login the URL should no longer contain "login"
        Assert.assertFalse(loginPage.isOnLoginPage(),
                "Should navigate away from login page after valid login");
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Invalid credentials should show error message")
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo(BASE_URL);
        loginPage.login(testData.getData("invalidUsername"), testData.getData("invalidPassword"));
        // Should stay on login page
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Should remain on login page after invalid login");
        String error = loginPage.getErrorMessage();
        Assert.assertFalse(error.isEmpty(),
                "Error message should be displayed for invalid credentials");
    }
}
