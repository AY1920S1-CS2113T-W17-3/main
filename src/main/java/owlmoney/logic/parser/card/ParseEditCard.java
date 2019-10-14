package owlmoney.logic.parser.card;

import java.util.Iterator;

import owlmoney.logic.command.Command;
import owlmoney.logic.command.card.EditCardCommand;
import owlmoney.logic.parser.exception.ParserException;

public class ParseEditCard extends ParseCard {

    /**
     * Creates an instance of ParseEditSaving.
     *
     * @param data Raw user input date.
     * @throws ParserException If the first parameter is invalid.
     */
    public ParseEditCard(String data) throws ParserException {
        super(data);
        checkFirstParameter();
    }

    /**
     * Checks each user input for each parameter.
     *
     * @throws ParserException If there are any invalid or missing inputs.
     */
    public void checkParameter() throws ParserException {
        Iterator<String> cardIterator = cardParameters.keySet().iterator();
        int changeCounter = 0;
        while (cardIterator.hasNext()) {
            String key = cardIterator.next();
            String value = cardParameters.get(key);
            if (NAME.equals(key) && (value.isEmpty() || value.isBlank())) {
                throw new ParserException("/name cannot be empty.");
            } else if (NAME.equals(key)) {
                checkName(value);
            }
            if (LIMIT.equals(key) && !(value.isEmpty() || value.isBlank())) {
                checkDouble(value, LIMIT);
                changeCounter++;
            }
            if (REBATE.equals(key) && !(value.isEmpty() || value.isBlank())) {
                checkDouble(value, REBATE);
                changeCounter++;
            }
            if (NEW_NAME.equals(key) && !(value.isEmpty() || value.isBlank())) {
                checkName(value);
                changeCounter++;
            }
        }
        if (changeCounter == 0) {
            throw new ParserException("Edit should have at least 1 differing parameter to change.");
        }
    }

    /**
     * Returns the command to execute the editing of a saving.
     *
     * @return Returns EditSavingsCommand to be executed.
     */
    public Command getCommand() {
        EditCardCommand newEditCardCommand = new EditCardCommand(cardParameters.get(NAME),
                cardParameters.get(LIMIT), cardParameters.get(REBATE), cardParameters.get(NEW_NAME));
        return newEditCardCommand;
    }
}