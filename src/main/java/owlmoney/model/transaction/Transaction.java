package owlmoney.model.transaction;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Transaction class which is abstract where various transaction types will inherit from given that
 * it is abstract.
 */

public abstract class Transaction {

    private String description;
    private double amount;
    private Date date;
    private String category;

    /**
     * Creates an instance of a transaction object.
     *
     * @param description The description that describes this expenditure.
     * @param amount      The amount of money spent in this instance of expenditure.
     * @param date        The date when this expenditure was made.
     * @param category    The category that this expenditure can be tagged to.
     */
    public Transaction(String description, double amount, Date date, String category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    /**
     * Gets the amount of money spent in this transaction.
     *
     * @return The amount of money spent in this transaction.
     */
    public double getAmount() {
        return this.amount;
    }

    /**
     * Gets the description of the instance of expenditure.
     *
     * @return The description of the expenditure.
     */
    private String getDescription() {
        return this.description;
    }

    /**
     * Gets the date that this expenditure was made.
     *
     * @return The date that the expenditure was made.
     */
    private String getDate() {
        DateFormat temp = new SimpleDateFormat("dd EEEE yyyy");
        return temp.format(this.date);
    }

    /**
     * Gets the category of that the expenditure was tagged to.
     *
     * @return The category of the expenditure that it was tagged to.
     */
    public String getCategory() {
        return this.category;
    }

    private String checkDebitCredit() {
        if (this.category.equals("deposit")) {
            return "[+] ";
        } else {
            return "[-] ";
        }
    }

    /**
     * Displays the description, amount added/spent, date and category of transaction.
     *
     * @return description, amount added/spent, date and category of transaction in String format.
     */
    String getDetails() {
        return "Description: " + getDescription() + "\nAmount: " + checkDebitCredit()
                + new DecimalFormat("0.00").format(getAmount()) + "\nDate: " + getDate()
                + "\nCategory: " + getCategory();
    }

    /**
     * Sets the new description of the transaction.
     *
     * @param newDesc New description of transaction.
     */
    void setDescription(String newDesc) {
        this.description = newDesc;
    }

    /**
     * Sets the new amount for the transaction.
     *
     * @param newAmount New amount for the transaction.
     */
    void setAmount(double newAmount) {
        this.amount = newAmount;
    }

    /**
     * Sets the new date of the transaction.
     *
     * @param newDate New date of the transaction.
     */
    void setDate(Date newDate) {
        this.date = newDate;
    }

    /**
     * Sets the new category of the transaction.
     * @param newCategory New category of the transaction.
     */
    void setCategory(String newCategory) {
        this.category = newCategory;
    }
}