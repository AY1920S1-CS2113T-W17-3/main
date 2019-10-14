package owlmoney.logic.command.transaction;

import owlmoney.logic.command.Command;
import owlmoney.model.bank.exception.BankException;
import owlmoney.model.profile.Profile;
import owlmoney.model.transaction.exception.TransactionException;
import owlmoney.ui.Ui;

/**
 * DeleteDepositCommand class which contains the execution function to delete a deposit transaction.
 */
public class DeleteDepositCommand extends Command {
    private final int expNumber;
    private final String from;

    /**
     * Constructor to create an instance of DeleteDepositCommand.
     *
     * @param bankName Bank account name.
     * @param index    Transaction number.
     */
    public DeleteDepositCommand(String bankName, int index) {
        this.expNumber = index;
        this.from = bankName;
    }

    /**
     * Executes the function to delete a deposit transaction.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     */
    public boolean execute(Profile profile, Ui ui) throws BankException, TransactionException {
        profile.profileDeleteDeposit(this.expNumber, this.from, ui);
        return this.isExit;
    }
}
