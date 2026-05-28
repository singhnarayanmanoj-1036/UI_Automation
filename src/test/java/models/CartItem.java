package models;

import java.util.Objects;

/**
 * Represents an item in the shopping cart.
 * Used by CartPage.getCartItems() to return cart contents.
 */
public class CartItem {

    private String productName;
    private int quantity;
    private double unitPrice;

    /**
     * Constructs a CartItem with all fields.
     *
     * @param productName the name of the product in the cart
     * @param quantity    the current quantity in the cart
     * @param unitPrice   the unit price at time of adding to cart
     */
    public CartItem(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Default no-arg constructor.
     */
    public CartItem() {
    }

    // --- Getters ---

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    // --- Setters ---

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    // --- equals & hashCode ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity
                && Double.compare(cartItem.unitPrice, unitPrice) == 0
                && Objects.equals(productName, cartItem.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, quantity, unitPrice);
    }

    // --- toString ---

    @Override
    public String toString() {
        return "CartItem{"
                + "productName='" + productName + '\''
                + ", quantity=" + quantity
                + ", unitPrice=" + unitPrice
                + '}';
    }
}
