package owlmoney.ui;

/**
 * Ui handles messages shown to the user of this application.
 * It does not need to be instantiated with any tasks; these are passed
 * as arguments to its methods.
 */
public class Ui {

    private static final String ITEMNO = "Item No.";
    private static final String DESCRIPTION = "Description";
    private static final String AMOUNT = "Amount";
    private static final String DATE = "Date";
    private static final String CATEGORY = "Category";
    private static final String TRANSACTION_NUMBER = "Transaction No.";
    private static final String TRANSACTION_TYPE = "transaction";
    private static final String ITEM_TYPE = "item";
    private static final String CARD_NAME = "Card Name";
    private static final String MONTHLY_LIMIT = "Monthly Limit";
    private static final String REMAINING_LIMIT = "Remaining Limit";
    private static final String REBATE_RATE = "Rebate";
    private static final String ACCOUNT_NAME = "Account Name";
    private static final String ACCOUNT_TYPE = "Account Type";
    private static final String CURRENT_AMOUNT = "Current Amount";
    private static final String INCOME = "Income";
    private static final String GOAL_NAME = "To Accomplish";
    private static final String GOAL_AMOUNT = "Amount to save";
    private static final String GOAL_DATE = "To be achieved by";
    private static final String BOND_NAME = "Bond Name";
    private static final String BOND_RATE = "Rate";
    private static final String BOND_DATE = "Date of Purchase";
    private static final String BOND_YEAR = "Number of Years";
    private static final String GOAL_PROGRESS = "Save another";
    private static final String GOAL_BANK_NAME = "Saving Account";
    private static final String GOAL_ACHIEVED = "Goal Achieved";
    private static final String RECURRING_TRANSACTION_TYPE = "recurTransaction";
    private static final String NEXT_EXPENSE_DATE = "Next Expense Date";
    private static final String RECURRING_ITEM_TYPE = "recurItem";
    private static final String ACHIEVEMENT_NAME = "Achievement Name";
    private static final String ACHIEVEMENT_AMOUNT = "Amount saved";
    private static final String ACHIEVEMENT_DATE = "Date set to achieve";
    private static final String NEWLINE = System.lineSeparator();

    /**
     * Prints a message line to the user.
     *
     * @param msg The message to print.
     */
    public void printMessage(String msg) {
        System.out.println(msg);
    }

    /**
     * Prints the transaction header.
     */
    public void printTransactionHeader(String type) {
        if (ITEM_TYPE.equals(type)) {
            System.out.printf("%-20s %-55s %-20s %-20s %-20s %n", ITEMNO, DESCRIPTION,
                    AMOUNT, DATE, CATEGORY);
        } else if (TRANSACTION_TYPE.equals(type)) {
            System.out.printf("%-20s %-55s %-20s %-20s %-20s %n", TRANSACTION_NUMBER,
                    DESCRIPTION, AMOUNT, DATE, CATEGORY);
        } else if (RECURRING_ITEM_TYPE.equals(type)) {
            System.out.printf("%-20s %-55s %-20s %-20s %-20s %n", ITEMNO, DESCRIPTION,
                    AMOUNT, NEXT_EXPENSE_DATE, CATEGORY);
        } else if (RECURRING_TRANSACTION_TYPE.equals(type)) {
            System.out.printf(
                    "%-20s %-55s %-20s %-20s %-20s %n", TRANSACTION_NUMBER, DESCRIPTION,
                    AMOUNT, NEXT_EXPENSE_DATE, CATEGORY);
        }
        System.out.println("-----------------------------------------------------------------------------"
                + "----------------------------------------------------------------");
    }

    /**
     * Prints the transaction being specified.
     *
     * @param num         Represents the numbering of the transaction.
     * @param description Describes the listed transaction.
     * @param amount      Represents the amount spent for the listed transaction.
     * @param date        Represents the date entered for the listed transaction.
     * @param category    Represents the category which the listed transaction falls under.
     */
    public void printTransaction(int num, String description, String amount, String date,
            String category) {
        System.out.printf("%-20s %-55s %-20s %-20s %-20s %n", num, description, amount, date, category);
    }

