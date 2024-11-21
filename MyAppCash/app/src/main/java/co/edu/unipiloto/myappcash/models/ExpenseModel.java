package co.edu.unipiloto.myappcash.models;

public class ExpenseModel {
    private String description;
    private double amount;
    private String date;
    private String category;
    private String userId;

    public ExpenseModel() {
    }

    public ExpenseModel(String description, double amount, String date, String category, String userId) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
