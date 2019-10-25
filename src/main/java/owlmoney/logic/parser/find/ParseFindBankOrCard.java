package owlmoney.logic.parser.find;

import java.util.Iterator;

import owlmoney.logic.command.Command;
import owlmoney.logic.command.find.FindBankOrCardCommand;
import owlmoney.logic.parser.exception.ParserException;

/**
 * Represents the parsing of inputs for finding of bank or card.
 */
public class ParseFindBankOrCard extends ParseFind {

    /**
     * Creates an instance of ParseFindBankOrCard.
     *
     * @param data Raw user input data.
     * @param type Represents the type of object to be searched.
     * @throws ParserException If there are redundant parameters or first parameter is invalid.
     */
    public ParseFindBankOrCard(String data, String type) throws ParserException {
        super(data, type);
        checkRedundantParameter(DESCRIPTION, ISFIND);
        checkRedundantParameter(CATEGORY, ISFIND);
        checkRedundantParameter(FROM, ISFIND);
        checkRedundantParameter(TO, ISFIND);
        checkFirstParameter();
    }

    /**
     * Checks each user input for each parameter.
     *
     * @throws ParserException If there are missing or invalid parameters.
     */
    public void checkParameter() throws ParserException {
        Iterator<String> findIterator = findParameters.keySet().iterator();

        while (findIterator.hasNext()) {
            String key = findIterator.next();
            String value = findParameters.get(key);
            if (!DESCRIPTION.equals(key) && !CATEGORY.equals(key) && !FROM.equals(key) && !TO.equals(key)
                    && (value.isBlank() || value.isEmpty())) {
                throw new ParserException(key + " cannot be empty when doing a search");
            }
            if (NAME.equals(key)) {
                checkName(value);
            }
        }
    }

    /**
     * Returns the command to find bank or card.
     *
     * @return Returns FindBankOrCardCommand to be executed.
     */
    public Command getCommand() {
        FindBankOrCardCommand newFindBankOrCardCommand = new FindBankOrCardCommand(findParameters.get(NAME), this.type);
        return newFindBankOrCardCommand;
    }
}
