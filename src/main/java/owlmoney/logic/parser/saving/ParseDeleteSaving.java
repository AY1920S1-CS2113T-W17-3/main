package owlmoney.logic.parser.saving;

import java.util.Iterator;

import owlmoney.logic.command.Command;
import owlmoney.logic.command.bank.DeleteSavingsCommand;

import owlmoney.logic.parser.exception.ParserException;

/**
 * Parses the inputs for deleting a saving.
 */
public class ParseDeleteSaving extends ParseSaving {

    private static final String DELETE_COMMAND = "/delete";

    /**
     * Creates an instance of ParseDeleteSaving.
     *
     * @param data Raw user input data.
     * @throws ParserException If there are redundant parameters or if the first parameter is not valid.
     */
    public ParseDeleteSaving(String data) throws ParserException {
        super(data);
        checkRedundantParameter(AMOUNT_PARAMETER, DELETE_COMMAND);
        checkRedundantParameter(INCOME_PARAMETER, DELETE_COMMAND);
        checkRedundantParameter(NEW_NAME_PARAMETER, DELETE_COMMAND);
        checkFirstParameter();
    }

    /**
     * Checks each user input for each parameter.
     *
     * @throws ParserException If there are any invalid user input.
     */
    public void checkParameter() throws ParserException {
        Iterator<String> savingsIterator = savingsParameters.keySet().iterator();
        while (savingsIterator.hasNext()) {
            String key = savingsIterator.next();
            String value = savingsParameters.get(key);
            if (NAME_PARAMETER.equals(key) && (value == null || value.isBlank())) {
                logger.warning(key + " cannot be empty when deleting savings account");
                throw new ParserException(key + " cannot be empty when deleting savings account");
            } else if (NAME_PARAMETER.equals(key)) {
                checkName(NAME_PARAMETER, value);
            }
        }
    }

    /**
     * Returns the command to execute the deleting of a saving.
     *
     * @return DeleteSavingsCommand to be executed.
     */
    public Command getCommand() {
        DeleteSavingsCommand newDeleteSavingsCommand = new DeleteSavingsCommand(
                savingsParameters.get(NAME_PARAMETER));
        logger.info("Successful creation of DeleteSavingsCommand object");
        return newDeleteSavingsCommand;
    }

}
