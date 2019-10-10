package owlmoney.logic.parser.transaction.expenditure;

import java.util.Date;
import java.util.Iterator;

import owlmoney.logic.command.Command;
import owlmoney.logic.command.transaction.EditExpenditureCommand;
import owlmoney.logic.parser.exception.ParserException;

/**
 * Represents the parsing of inputs for editing a expenditure.
 */
public class ParseEditExpenditure extends ParseExpenditure {
    private static final String EDIT = "/edit";

    /**
     * Constructor which creates an instance of ParseEditExpenditure.
     *
     * @param data Raw user input date.
     * @throws ParserException If there are redundant parameters or first parameter is invalid.
     */
    public ParseEditExpenditure(String data) throws ParserException {
        super(data);
        checkRedundantParameter(NUM, EDIT);
        checkFirstParameter();
    }

    /**
     * Checks each user input for each parameter.
     *
     * @throws ParserException If user input is invalid.
     */
    public void checkParameter() throws ParserException {
        Iterator<String> savingsIterator = expendituresParameters.keySet().iterator();
        int changeCounter = 0;
        while (savingsIterator.hasNext()) {
            String key = savingsIterator.next();
            String value = expendituresParameters.get(key);
            if ((TRANSNO.equals(key) || FROM.equals(key)) && (value.isBlank() || value.isEmpty())) {
                throw new ParserException(key + " cannot be empty when editing an expenditure");
            } else if (TRANSNO.equals(key)) {
                checkInt(TRANSNO, value);
            } else if (FROM.equals(key)) {
                checkName(value);
            } else if (CATEGORY.equals(key) && "deposit".equals(value)) {
                throw new ParserException(key + " cannot be deposit when editing an expenditure");
            } else if ((!value.isEmpty() || !value.isBlank())) {
                if (AMOUNT.equals(key)) {
                    checkAmount(value);
                } else if (DESCRIPTION.equals(key)) {
                    checkDescription(value);
                } else if (DATE.equals(key)) {
                    Date temp = checkDate(value);
                }
                changeCounter++;
            }
        }
        if (changeCounter == 0) {
            throw new ParserException("Edit should have at least 1 differing parameter to change.");
        }
    }

    /**
     * Returns the command to execute the editing of a expenditure.
     *
     * @return EditExpenditureCommand to be executed.
     */
    public Command getCommand() {
        EditExpenditureCommand newEditExpenditureCommand = new EditExpenditureCommand(expendituresParameters.get(FROM),
                expendituresParameters.get(AMOUNT), expendituresParameters.get(DATE),
                expendituresParameters.get(DESCRIPTION), expendituresParameters.get(CATEGORY),
                Integer.parseInt(expendituresParameters.get(TRANSNO)));
        return newEditExpenditureCommand;
    }
}