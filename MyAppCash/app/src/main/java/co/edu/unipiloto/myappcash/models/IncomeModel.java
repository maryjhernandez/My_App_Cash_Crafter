package co.edu.unipiloto.myappcash.models;

public class IncomeModel {
    private String description;
    private double amount;
    private String date;
    private String userId;
    private String incomeSource;
    private String incomeType;

    // Constructor vacío requerido para Firebase
    public IncomeModel() {}

    public IncomeModel(String description, double amount, String date, String userId, String incomeSource, String incomeType) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.userId = userId;
        this.incomeSource = incomeSource;
        this.incomeType = incomeType;
    }

    // Getters y setters
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIncomeSource() {
        return incomeSource;
    }

    public void setIncomeSource(String incomeSource) {
        this.incomeSource = incomeSource;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }
}