    /**
     * Prints the card header.
     */
    public void printCardHeader() {
        System.out.printf("%-20s %-35s %-15s %-20s %-15s %n", ITEMNO, CARD_NAME, MONTHLY_LIMIT,
                REMAINING_LIMIT, REBATE_RATE);

        System.out.println("-----------------------------------------------------------------------------"
                + "----------------------------------------------------------------");
    }

    /**
     * Prints the card details being specified.
     *
     * @param num      Represents the numbering of the card.
     * @param name     Represents the name of the card.
     * @param monLimit Represents the monthly limit of the card.
     * @param remLimit Represents the remaining card limit left for the card.
     * @param rebate   Represents the card rebate in percentage.
     */
    public void printCard(int num, String name, String monLimit, String remLimit, String rebate) {
        System.out.printf("%-20s %-35s %-15s %-20s %-15s %n", num, name, monLimit, remLimit, rebate);
    }

    /**
     * Prints the bank header.
     */
    public void printBankHeader() {
        System.out.printf("%-20s %-35s %-15s %-15s %-15s %n", ITEMNO, ACCOUNT_NAME, ACCOUNT_TYPE,
                CURRENT_AMOUNT, INCOME);

        System.out.println("-----------------------------------------------------------------------------"
                + "----------------------------------------------------------------");
    }

    /**
     * Prints the investment account details being specified.
     *
     * @param num    Represents the numbering of the investment account.
     * @param name   Represents the name of the investment account.
     * @param type   Represents the account type.
     * @param amount Represents the current amount in the listed investment account.
     */
    public void printInvestment(int num, String name, String type, String amount) {
        System.out.printf("%-20s %-35s %-15s %-15s %-15s %n", num, name, type, amount, "Not Applicable");
    }

    /**
     * Prints the saving account details being specified.
     *
     * @param num    Represents the numbering of the saving account.
     * @param name   Represents the name of the saving account.
     * @param type   Represents the account type.
     * @param amount Represents the current amount in the listed saving account.
     * @param income Represents the income in the listed saving account.
     */
    public void printSaving(int num, String name, String type, String amount, String income) {
        System.out.printf("%-20s %-35s %-15s %-15s %-15s %n", num, name, type, amount, income);
    }

    /**
     * Prints the goal header.
     */
    public void printGoalHeader() {
        System.out.printf("%-9s %-22s %-20s %-30s %-20s %-20s %-20s %n", ITEMNO, GOAL_NAME, GOAL_AMOUNT, GOAL_BANK_NAME,
                GOAL_PROGRESS, GOAL_DATE, GOAL_ACHIEVED);

        System.out.println("-----------------------------------------------------------------------------"
                + "---------------------------------------------------------------");
    }

    /**
     * Prints the goal details being specified.
     *
     * @param num    Represents the numbering of the goal.
     * @param name   Represents the goal name.
     * @param amount Represents the amount to save up in the goal.
     * @param date   Represents the date to accomplish the goal.
     */
    public void printGoal(int num, String name, String amount, String bankName, String progress, String date,
            String achieved) {
        System.out.printf("%-9s %-22s %-20s %-30s %-20s %-20s %-20s %n", num, name, amount, bankName, progress,
                date, achieved);
    }

    /**
     * Prints the achievement header.
     */
    public void printAchievementHeader() {
        System.out.printf("%-9s %-22s %-20s %-20s %n", ITEMNO, ACHIEVEMENT_NAME,
                ACHIEVEMENT_AMOUNT, ACHIEVEMENT_DATE);

        System.out.println("-----------------------------------------------------------------------------"
                + "----------------------------------------------------------------");
    }

