package app.entities;

public class User {
    private int id;
    private String email;
    private double balance;
    private boolean admin;

    public User(int id, String email, double balance, boolean admin) {
        this.id = id;
        this.email = email;
        this.balance = balance;
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
