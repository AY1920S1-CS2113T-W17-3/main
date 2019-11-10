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
 * Executes ListExpenditureCommand to list expenditures.
 */
public class ListExpenditureCommand extends Command {
    private final String accountName;
    private final int displayNum;
    private final String type;
    private static final Logger logger = getLogger(ListExpenditureCommand.class);

    /**
     * Creates an instance of ListExpenditureCommand.
     *
     * @param name       Bank account name.
     * @param displayNum Number of expenditures to display.
     * @param type       Represents type of expenditure to be listed.
     */
    public ListExpenditureCommand(String name, int displayNum, String type) {
        this.accountName = name;
        this.displayNum = displayNum;
        this.type = type;
    }

    /**
     * Executes the function to list the specified number of expenditures.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     * @throws BankException        If bank account does not exist.
     * @throws TransactionException If no expenditure found or no expenditure is in the list..
     * @throws CardException        If the credit card name cannot be found.
     */
    public boolean execute(Profile profile, Ui ui) throws BankException, TransactionException, CardException {
        profile.profileListExpenditure(accountName, ui, displayNum, this.type);
        logger.info("Successful execution og ListExpenditureCommand");
        return this.isExit;
    }
}
