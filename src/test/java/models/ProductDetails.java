package models;

import java.util.Objects;

/**
 * Data model representing product details retrieved from the AUT product page.
 * Used by ProductPage.getProductDetails() to return structured product information.
 */
public class ProductDetails {

    private String name;
    private double price;
    private String description;
    private int quantity;

    /**
     * Constructs a ProductDetails instance with all fields.
     *
     * @param name        product display name as shown on the AUT
     * @param price       unit price parsed from the price element text
     * @param description full product description text
     * @param quantity    available stock quantity
     */
    public ProductDetails(String name, double price, String description, int quantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }

    /**
     * Default no-arg constructor.
     */
    public ProductDetails() {
    }

    // --- Getters ---

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    // --- Setters ---

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // --- equals() and hashCode() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDetails that = (ProductDetails) o;
        return Double.compare(that.price, price) == 0
                && quantity == that.quantity
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, description, quantity);
    }

    // --- toString() ---

    @Override
    public String toString() {
        return "ProductDetails{"
                + "name='" + name + '\''
                + ", price=" + price
                + ", description='" + description + '\''
                + ", quantity=" + quantity
                + '}';
    }
}
