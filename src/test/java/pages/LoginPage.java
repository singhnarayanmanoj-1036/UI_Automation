package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    // AUT selectors for https://advantageonlineshopping.com/#/login
    private final By usernameInput = By.cssSelector("input[name='username'], input#userName");
    private final By passwordInput = By.cssSelector("input[name='password'], input[type='password']");
    private final By loginButton   = By.cssSelector(
            "button#sign_in_btn, input[value='SIGN IN'], button[type='submit'], button.login-btn");
    private final By errorMessage  = By.cssSelector(
            "span.login-error-message, div.login-error, p.login-error, " +
            "div[class*='error'], span[class*='error'], .login-error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage navigateTo(String baseUrl) {
        driver.get(baseUrl + "/#/login");
        waitForPageLoad();
        waitForAngular();
        // Wait for either the username field or the login URL
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        } catch (Exception e) {
            // page may still be loading — give it extra time
            pause(2000);
        }
        return this;
    }

    public void login(String username, String password) {
        WebElement user = waitForVisible(usernameInput);
        user.clear();
        user.sendKeys(username);

        WebElement pass = waitForVisible(passwordInput);
        pass.clear();
        pass.sendKeys(password);

        waitForClickable(loginButton).click();
        waitForPageLoad();
        waitForAngular();
        pause(1500);
    }

    public String getErrorMessage() {
        try {
            WebElement el = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return el.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("login");
    }
}
