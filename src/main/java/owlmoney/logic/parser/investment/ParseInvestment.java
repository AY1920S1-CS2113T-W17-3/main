package owlmoney.logic.parser.investment;

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
 * Abstracts common Investment methods and functions where the child parsers will inherit from.
 */
public abstract class ParseInvestment {
    HashMap<String, String> investmentParameters = new HashMap<String, String>();
    private ParseRawData parseRawData = new ParseRawData();
    private String rawData;
    static final String AMOUNT_PARAMETER = "/amount";
    static final String NAME_PARAMETER = "/name";
    static final String NEW_NAME_PARAMETER = "/newname";
    private static final String[] INVESTMENT_KEYWORD = new String[] {AMOUNT_PARAMETER,
        NAME_PARAMETER, NEW_NAME_PARAMETER};
    private static final List<String> INVESTMENT_KEYWORD_LISTS = Arrays.asList(INVESTMENT_KEYWORD);
    static final Logger logger = getLogger(ParseInvestment.class);

    /**
     * Creates an instance of any ParseInvestment type object.
     *
     * @param data Raw user input data.
     */
    ParseInvestment(String data) {
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
            logger.warning(command + "/investment should not contain " + parameter);
            throw new ParserException(command + "/investment should not contain " + parameter);
        }
    }

    /**
     * Checks if the first parameter is a valid parameter.
     *
     * @throws ParserException If the first parameter is invalid.
     */
    void checkFirstParameter() throws ParserException {
        String[] rawDateSplit = rawData.split(" ", 2);
        if (!INVESTMENT_KEYWORD_LISTS.contains(rawDateSplit[0])) {
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
        investmentParameters.put(AMOUNT_PARAMETER,
                parseRawData.extractParameter(rawData, AMOUNT_PARAMETER, INVESTMENT_KEYWORD).trim());
        investmentParameters.put(NAME_PARAMETER,
                parseRawData.extractParameter(rawData, NAME_PARAMETER, INVESTMENT_KEYWORD).trim());
        investmentParameters.put(NEW_NAME_PARAMETER,
                parseRawData.extractParameter(rawData, NEW_NAME_PARAMETER, INVESTMENT_KEYWORD).trim());
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
     * Abstract method where each investment parser performs different checks on the parameters.
     *
     * @throws ParserException If any parameters fail the check.
     */
    public abstract void checkParameter() throws ParserException;

    /**
     * Abstract method where each investment parser creates different commands.
     *
     * @return Command to be executed.
     */
    public abstract Command getCommand();
}
