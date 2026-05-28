package models;

import java.util.Objects;

/**
 * Data model representing a single line item in an order summary.
 * Used by CheckoutPage.getOrderSummary() to represent each product in the checkout order.
 */
public class OrderLineItem {

    private String productName;
    private int quantity;
    private double unitPrice;
    private double lineTotal;

    /**
     * Constructs an OrderLineItem with all fields.
     *
     * @param productName name of the product in the order
     * @param quantity    quantity ordered
     * @param unitPrice   price per unit at time of checkout
     * @param lineTotal   unitPrice × quantity, as displayed in the order summary
     */
    public OrderLineItem(String productName, int quantity, double unitPrice, double lineTotal) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
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

    public double getLineTotal() {
        return lineTotal;
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

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }

    // --- equals() and hashCode() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineItem that = (OrderLineItem) o;
        return quantity == that.quantity
                && Double.compare(that.unitPrice, unitPrice) == 0
                && Double.compare(that.lineTotal, lineTotal) == 0
                && Objects.equals(productName, that.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, quantity, unitPrice, lineTotal);
    }

    // --- toString() ---

    @Override
    public String toString() {
        return "OrderLineItem{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + lineTotal +
                '}';
    }
}
