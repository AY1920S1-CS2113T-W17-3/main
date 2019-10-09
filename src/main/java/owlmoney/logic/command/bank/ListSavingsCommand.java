package owlmoney.logic.command.bank;

import owlmoney.logic.command.Command;
import owlmoney.model.profile.Profile;
import owlmoney.ui.Ui;

public class ListSavingsCommand extends Command {
    @Override
    public boolean execute(Profile profile, Ui ui) {
        profile.listBanks(ui);
        return this.isExit;
    }
}
