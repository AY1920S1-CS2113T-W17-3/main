package owlmoney.logic.command.transaction;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.util.logging.Logger;

import owlmoney.logic.command.Command;
import owlmoney.model.bank.exception.BankException;
import owlmoney.model.profile.Profile;
import owlmoney.model.transaction.exception.TransactionException;
import owlmoney.ui.Ui;

/**
 * Executes ListRecurringExpenditureCommand to list expenditures.
 */
public class ListRecurringExpenditureCommand extends Command {
    private final String accountName;
    private final String type;
    private static final Logger logger = getLogger(ListRecurringExpenditureCommand.class);

    /**
     * Creates an instance of ListRecurringExpenditureCommand.
     *
     * @param name Bank account name.
     * @param type Represents type of expenditure to be listed.
     */
    public ListRecurringExpenditureCommand(String name, String type) {
        this.accountName = name;
        this.type = type;
    }

    /**
     * Executes the function to list all recurring expenditure.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     * @throws BankException        If bank account does not exist or is an investment account.
     * @throws TransactionException If there are no recurring expenditures.
     */
    public boolean execute(Profile profile, Ui ui) throws BankException, TransactionException {
        profile.profileListRecurringExpenditure(accountName, ui, this.type);
        logger.info("Successful execution of ListRecurringExpenditureCommand");
        return this.isExit;
    }
}
