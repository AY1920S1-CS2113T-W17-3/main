package owlmoney.logic.parser.saving;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import owlmoney.logic.command.Command;
import owlmoney.logic.parser.ParseRawData;
import owlmoney.logic.parser.exception.ParserException;
import owlmoney.logic.regex.RegexUtil;

/**
 * Abstracts common Savings methods and functions where the child parsers will inherit from.
 */
public abstract class ParseSaving {
    HashMap<String, String> savingsParameters = new HashMap<String, String>();
    private ParseRawData parseRawData = new ParseRawData();
    private String rawData;
    static final String AMOUNT_PARAMETER = "/amount";
    static final String INCOME_PARAMETER = "/income";
    static final String NAME_PARAMETER = "/name";
    static final String NEW_NAME_PARAMETER = "/newname";
    private static final String[] SAVINGS_KEYWORD = new String[] {AMOUNT_PARAMETER, INCOME_PARAMETER,
        NAME_PARAMETER, NEW_NAME_PARAMETER};
    private static final List<String> SAVINGS_KEYWORD_LISTS = Arrays.asList(SAVINGS_KEYWORD);
    private static final Logger logger = getLogger(ParseSaving.class);

    /**
     * Creates an instance of any ParseSaving type object.
     *
     * @param data Raw user input data.
     */
    ParseSaving(String data) {
        this.rawData = data;
    }

    /**
     * Checks the user input for any redundant parameters.
     *
     * @param parameter Redundant parameter to check for,
     * @param command   Command the user performed.
     * @throws ParserException If a redundant parameter is detected.
     */
    void checkRedundantParameter(String parameter, String command) throws ParserException {
        if (rawData.contains(parameter)) {
            logger.warning(command + "/savings should not contain " + parameter);
            throw new ParserException(command + "/savings should not contain " + parameter);
        }
    }

    /**
     * Checks if the first parameter is a valid parameter.
     *
     * @throws ParserException If the first parameter is invalid.
     */
    void checkFirstParameter() throws ParserException {
        String[] rawDateSplit = rawData.split(" ", 2);
        if (!SAVINGS_KEYWORD_LISTS.contains(rawDateSplit[0])) {
            logger.warning("Incorrect parameter " + rawDateSplit[0]);
            throw new ParserException("Incorrect parameter " + rawDateSplit[0]);
        }
    }

    /**
     * Fills a hash table mapping each user input to each parameter.
     *
     * @throws ParserException If duplicate parameters are detected.
     */
    public void fillHashTable() throws ParserException {
        savingsParameters.put(AMOUNT_PARAMETER,
                parseRawData.extractParameter(rawData, AMOUNT_PARAMETER, SAVINGS_KEYWORD).trim());
        savingsParameters.put(INCOME_PARAMETER,
                parseRawData.extractParameter(rawData, INCOME_PARAMETER, SAVINGS_KEYWORD).trim());
        savingsParameters.put(NAME_PARAMETER,
                parseRawData.extractParameter(rawData, NAME_PARAMETER, SAVINGS_KEYWORD).trim());
        savingsParameters.put(NEW_NAME_PARAMETER,
                parseRawData.extractParameter(rawData, NEW_NAME_PARAMETER, SAVINGS_KEYWORD).trim());
    }

    /**
     * Checks if the amount entered by the user is a double and only contains numbers.
     *
     * @param valueString String to be converted to double as the user's amount.
     * @throws ParserException If the string is not a double value.
     */
    void checkAmount(String valueString) throws ParserException {
        if (!RegexUtil.regexCheckBankAmount(valueString)) {
            logger.warning("/amount can only be numbers with at most 9 digits, 2 decimal places"
                    + " and a value of at least 0");
            throw new ParserException("/amount can only be numbers with at most 9 digits, 2 decimal places"
                    + " and a value of at least 0");
        }
    }

    /**
     * Checks if the income entered by the user is a double and only contains numbers.
     *
     * @param valueString String to be converted to double as the user's income.
     * @throws ParserException If the string is not a double value.
     */
    void checkIncome(String valueString) throws ParserException {
        if (!RegexUtil.regexCheckBankAmount(valueString)) {
            logger.warning("/income can only be numbers with at most 9 digits and 2 decimal places"
                    + " and a value of at least 0");
            throw new ParserException("/income can only be numbers with at most 9 digits and 2 decimal places"
                    + " and a value of at least 0");
        }
    }

    /**
     * Checks if the bank name entered by the user does not contain special character and not too long.
     *
     * @param key        /name or /newname
     * @param nameString Name of bank
     * @throws ParserException If the name is too long or contain special characters.
     */
    void checkName(String key, String nameString) throws ParserException {
        if (!RegexUtil.regexCheckName(nameString)) {
            logger.warning(key + " can only be alphanumeric and at most 30 characters");
            throw new ParserException(key + " can only be alphanumeric and at most 30 characters");
        }
    }

    /**
     * Checks the parameters entered by the user.
     *
     * @throws ParserException If any parameters fail the check.
     */
    public abstract void checkParameter() throws ParserException;

    /**
     * Gets the command needed to be executed.
     *
     * @return Command to be executed.
     */
    public abstract Command getCommand();
}
