package owlmoney.logic.command.bond;

import owlmoney.logic.command.Command;
import owlmoney.model.bank.exception.BankException;
import owlmoney.model.bond.Bond;
import owlmoney.model.bond.exception.BondException;
import owlmoney.model.profile.Profile;
import owlmoney.ui.Ui;

public class EditBondCommand extends Command {
    private final String bankName;
    private final String bondName;
    private final String year;
    private final String rate;
    private static final String INVESTMENT = "investment";
    private static final String BOND = "bonds";

    /**
     * Creates an instance of EditInvestmentCommand.
     *
     * @param bankName the name of the bank account.
     * @param bondName the name of the bond to be deleted.
     * @param rate the new rate of the bond.
     * @param year the new year of the bond.
     */
    public EditBondCommand(String bankName, String bondName, String rate, String year) {
        this.bankName = bankName;
        this.bondName = bondName;
        this.year = year;
        this.rate = rate;
    }

    /**
     * Executes the function to delete an investment account from the profile.
     *
     * @param profile Profile of the user.
     * @param ui      Ui of OwlMoney.
     * @return false so OwlMoney will not terminate yet.
     */
    @Override
    public boolean execute(Profile profile, Ui ui) throws BondException, BankException {
        Bond tempBond = profile.profileGetBond(this.bankName,this.bondName);
        profile.profileEditBond(this.bankName, this.bondName, this.year, this.rate, ui);
        return this.isExit;
    }
}