package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

public class SearchPage extends BasePage {

    // AUT search input — in the top navigation bar
    private final By searchInput = By.cssSelector(
            "input[name='searchTerm'], input[placeholder*='search' i], " +
            "input[type='search'], #search_field, input.search-field");

    private final By resultItems = By.cssSelector(
            "div.products-list article, div.product-list-item, " +
            "li.product-item, section.products article, " +
            "div[class*='product-list'] div[class*='product']");

    private final By noResultsMessage = By.cssSelector(
            "div.no-results, p.no-results-message, h4.no-results, " +
            "div[class*='no-result'], span[class*='no-result']");

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    public SearchPage searchFor(String term) {
        waitForPageLoad();
        waitForAngular();

        WebElement input = waitForVisible(searchInput);
        input.clear();
        input.sendKeys(term);
        pause(500);
        input.sendKeys(Keys.ENTER);

        // Wait for results to load
        waitForPageLoad();
        waitForAngular();
        pause(2000);
        return this;
    }

    public List<String> getResultNames() {
        return driver.findElements(resultItems)
                .stream()
                .map(WebElement::getText)
                .filter(t -> t != null && !t.isBlank())
                .collect(Collectors.toList());
    }

    public boolean hasResults() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(resultItems));
            return !driver.findElements(resultItems).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getNoResultsMessage() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(noResultsMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void clickResult(String productName) {
        pause(1000);
        List<WebElement> results = driver.findElements(resultItems);
        for (WebElement el : results) {
            String text = el.getText();
            if (text != null && text.toLowerCase().contains(productName.toLowerCase())) {
                el.click();
                waitForPageLoad();
                waitForAngular();
                return;
            }
        }
        // Fall back to first result if no text match
        if (!results.isEmpty()) {
            results.get(0).click();
            waitForPageLoad();
            waitForAngular();
        } else {
            throw new org.openqa.selenium.NoSuchElementException(
                    "No search results found for: " + productName);
        }
    }
}
