package pages;

import models.ProductDetails;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ProductPage extends BasePage {

    private final By productName    = By.cssSelector("h1.product-name, h3.product-name");
    private final By productPrice   = By.cssSelector("span.product-price, label.product-price");
    private final By productDesc    = By.cssSelector("div.product-description, p.description");
    private final By productQty     = By.cssSelector("input.product-quantity, span.product-quantity");
    private final By productList    = By.cssSelector("div.products-list article, div.product-list-item");
    private final By addToCartBtn   = By.cssSelector("button.add-to-cart-btn, button[name='save_to_cart']");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetails getProductDetails() {
        String name  = waitForVisible(productName).getText();
        String priceText = driver.findElement(productPrice).getText()
                .replaceAll("[^0-9.]", "");
        double price = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);
        String desc  = "";
        try { desc = driver.findElement(productDesc).getText(); } catch (Exception ignored) {}
        int qty = 0;
        try {
            String qtyText = driver.findElement(productQty).getAttribute("value");
            if (qtyText == null) qtyText = driver.findElement(productQty).getText();
            qty = Integer.parseInt(qtyText.trim());
        } catch (Exception ignored) {}
        return new ProductDetails(name, price, desc, qty);
    }

    public List<String> getCategoryProducts() {
        return driver.findElements(productList).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public void selectProduct(String productName) {
        driver.findElements(productList).stream()
                .filter(el -> el.getText().toLowerCase().contains(productName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new org.openqa.selenium.NoSuchElementException(
                        "Product not found: " + productName))
                .click();
    }

    public void addToCart() {
        waitForClickable(addToCartBtn).click();
    }
}
