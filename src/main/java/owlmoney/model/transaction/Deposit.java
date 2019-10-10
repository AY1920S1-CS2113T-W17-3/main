package owlmoney.model.transaction;

import java.util.Date;

/**
 * Deposit class which represents one expenditure.
 */
public class Deposit extends Transaction {
    /**
     * Constructor which constructs a new deposit.
     *
     * @param description Description of deposit.
     * @param amount Amount of deposit.
     * @param date Date of deposit.
     * @param category Category of deposit.
     */
    public Deposit(String description, double amount, Date date, String category) {
        super(description,amount,date,category);
    }
}
