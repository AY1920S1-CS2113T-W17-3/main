package owlmoney.logic.parser.transaction.expenditure;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import owlmoney.logic.command.Command;
import owlmoney.logic.parser.ParseRawData;
import owlmoney.logic.parser.exception.ParserException;
import owlmoney.logic.regex.RegexUtil;

/**
 * Abstracts common Expenditure methods and functions where the child parsers will inherit from.
 */
public abstract class ParseExpenditure {
    HashMap<String, String> expendituresParameters = new HashMap<String, String>();
    private ParseRawData parseRawData = new ParseRawData();
    private String rawData;
    String type;
    static final String AMOUNT_PARAMETER = "/amount";
    static final String DATE_PARAMETER = "/date";
    static final String DESCRIPTION_PARAMETER = "/desc";
    static final String CATEGORY_PARAMETER = "/category";
    static final String FROM_PARAMETER = "/from";
    static final String TRANSACTION_NUMBER_PARAMETER = "/transno";
    static final String NUM_PARAMETER = "/num";
    private static final String[] EXPENDITURE_KEYWORD = new String[] {
        AMOUNT_PARAMETER, DATE_PARAMETER, DESCRIPTION_PARAMETER,
        CATEGORY_PARAMETER, FROM_PARAMETER, TRANSACTION_NUMBER_PARAMETER, NUM_PARAMETER
    };
    private static final List<String> EXPENDITURE_KEYWORD_LISTS = Arrays.asList(EXPENDITURE_KEYWORD);
    private static final Logger logger = getLogger(ParseExpenditure.class);

    /**
     * Creates an instance of any ParseExpenditure type object.
     *
     * @param data Raw user input data.
     * @param type Represents type of expenditure to be added.
     */
    ParseExpenditure(String data, String type) {
        this.rawData = data;
        this.type = type;
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
            logger.warning(command + " /expenditure should not contain " + parameter);
            throw new ParserException(command + " /expenditure should not contain " + parameter);
        }
    }

    /**
     * Checks if the first parameter is a valid parameter.
     *
     * @throws ParserException If the first parameter is invalid.
     */
    void checkFirstParameter() throws ParserException {
        String[] rawDateSplit = rawData.split(" ", 2);
        if (!EXPENDITURE_KEYWORD_LISTS.contains(rawDateSplit[0])) {
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
        expendituresParameters.put(AMOUNT_PARAMETER,
                parseRawData.extractParameter(rawData, AMOUNT_PARAMETER, EXPENDITURE_KEYWORD).trim());
        expendituresParameters.put(DATE_PARAMETER,
                parseRawData.extractParameter(rawData, DATE_PARAMETER, EXPENDITURE_KEYWORD).trim());
        expendituresParameters.put(DESCRIPTION_PARAMETER,
                parseRawData.extractParameter(rawData, DESCRIPTION_PARAMETER, EXPENDITURE_KEYWORD).trim());
        expendituresParameters.put(CATEGORY_PARAMETER,
                parseRawData.extractParameter(rawData, CATEGORY_PARAMETER, EXPENDITURE_KEYWORD).trim());
        expendituresParameters.put(FROM_PARAMETER,
                parseRawData.extractParameter(rawData, FROM_PARAMETER, EXPENDITURE_KEYWORD).trim());
        expendituresParameters.put(TRANSACTION_NUMBER_PARAMETER,
                parseRawData.extractParameter(rawData, TRANSACTION_NUMBER_PARAMETER, EXPENDITURE_KEYWORD).trim());
        expendituresParameters.put(NUM_PARAMETER,
                parseRawData.extractParameter(rawData, NUM_PARAMETER, EXPENDITURE_KEYWORD).trim());
    }

    /**
     * Checks if the amount entered by the user is a double and only contains numbers.
     *
     * @param valueString String to be converted to double as the user's amount.
     * @throws ParserException If the string is not a double value.
     */
    void checkAmount(String valueString) throws ParserException {
        if (!RegexUtil.regexCheckMoney(valueString)) {
            logger.warning("/amount can only be positive numbers"
                    + " with at most 9 digits and 2 decimal places");
            throw new ParserException("/amount can only be positive numbers"
                    + " with at most 9 digits and 2 decimal places");
        }
    }

    /**
     * Checks if the transaction number or display number entered by the user is an integer.
     *
     * @param valueString String to be converted to integer.
     * @throws ParserException If the string is not an integer.
     */
    void checkInt(String variable, String valueString) throws ParserException {
        if (!RegexUtil.regexCheckListNumber(valueString)) {
            logger.warning(variable + " can only be a positive integer with at most 9 digits");
            throw new ParserException(variable + " can only be a positive integer with at most 9 digits");
        }
    }

    /**
     * Checks if the description entered by the user does not have special characters and is not too long.
     *
     * @param descString Deposit description.
     * @throws ParserException If the string has special characters or is too long.
     */
    void checkDescription(String descString, String keyword) throws ParserException {
        if (!RegexUtil.regexCheckDescription(descString)) {
            logger.warning(keyword + " can only contain numbers and letters and at most 50 characters");
            throw new ParserException(keyword + " can only contain numbers and letters and at most 50 characters");
        }
    }

    /**
     * Checks if the category entered by the user does not have special characters and is not too long.
     *
     * @param category Category of transaction.
     * @throws ParserException If the string has special characters or is too long.
     */
    void checkCategory(String category) throws ParserException {
        if (!RegexUtil.regexCheckCategory(category)) {
            logger.warning("/category can only contain numbers and letters and at most 15 characters");
            throw new ParserException("/category can only contain numbers and letters and at most 15 characters");
        }
    }

    /**
     * Checks if the bank name entered by the user does not contain special character and not too long.
     *
     * @param nameString Name of bank
     * @throws ParserException If the name is too long or contain special characters.
     */
    void checkName(String nameString) throws ParserException {
        if (!RegexUtil.regexCheckName(nameString)) {
            logger.warning("/from can only be alphanumeric and at most 30 characters");
            throw new ParserException("/from can only be alphanumeric and at most 30 characters");
        }
    }

    /**
     * Checks if the deposit date is of valid format and not after now.
     *
     * @param dateString Date to be checked.
     * @return Date if checks pass.
     * @throws ParserException If date format is invalid.
     */
    Date checkDate(String dateString) throws ParserException {
        if (RegexUtil.regexCheckDateFormat(dateString)) {
            DateFormat temp = new SimpleDateFormat("dd/MM/yyyy");
            temp.setLenient(false);
            Date date;
            try {
                date = temp.parse(dateString);
                if (date.compareTo(new Date()) > 0) {
                    logger.warning("/date cannot be after today");
                    throw new ParserException("/date cannot be after today");
                }
                return date;
            } catch (ParseException e) {
                logger.warning("Incorrect date format."
                        + " Date format is dd/mm/yyyy in year range of 1900-2099");
                throw new ParserException("Incorrect date format."
                        + " Date format is dd/mm/yyyy in year range of 1900-2099");
            }
        }
        throw new ParserException("Incorrect date format." + " Date format is dd/mm/yyyy in year range of 1900-2099");
    }

    /**
     * Checks the parameters given by the user.
     *
     * @throws ParserException If any parameters fail the check.
     */
    public abstract void checkParameter() throws ParserException;

    /**
     * Gets the command to be executed.
     *
     * @return Command to be executed.
     */
    public abstract Command getCommand();
}
