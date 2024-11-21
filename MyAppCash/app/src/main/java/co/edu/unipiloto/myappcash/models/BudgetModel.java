package co.edu.unipiloto.myappcash.models;

public class BudgetModel {
    private String budgetId;  // Nuevo campo agregado
    private String budgetName;
    private double totalAmount;
    private String category;
    private String userId;

    // Constructor vacío
    public BudgetModel(String budgetName, double totalAmount, String category, String userId) {}

    // Constructor con todos los campos
    public BudgetModel(String budgetId, String budgetName, double totalAmount, String category, String userId) {
        this.budgetId = budgetId;
        this.budgetName = budgetName;
        this.totalAmount = totalAmount;
        this.category = category;
        this.userId = userId;
    }

    // Getters y Setters
    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
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
