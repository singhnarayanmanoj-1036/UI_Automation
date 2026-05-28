package pages;

import models.CartItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class CartPage extends BasePage {

    private final By cartBadge      = By.cssSelector("span.cart-count, span.roboto-medium.ng-binding");
    private final By cartItems      = By.cssSelector("div.cart-item, tr.cart-item-row");
    private final By cartIcon       = By.cssSelector("a.cart-btn, div.cart-icon");
    private final By checkoutBtn    = By.cssSelector("button.checkout-btn, a.btn-checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public void openCart() {
        waitForClickable(cartIcon).click();
    }

    public int getCartItemCount() {
        try {
            String text = waitForVisible(cartBadge).getText().trim();
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (Exception e) {
            return 0;
        }
    }

    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        for (WebElement row : driver.findElements(cartItems)) {
            try {
                String name  = row.findElement(By.cssSelector(".product-name, td.name")).getText();
                String priceText = row.findElement(By.cssSelector(".product-price, td.price"))
                        .getText().replaceAll("[^0-9.]", "");
                String qtyText = row.findElement(By.cssSelector("input.quantity, td.quantity"))
                        .getAttribute("value");
                double price = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);
                int qty = qtyText == null || qtyText.isEmpty() ? 1 : Integer.parseInt(qtyText.trim());
                items.add(new CartItem(name, qty, price));
            } catch (Exception ignored) {}
        }
        return items;
    }

    public double getLineItemTotal(String productName) {
        for (WebElement row : driver.findElements(cartItems)) {
            try {
                String name = row.findElement(By.cssSelector(".product-name, td.name")).getText();
                if (name.toLowerCase().contains(productName.toLowerCase())) {
                    String totalText = row.findElement(By.cssSelector(".line-total, td.total"))
                            .getText().replaceAll("[^0-9.]", "");
                    return totalText.isEmpty() ? 0.0 : Double.parseDouble(totalText);
                }
            } catch (Exception ignored) {}
        }
        return 0.0;
    }

    public void proceedToCheckout() {
        waitForClickable(checkoutBtn).click();
    }
}
