package owlmoney.logic.parser.goals;

import owlmoney.logic.command.Command;
import owlmoney.logic.parser.ParseRawData;
import owlmoney.logic.parser.exception.ParserException;
import owlmoney.logic.regex.RegexUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static owlmoney.commons.log.LogsCenter.getLogger;


/**
 * Abstracts common Goals methods and functions where the child parsers will inherit from.
 */
public abstract class ParseGoals {
    HashMap<String, String> goalsParameters = new HashMap<>();
    private ParseRawData parseRawData = new ParseRawData();
    private String rawData;


    static final String NAME_PARAMETER = "/name";
    static final String AMOUNT_PARAMETER = "/amount";
    static final String BY_PARAMETER = "/by";
    static final String NEW_NAME_PARAMETER = "/newname";
    static final String FROM_PARAMETER = "/from";
    static final String IN_PARAMETER = "/in";
    static final String MARK_DONE_PARAMETER = "/mark";
    private static final String[] GOALS_KEYWORD = new String[]{NAME_PARAMETER, AMOUNT_PARAMETER, BY_PARAMETER,
        NEW_NAME_PARAMETER, FROM_PARAMETER, IN_PARAMETER, MARK_DONE_PARAMETER};
    private static final List<String> GOALS_KEYWORD_LISTS = Arrays.asList(GOALS_KEYWORD);
    private static final Logger logger = getLogger(ParseGoals.class);

    /**
     * Creates an instance of any ParseGoals type object.
     *
     * @param data Raw user input data.
     */
    ParseGoals(String data) {
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
            logger.warning("Contained redundant parameter: " + parameter);
            throw new ParserException(command + " /goals should not contain " + parameter);
        }
    }

    /**
     * Checks if the first parameter is a valid parameter.
     *
     * @throws ParserException If the first parameter is invalid.
     */
    void checkFirstParameter() throws ParserException {
        String[] rawDataSplit = rawData.split(" ", 2);
        if (!GOALS_KEYWORD_LISTS.contains(rawDataSplit[0])) {
            logger.warning("Invalid parameters provided");
            throw new ParserException("Incorrect parameter: " + rawDataSplit[0]);
        }
    }

    /**
     * Fills a hash table mapping each user input to each parameter.
     *
     * @throws ParserException If duplicate parameters are detected.
     */
    public void fillHashTable() throws ParserException {
        goalsParameters.put(NAME_PARAMETER,
                parseRawData.extractParameter(rawData, NAME_PARAMETER, GOALS_KEYWORD).trim());
        goalsParameters.put(AMOUNT_PARAMETER,
                parseRawData.extractParameter(rawData, AMOUNT_PARAMETER, GOALS_KEYWORD).trim());
        goalsParameters.put(BY_PARAMETER,
                parseRawData.extractParameter(rawData, BY_PARAMETER, GOALS_KEYWORD).trim());
        goalsParameters.put(NEW_NAME_PARAMETER,
                parseRawData.extractParameter(rawData, NEW_NAME_PARAMETER, GOALS_KEYWORD).trim());
        goalsParameters.put(IN_PARAMETER,
                parseRawData.extractParameter(rawData, IN_PARAMETER, GOALS_KEYWORD).trim());
        goalsParameters.put(FROM_PARAMETER,
                parseRawData.extractParameter(rawData, FROM_PARAMETER, GOALS_KEYWORD).trim());
        goalsParameters.put(MARK_DONE_PARAMETER,
                parseRawData.extractParameter(rawData, MARK_DONE_PARAMETER, GOALS_KEYWORD).trim());
    }

    /**
     * Checks if the amount entered by the user is a double and only contains numbers.
     *
     * @param valueString String to be converted to double as the user's amount.
     * @throws ParserException If the string is not a double value.
     */
    void checkGoalsAmount(String valueString) throws ParserException {
        if (!RegexUtil.regexCheckMoney(valueString)) {
            logger.warning("Amount does not contain at most 9 digits and 2d.p or value of at least 1");
            throw new ParserException("/amount can only be numbers with at most 9 digits, 2 decimal places"
                    + " and a value of at least 1");
        }
    }

    /**
     * Checks if the goal name entered by the user does not contain special character and not too long.
     *
     * @param key  /name or /newname
     * @param name Name of goal
     * @throws ParserException If the name is too long or contain special characters.
     */
    void checkGoalsName(String key, String name) throws ParserException {
        if (!RegexUtil.regexCheckGoalsName(name)) {
            logger.warning("Name is not alphanumeric or more than 20 characters");
            throw new ParserException(key + " can only be alphanumeric and at most 20 characters");
        }
    }

    /**
     * Checks if the savings account name entered by the user does not contain special character and not too long.
     *
     * @param key  /name or /newname
     * @param name Name of goal
     * @throws ParserException If the name is too long or contain special characters.
     */
    void checkName(String key, String name) throws ParserException {
        if (!RegexUtil.regexCheckName(name)) {
            throw new ParserException(key + " can only be alphanumeric and at most 30 characters");
        }
    }

    /**
     * Checks if the date set for the goal is of valid format and not before now.
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
                if (((Date) date).compareTo(new Date()) < 0) {
                    logger.warning("Date provided is before today's date");
                    throw new ParserException("/date has already passed");
                }
                return date;
            } catch (ParseException e) {
                logger.warning("Date provided is invalid format");
                throw new ParserException("Incorrect date format."
                        + " Date format is dd/mm/yyyy in year range of 1900-2099");
            }
        }
        logger.warning("Date provided is invalid format");
        throw new ParserException("Incorrect date format."
                + " Date format is dd/mm/yyyy in year range of 1900-2099");
    }

    /**
     * Checks if days set for goal is valid format.
     *
     * @param variable    number of days.
     * @param valueString value of number of days.
     * @throws ParserException If days is set too long after or contain invalid parameters.
     */
    void checkDay(String variable, String valueString) throws ParserException {
        if (!RegexUtil.regexCheckDay(valueString)) {
            logger.warning("/in can only be integer up to 365 days");
            throw new ParserException(variable + " can only be a positive integer up to only 365 days!");
        }
    }

    void checkInt(String variable, String valueString) throws ParserException {
        if (!RegexUtil.regexCheckExactNumFormat(valueString)) {
            logger.warning("/mark can only be accompanied with input 1");
            throw new ParserException(variable + " can only be 1!");
        }
    }

    /**
     * Converts days to Date.
     *
     * @param day number of days in which user expects to achieve goal.
     * @return Date of goal deadline.
     */
    Date convertDaysToDate(int day) {
        //count from now to number of days left
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * Checks if only one of /by or /in is provided for Goals deadline.
     *
     * @param by Date of goals deadline.
     * @param in Days of goals deadline.
     * @throws ParserException If both /by and /in provided, or none provided.
     */
    void checkOptionalParameter(String by, String in) throws ParserException {
        if (by.isBlank() && in.isBlank()) {
            logger.warning("Date parameter not specified, use either /in [DAYS] or /by [DATE]");
            throw new ParserException("/by and /in cannot be both empty when adding new goals");
        } else if (!by.isBlank() && !in.isBlank()) {
            logger.warning("Cannot specify both /in and /by");
            throw new ParserException("/by and /in cannot be specified concurrently when adding new goals");
        }
    }

    /**
     * Checks the parameters entered by the user.
     *
     * @throws ParserException If any parameters fail the check.
     */
    public abstract void checkParameter() throws ParserException;

    /**
     * Gets the relevant command to be executed.
     *
     * @return Command to be executed.
     */
    public abstract Command getCommand();
}
