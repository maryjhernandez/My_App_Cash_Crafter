package co.edu.unipiloto.myappcash.models;

public class Ahorro {
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private int monthsToAchieve;
    private String userId;

    public Ahorro() {
        // Constructor vacío requerido para Firebase
    }

    public Ahorro(String goalName, double targetAmount, double currentAmount, int monthsToAchieve, String userId) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.monthsToAchieve = monthsToAchieve;
        this.userId = userId;
    }

    public double calculateMonthlySavings() {
        // Verificar que no se está dividiendo por cero
        if (monthsToAchieve <= 0) {
            return 0; // Devolver 0 o manejar como prefieras
        }
        return (targetAmount - currentAmount) / monthsToAchieve;
    }

    // Getters y Setters
    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getMonthsToAchieve() {
        return monthsToAchieve;
    }

    public void setMonthsToAchieve(int monthsToAchieve) {
        this.monthsToAchieve = monthsToAchieve;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;

    }
}