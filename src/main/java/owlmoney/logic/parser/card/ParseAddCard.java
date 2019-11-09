package owlmoney.logic.parser.card;

import java.util.Iterator;

import owlmoney.logic.command.Command;
import owlmoney.logic.command.card.AddCardCommand;
import owlmoney.logic.parser.exception.ParserException;

/**
 * Represents the parsing of inputs for adding a new card.
 */
public class ParseAddCard extends ParseCard {

    /**
     * Creates an instance of ParseAddCard.
     *
     * @param data Raw data of user input to be parsed.
     * @throws ParserException If there is a redundant parameter or first parameter is not a valid type.
     */
    public ParseAddCard(String data) throws ParserException {
        super(data);
        checkFirstParameter();
    }

    /**
     * Checks each user input for each parameter.
     *
     * @throws ParserException If there are any invalid or missing input.
     */
    public void checkParameter() throws ParserException {
        Iterator<String> cardIterator = cardParameters.keySet().iterator();

        while (cardIterator.hasNext()) {
            String key = cardIterator.next();
            String value = cardParameters.get(key);
            if (!NEW_NAME_PARAMETER.equals(key) && (value.isBlank() || value.isEmpty())) {
                logger.warning(key + " cannot be empty when adding a new card");
                throw new ParserException(key + " cannot be empty when adding a new card");
            }
            if (LIMIT_PARAMETER.equals(key)) {
                checkLimit(value);
            }
            if (REBATE_PARAMETER.equals(key)) {
                checkCashBack(value);
            }
            if (NAME_PARAMETER.equals(key)) {
                checkName(value);
            }
        }
    }

    /**
     * Returns the command to execute the adding of a new card.
     *
     * @return AddCardCommand to be executed.
     */
    public Command getCommand() {
        AddCardCommand newAddCardCommand = new AddCardCommand(cardParameters.get(NAME_PARAMETER),
                Double.parseDouble(cardParameters.get(LIMIT_PARAMETER)), Double.parseDouble(cardParameters.get(
                REBATE_PARAMETER)));
        logger.info("Successful creation of AddCardCommand object");
        return newAddCardCommand;
    }
}
