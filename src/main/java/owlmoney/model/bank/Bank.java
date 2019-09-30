package owlmoney.model.bank;

import owlmoney.model.expenditure.Expenditure;

public abstract class Bank {
    private String accountName;
    private double currentAmount;

    public Bank(String name, double currentAmount) {
        this.accountName = name;
        this.currentAmount = currentAmount;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public double getCurrentAmount() {
        return this.currentAmount;
    }

    public void getDescription() {
        System.out.println(accountName + "\n" + currentAmount);
    }

    public abstract void addInExpenditure(Expenditure exp);

    public abstract void listAllExpenditure();
}