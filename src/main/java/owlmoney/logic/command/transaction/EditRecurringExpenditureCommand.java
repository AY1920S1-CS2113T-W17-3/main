package owlmoney.logic.command.transaction;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.util.logging.Logger;

import owlmoney.logic.command.Command;
import owlmoney.model.bank.exception.BankException;
import owlmoney.model.profile.Profile;
import owlmoney.model.transaction.exception.TransactionException;
import owlmoney.ui.Ui;

/**
 * Executes EditRecurringExpenditureCommand to edit a recurring expenditure transaction.
 */
public class EditRecurringExpenditureCommand extends Command {

    private final String accountName;
    private final String amount;
    private final String description;
    private final String category;
    private final int index;
    private final String type;
    private static final Logger logger = getLogger(EditRecurringExpenditureCommand.class);

    /**
     * Creates an instance of EditRecurringExpenditureCommand.
     *
     * @param name        Bank account name.
     * @param amount      New amount of recurring expenditure if any.
     * @param description New description of recurring expenditure if any.
     * @param category    New category of recurring expenditure if any.
     * @param index       Transaction number
     * @param type        The type of account to retrieve expenditure from.
     */
    public EditRecurringExpenditureCommand(String name, String amount,
            String description, String category, int index, String type) {
        this.accountName = name;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.index = index;
        this.type = type;
    }

    /**
     * Executes the function to delete a recurring expenditure.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     * @throws BankException        If bank account does not exist or is an investment account.
     * @throws TransactionException If there are 0 recurring expenditure or index is out of range.
     */
    public boolean execute(Profile profile, Ui ui) throws BankException, TransactionException {
        profile.profileEditRecurringExpenditure(accountName, index, description, amount, category, ui, this.type);
        logger.info("Successful execution of EditRecurringExpenditureCommand");
        return this.isExit;
    }
}
