package owlmoney.logic.command.transaction;

import owlmoney.logic.command.Command;
import owlmoney.model.profile.Profile;
import owlmoney.ui.Ui;

/**
 * ListDepositCommand class which contains the execution function to list deposits.
 */
public class ListDepositCommand extends Command {
    private final String accName;
    private final int displayNum;

    /**
     * Constructor to create an instance of ListDepositCommand.
     *
     * @param name       Bank account name.
     * @param displayNum Number of deposits to display.
     */
    public ListDepositCommand(String name, int displayNum) {
        this.accName = name;
        this.displayNum = displayNum;
    }

    /**
     * Executes the function to delete a deposit transaction.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     */
    public boolean execute(Profile profile, Ui ui) {
        profile.listDeposit(accName, ui, displayNum);
        return this.isExit;
    }
}