    /**
     * Prints the achievement details being specified.
     *
     * @param num    Represents the numbering of the achievement.
     * @param name   Represents the achievement name.
     * @param amount Represents the amount to save up in the goal.
     * @param date   Represents the date to accomplish the goal.
     */
    public void printAchievement(int num, String name, String amount, String date) {
        System.out.printf("%-9s %-22s %-20s %-20s %n", num, name, amount, date);
    }

    /**
     * Prints the bond header.
     */
    public void printBondHeader() {
        System.out.printf("%-20s %-30s %-15s %-10s %-20s %-10s %n", ITEMNO, BOND_NAME, AMOUNT,
                BOND_RATE, BOND_DATE,
                BOND_YEAR);

        System.out.println("-----------------------------------------------------------------------------"
                + "----------------------------------------------------------------");
    }

    /**
     * Prints the bond details being specified.
     *
     * @param num    Represents the numbering of the bond.
     * @param name   Represents the bond name.
     * @param amount Represents the amount of the bond.
     * @param rate   Represents the rate for the bond.
     * @param date   Represents the date of purchase for the bond.
     * @param year   Represents the year for the bond.
     */
    public void printBond(int num, String name, String amount, String rate, String date, int year) {
        System.out.printf("%-20s %-30s %-15s %-10s %-20s %-10s %n", num, name, amount, rate, date, year);
    }

    /**
     * Prints the divider to separate the section of the output.
     */
    public void printDivider() {
        System.out.println("-----------------------------------------------------------------------------"
                + "----------------------------------------------------------------");
    }

    /**
     * Prints the divider to separate the section for Goals output.
     */
    public void printGoalDivider() {
        System.out.println("-----------------------------------------------------------------------------"
                + "---------------------------------------------------------------");
    }

    /**
     * Prints a greeting message to the user, which happens at startup.
     */
    public void greet(String username) {
        String welcomeLogo = NEWLINE
                + " $$$$$$\\                $$\\ $$\\      $$\\                                         "
                + NEWLINE
                + "$$  __$$\\               $$ |$$$\\    $$$ |                                        "
                + NEWLINE
                + "$$ /  $$ |$$\\  $$\\  $$\\ $$ |$$$$\\  $$$$ | $$$$$$\\  $$$$$$$\\   $$$$$$\\  $$\\   $$\\ "
                + NEWLINE
                + "$$ |  $$ |$$ | $$ | $$ |$$ |$$\\$$\\$$ $$ |$$  __$$\\ $$  __$$\\ $$  __$$\\ $$ |  $$ |"
                + NEWLINE
                + "$$ |  $$ |$$ | $$ | $$ |$$ |$$ \\$$$  $$ |$$ /  $$ |$$ |  $$ |$$$$$$$$ |$$ |  $$ |"
                + NEWLINE
                + "$$ |  $$ |$$ | $$ | $$ |$$ |$$ |\\$  /$$ |$$ |  $$ |$$ |  $$ |$$   ____|$$ |  $$ |"
                + NEWLINE
                + " $$$$$$  |\\$$$$$\\$$$$  |$$ |$$ | \\_/ $$ |\\$$$$$$  |$$ |  $$ |\\$$$$$$$\\ \\$$$$$$$ |"
                + NEWLINE
                + " \\______/  \\_____\\____/ \\__|\\__|     \\__| \\______/ \\__|  \\__| \\_______| "
                + "\\____$$ |"
                + NEWLINE
                + "                                                                       $$\\   $$ |"
                + NEWLINE
                + "                                                                       \\$$$$$$  |"
                + NEWLINE
                + "                                                                        \\______/ "
                + NEWLINE;
        printMessage(welcomeLogo);
        printMessage("Welcome " + username + "!");
        printMessage("What can I do for you?");
    }

    /**
     * Prompts the user for a username on first instance of running this program.
     */
    public void firstTimeRun() {
        printMessage("Please enter a username:");
    }

    /**
     * Prints an error message with the given content.
     *
     * @param exceptionMessage The specifics of the error.
     */
    public void printError(String exceptionMessage) {
        printMessage("OOPS!!! " + exceptionMessage);
    }


}
