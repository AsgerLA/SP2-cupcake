package app.entities;

public class Order {
    private int id;
    private String topping;
    private String bottom;
    private double price;

    public Order(int id, String topping, String bottom, double price) {
        this.id = id;
        this.topping = topping;
        this.bottom = bottom;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopping() {
        return topping;
    }

    public void setTopping(String topping) {
        this.topping = topping;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
