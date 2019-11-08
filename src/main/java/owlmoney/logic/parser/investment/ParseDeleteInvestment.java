package owlmoney.logic.parser.investment;

import java.util.Iterator;

import owlmoney.logic.command.Command;
import owlmoney.logic.command.bank.DeleteInvestmentCommand;
import owlmoney.logic.parser.exception.ParserException;

/**
 * Represents the parsing of inputs for deleting an investment account..
 */
public class ParseDeleteInvestment extends ParseInvestment {
    private static final String DELETE_COMMAND = "/delete";

    /**
     * Creates an instance of ParseDeleteInvestment.
     *
     * @param data Raw user input data.
     * @throws ParserException If there are redundant parameters or if the first parameter is not valid.
     */
    public ParseDeleteInvestment(String data) throws ParserException {
        super(data);
        checkRedundantParameter(AMOUNT_PARAMETER, DELETE_COMMAND);
        checkRedundantParameter(NEW_NAME_PARAMETER, DELETE_COMMAND);
        checkFirstParameter();
    }

    /**
     * Checks each user input for each parameter.
     *
     * @throws ParserException If there are any invalid user input.
     */
    public void checkParameter() throws ParserException {
        Iterator<String> investmentIterator = investmentParameters.keySet().iterator();
        while (investmentIterator.hasNext()) {
            String key = investmentIterator.next();
            String value = investmentParameters.get(key);
            if (NAME_PARAMETER.equals(key) && (value.isBlank() || value.isEmpty())) {
                throw new ParserException(key + " cannot be empty when deleting an investment account");
            } else if (NAME_PARAMETER.equals(key)) {
                checkName(NAME_PARAMETER, value);
            }
        }
    }

    /**
     * Returns the command to execute the deletion of investment account.
     *
     * @return DeleteInvestmentCommand to be executed.
     */
    public Command getCommand() {
        DeleteInvestmentCommand newDeleteInvestmentCommand =
                new DeleteInvestmentCommand(investmentParameters.get(NAME_PARAMETER));
        return newDeleteInvestmentCommand;
    }
}
