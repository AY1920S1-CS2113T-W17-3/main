package owlmoney.logic.command.bank;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.util.logging.Logger;

import owlmoney.logic.command.Command;
import owlmoney.model.bank.exception.BankException;
import owlmoney.model.profile.Profile;
import owlmoney.ui.Ui;

/**
 * Executes DeleteInvestmentCommand to delete an investment object.
 */
public class DeleteInvestmentCommand extends Command {
    private final String bankName;
    private static final String INVESTMENT_BANK_TYPE = "investment";
    private static final Logger logger = getLogger(DeleteInvestmentCommand.class);

    /**
     * Creates an instance of DeleteInvestmentCommand.
     *
     * @param bankName Bank name to be deleted.
     */
    public DeleteInvestmentCommand(String bankName) {
        this.bankName = bankName;
    }

    /**
     * Executes the function to delete an investment account from the profile.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     * @throws BankException If bank account fails check criteria.
     */
    @Override
    public boolean execute(Profile profile, Ui ui) throws BankException {
        profile.profileDeleteBank(this.bankName, INVESTMENT_BANK_TYPE, ui);
        logger.info("Executed DeleteInvestmentCommand Successfully");
        return this.isExit;
    }
}
