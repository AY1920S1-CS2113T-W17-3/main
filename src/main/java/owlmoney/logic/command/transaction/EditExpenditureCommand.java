package owlmoney.logic.command.transaction;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.util.logging.Logger;

import owlmoney.logic.command.Command;
import owlmoney.model.bank.exception.BankException;
import owlmoney.model.card.exception.CardException;
import owlmoney.model.profile.Profile;
import owlmoney.model.transaction.exception.TransactionException;
import owlmoney.ui.Ui;

/**
 * Executes EditExpenditureCommand to edit an expenditure transaction.
 */
public class EditExpenditureCommand extends Command {

    private final String accountName;
    private final String amount;
    private final String date;
    private final String description;
    private final String category;
    private final int index;
    private final String type;
    private static final Logger logger = getLogger(EditExpenditureCommand.class);

    /**
     * Creates an instance of EditExpenditureCommand.
     *
     * @param name        Bank account name.
     * @param amount      New amount of expenditure if any.
     * @param date        New date of expenditure if any.
     * @param description New description of expenditure if any.
     * @param category    New category of expenditure if any.
     * @param index       Transaction number
     * @param type        The type of account to retrieve expenditure from.
     */
    public EditExpenditureCommand(String name, String amount, String date,
            String description, String category, int index, String type) {
        this.accountName = name;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
        this.index = index;
        this.type = type;
    }

    /**
     * Executes the function to edit an expenditure transaction.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     * @throws BankException        If bank account does not exist.
     * @throws TransactionException If incorrect date format.
     * @throws CardException        If card does not exist.
     */
    public boolean execute(Profile profile, Ui ui) throws BankException, TransactionException, CardException {
        profile.profileEditExpenditure(index, accountName, description, amount, date, category, ui, this.type);
        logger.info("Successful execution of EditExpenditureCommand");
        return this.isExit;
    }
}
