package pages;

import models.OrderLineItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CheckoutPage extends BasePage {

    private final By orderSummaryRows = By.cssSelector("div.order-summary-item, tr.order-row");
    private final By checkoutHeader   = By.cssSelector("h3.checkout-title, div.checkout-header");

    public CheckoutPage(WebDriver driver) {
        super(driver);
        // Checkout gets a longer wait
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isCheckoutPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutHeader));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<OrderLineItem> getOrderSummary() {
        List<OrderLineItem> items = new ArrayList<>();
        for (WebElement row : driver.findElements(orderSummaryRows)) {
            try {
                String name = row.findElement(By.cssSelector(".product-name, td.name")).getText();
                String qtyText = row.findElement(By.cssSelector(".quantity, td.qty")).getText()
                        .replaceAll("[^0-9]", "");
                String priceText = row.findElement(By.cssSelector(".unit-price, td.price"))
                        .getText().replaceAll("[^0-9.]", "");
                String totalText = row.findElement(By.cssSelector(".line-total, td.total"))
                        .getText().replaceAll("[^0-9.]", "");
                int qty = qtyText.isEmpty() ? 1 : Integer.parseInt(qtyText);
                double unitPrice = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);
                double lineTotal = totalText.isEmpty() ? 0.0 : Double.parseDouble(totalText);
                items.add(new OrderLineItem(name, qty, unitPrice, lineTotal));
            } catch (Exception ignored) {}
        }
        return items;
    }
}
