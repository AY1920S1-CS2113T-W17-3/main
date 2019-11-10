package owlmoney.logic.command.bank;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.util.logging.Logger;

import owlmoney.logic.command.Command;
import owlmoney.model.bank.exception.BankException;
import owlmoney.model.profile.Profile;
import owlmoney.ui.Ui;

/**
 * Executes DeleteSavingsCommand to delete a saving object.
 */
public class DeleteSavingsCommand extends Command {
    private final String bankName;
    private static final String SAVING_BANK_TYPE = "saving";
    private static final Logger logger = getLogger(DeleteSavingsCommand.class);

    /**
     * Creates an instance of DeleteSavingCommand.
     *
     * @param bankName Bank name to be deleted.
     */
    public DeleteSavingsCommand(String bankName) {
        this.bankName = bankName;
    }

    /**
     * Executes the function to delete a savings account from the profile.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     * @throws BankException If bank account fails check criteria.
     */
    @Override
    public boolean execute(Profile profile, Ui ui) throws BankException {
        profile.profileDeleteBank(this.bankName, SAVING_BANK_TYPE, ui);
        logger.info("Successful execution of DeleteSavingsCommand");
        return this.isExit;
    }
}
