package owlmoney.model.bank;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import owlmoney.model.bank.exception.BankException;
import owlmoney.model.bond.Bond;
import owlmoney.model.bond.exception.BondException;
import owlmoney.model.transaction.Transaction;
import owlmoney.model.transaction.exception.TransactionException;
import owlmoney.storage.Storage;
import owlmoney.ui.Ui;

/**
 * Contains a list of all bank objects in the profile.
 */
public class BankList {
    private ArrayList<Bank> bankLists;
    private Storage storage;
    private static final String SAVING = "saving";
    private static final String INVESTMENT = "investment";
    private static final int ONE_INDEX = 1;
    private static final int MAX_SAVINGS_LIMIT = 7;
    private static final int MAX_INVESTMENT_LIMIT = 3;
    private static final boolean ISMULTIPLE = true;
    private static final boolean ISSINGLE = false;
    private static final int ISZERO = 0;
    private static final String FILE_PATH = "data/";
    private static final String PROFILE_BANK_LIST_FILE_NAME = "profile_banklist.csv";
    private static final String INVESTMENT_BOND_LIST_FILE_NAME = "_investment_bondList.csv";
    private static final String INVESTMENT_TRANSACTION_LIST_FILE_NAME = "_investment_transactionList.csv";
    private static final String SAVING_TRANSACTION_LIST_FILE_NAME = "_saving_transactionList.csv";
    private static final String SAVING_RECURRING_TRANSACTION_LIST_FILE_NAME = "_saving_recurring_transactionList.csv";
    private static final Logger logger = getLogger(BankList.class);


    /**
     * Creates a instance of BankList that contains an arrayList of Banks.
     * @param storage for importing and exporting purposes.
     */
    public BankList(Storage storage) {
        bankLists = new ArrayList<Bank>();
        this.storage = storage;
    }

    /**
     * Gets the saving account with the specified name.
     *
     * @param bankName The name of the bank account in the arrayList.
     * @return The name of the bank account.
     */
    public Bank bankListGetSavingAccount(String bankName) throws BankException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = 0; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            String currentBankType = currentBank.getType();
            if (capitalBankName.equals(capitalCurrentBankName) && currentBankType.equals(SAVING)) {
                logger.info("Obtained savings account of name: " + currentBank.getAccountName());
                return currentBank;
            }
        }
        logger.warning("Cannot find savings account with the name: " + bankName);
        throw new BankException("Cannot find savings account with the name: " + bankName);
    }

    /**
     * Adds an instance of a bank account into the BankList.
     *
     * @param newBank a new bank object.
     * @param ui      required for printing.
     * @throws BankException If duplicate bank account name found.
     */
    public void bankListAddBank(Bank newBank, Ui ui) throws BankException {
        if (bankAccountExists(newBank.getAccountName())) {
            logger.warning("There is already a bank account with the name " + newBank.getAccountName());
            throw new BankException("There is already a bank account with the name " + newBank.getAccountName());
        }
        String accountType = newBank.getType();
        if (accountType.equals(SAVING) && getNumberOfAccountType(accountType) >= MAX_SAVINGS_LIMIT) {
            logger.warning("The maximum limit of 7 savings account has been reached");
            throw new BankException("The maximum limit of 7 savings account has been reached");
        } else if (accountType.equals(INVESTMENT) && getNumberOfAccountType(accountType) >= MAX_INVESTMENT_LIMIT) {
            logger.warning("The maximum limit of 3 investment account has been reached");
            throw new BankException("The maximum limit of 3 investment account has been reached");
        }
        bankLists.add(newBank);
        logger.info("Added bank account");
        ui.printMessage("Added new bank with following details: ");
        printOneBank(ONE_INDEX, newBank, ISSINGLE, ui);
        try {
            exportBankList();
        } catch (IOException e) {
            logger.warning("Error trying to save your additions of banks to disk. Your data is"
                    + " at risk, but we will try again, feel free to continue using the program.");
            ui.printError("Error trying to save your additions of banks to disk. Your data is"
                    + " at risk, but we will try again, feel free to continue using the program.");
        }
    }

    /**
     * Returns true if bankList is empty and false if there are banks stored in bankList.
     *
     * @return status of whether there are banks stored.
     */
    private boolean isEmpty() {
        return bankLists.isEmpty();
    }

    /**
     * Gets the size of the bankList which counts all types of accounts.
     *
     * @return size of bankList.
     */
    int getBankListSize() {
        return bankLists.size();
    }

    /**
     * Counts the number of bank accounts of the type specified.
     *
     * @param accountType The type of bank account
     * @return the number of accounts of the specified type.
     */
    private int getNumberOfAccountType(String accountType) {
        int counter = ISZERO;
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (accountType.equals(bankLists.get(i).getType())) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Checks if the bank name and type that the user specified is correct.
     *
     * @param bankName name of bank account.
     * @param bankType type of bank account.
     * @return the result bankName is of bankType.
     */
    private boolean hasCorrectBankNameAndType(String bankName, String bankType) {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)
                    && (bankType.equals(currentBank.getType()))) {
                logger.info("Bank account has correct name and type");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the bank name that the user specified exists.
     *
     * @param bankName name of bank account.
     * @return the result bankName exists.
     */
    private boolean bankAccountExists(String bankName) {
        String capitalName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalNameInList = currentBankName.toUpperCase();
            if (capitalName.equals(capitalNameInList)) {
                logger.info("Bank account exists");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the user passes all requirements to delete a bank account.
     *
     * @param bankName name of bank account.
     * @param bankType type of bank account.
     * @return the result bankName is of bankType.
     * @throws BankException If bank account fails any criteria.
     */
    private boolean canPassDeleteBankRequirements(String bankName, String bankType) throws BankException {
        if (isEmpty()) {
            logger.warning("There are 0 bank accounts in your profile");
            throw new BankException("There are 0 bank accounts in your profile");
        }
        if (bankType.equals(SAVING) && getNumberOfAccountType(SAVING) == 1) {
            logger.warning("There must be at least 1 savings account");
            throw new BankException("There must be at least 1 savings account");
        }
        if (!bankAccountExists(bankName)) {
            logger.warning("There are no bank accounts with name " + bankName);
            throw new BankException("There are no bank accounts with name " + bankName);
        }
        if (hasCorrectBankNameAndType(bankName, bankType)) {
            logger.info("Bank passed all checks");
            return true;
        } else {
            logger.warning(bankName + " is not of type: " + bankType);
            throw new BankException(bankName + " is not of type: " + bankType);
        }
    }

    /**
     * Deletes an instance of a bank account from the BankList.
     *
     * @param bankName name of the bank account.
     * @param bankType type of bank account.
     * @param ui       required for printing.
     * @throws BankException If bank account fails any criteria.
     */
    public void bankListDeleteBank(String bankName, String bankType, Ui ui) throws BankException {
        String capitalBankName = bankName.toUpperCase();
        if (canPassDeleteBankRequirements(bankName, bankType)) {
            for (int i = ISZERO; i < getBankListSize(); i++) {
                Bank currentBank = bankLists.get(i);
                String currentBankName = currentBank.getAccountName();
                String capitalCurrentBankName = currentBankName.toUpperCase();
                if (capitalBankName.equals(capitalCurrentBankName)) {
                    bankLists.remove(i);
                    logger.info("Successfully deleted bank");
                    ui.printMessage("Removed bank with the following details: ");
                    printOneBank(ONE_INDEX, currentBank, ISSINGLE, ui);
                    try {
                        exportBankList();
                        Files.deleteIfExists(Paths.get(FILE_PATH + Integer.toString(i)
                                + INVESTMENT_BOND_LIST_FILE_NAME));
                        Files.deleteIfExists(Paths.get(FILE_PATH + Integer.toString(i)
                                + INVESTMENT_TRANSACTION_LIST_FILE_NAME));
                        Files.deleteIfExists(Paths.get(FILE_PATH + Integer.toString(i)
                                + SAVING_TRANSACTION_LIST_FILE_NAME));
                        Files.deleteIfExists(Paths.get(FILE_PATH + Integer.toString(i)
                                + SAVING_RECURRING_TRANSACTION_LIST_FILE_NAME));
                        logger.info("Successfully saved all bank list");
                    } catch (IOException e) {
                        ui.printError("Error trying to save your deletions to disk."
                                + " Your data is at risk, but we will try again,"
                                + " feel free to continue using the program.");
                    }
                    break;
                }
            }
        }
    }

    /**
     * Edits the saving details.
     *
     * @param bankName Bank account to be edited.
     * @param newName  New name of bank account.
     * @param amount   New amount of bank account.
     * @param income   New income of bank account.
     * @param ui       required for printing.
     * @throws BankException If bank account does not exist or duplicate bank name.
     */
    public void bankListEditSavings(String bankName, String newName, String amount, String income, Ui ui)
            throws BankException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)
                    && "saving".equals(currentBank.getType())) {
                if (!(newName == null || newName.isBlank())) {
                    compareBank(currentBank, newName);
                    currentBank.setAccountName(newName);
                }
                if (!(amount == null || amount.isBlank())) {
                    currentBank.setCurrentAmount(Double.parseDouble(amount));
                }
                if (!(income == null || income.isBlank())) {
                    currentBank.setIncome(Double.parseDouble(income));
                }
                logger.info("Edited bank details");
                ui.printMessage("New details of the account:");
                printOneBank(ONE_INDEX, currentBank, ISSINGLE, ui);
                try {
                    exportBankList();
                    logger.info("Successfully exported bankList");
                } catch (IOException e) {
                    logger.warning("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("There are no bank with the name: " + bankName);
        throw new BankException("There are no bank with the name: " + bankName);
    }

    /**
     * Checks if new bank name is unique.
     *
     * @param currentBank The bank to be changed.
     * @param newBankName The new name of the bank.
     * @throws BankException If new name is not unique.
     */
    private void compareBank(Bank currentBank, String newBankName) throws BankException {
        String capitalNewBankName = newBankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank checkBank = bankLists.get(i);
            String checkBankName = checkBank.getAccountName();
            String capitalCheckBankName = checkBankName.toUpperCase();
            if (capitalCheckBankName.equals(capitalNewBankName) && !checkBank.equals(currentBank)) {
                logger.warning("There is already a bank account with the name " + newBankName);
                throw new BankException("There is already a bank account with the name " + newBankName);
            }
        }
    }

    /**
     * Edits the investment account details.
     *
     * @param bankName Bank account to be edited.
     * @param newName  New name of bank account.
     * @param amount   New amount of bank account.
     * @param ui       required for printing.
     * @throws BankException If bank account does not exist or duplicate bank name.
     */
    public void bankListEditInvestment(String bankName, String newName, String amount, Ui ui)
            throws BankException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)
                    && "investment".equals(bankLists.get(i).getType())) {
                if (!(newName == null || newName.isBlank())) {
                    compareBank(currentBank, newName);
                    currentBank.setAccountName(newName);
                }
                if (!(amount == null || amount.isBlank())) {
                    currentBank.setCurrentAmount(Double.parseDouble(amount));
                }
                logger.info("Edited investment details");
                ui.printMessage("New details of the account:");
                printOneBank(ONE_INDEX, currentBank, ISSINGLE, ui);
                try {
                    exportBankList();
                    logger.info("Successfully exported bankList");
                } catch (IOException e) {
                    logger.warning("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("There are no bank with the name: " + bankName);
        throw new BankException("There are no bank with the name: " + bankName);
    }

    /**
     * Lists all bank accounts in the BankList.
     *
     * @param ui required for printing.
     * @throws BankException If there are no specified bank accounts.
     */
    public void bankListListBankAccount(String bankType, Ui ui) throws BankException {
        if (getBankListSize() <= ISZERO) {
            logger.warning("There are 0 bank accounts");
            throw new BankException("There are 0 bank accounts");
        }
        int numberOfBanks = ISZERO;
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankType.equals(bankLists.get(i).getType())) {
                printOneHeader(numberOfBanks, ui);
                printOneBank(numberOfBanks + ONE_INDEX, bankLists.get(i), ISMULTIPLE, ui);
                numberOfBanks++;
            }
        }
        if (numberOfBanks == ISZERO) {
            logger.warning("There are 0 " + bankType + " accounts");
            throw new BankException("There are 0 " + bankType + " accounts");
        } else {
            ui.printDivider();
        }
    }

    /**
     * Lists expenditures in the bank account.
     *
     * @param bankToList The name of the bank account.
     * @param ui         required for printing.
     * @param expenditureToDisplay Number of expenditures to list.
     * @throws TransactionException If no expenditure is found.
     * @throws BankException        If bank name does not exist.
     */
    public void bankListListBankExpenditure(String bankToList, Ui ui, int expenditureToDisplay)
            throws TransactionException, BankException {
        String capitalBankToList = bankToList.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankToList.equals(capitalCurrentBankName)) {
                currentBank.listAllExpenditure(ui, expenditureToDisplay);
                return;
            }
            logger.info("Successfully listed bank expenditures");
        }
        logger.warning("Cannot find bank with name: " + bankToList);
        throw new BankException("Cannot find bank with name: " + bankToList);
    }

    /**
     * Lists deposits in the bank account.
     *
     * @param bankToList The name of the bank account.
     * @param ui         required for printing.
     * @param depositsToDisplay Number of deposits to list.
     * @throws TransactionException If no deposit is found.
     * @throws BankException        If bank account does not exist.
     */
    public void bankListListBankDeposit(String bankToList, Ui ui, int depositsToDisplay)
            throws TransactionException, BankException {
        String capitalBankToList = bankToList.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankToList.equals(capitalCurrentBankName)) {
                currentBank.listAllDeposit(ui, depositsToDisplay);
                return;
            }
            logger.info("Successfully listed deposits");
        }
        logger.warning("Cannot find bank with name: " + bankToList);
        throw new BankException("Cannot find bank with name: " + bankToList);
    }

    /**
     * Adds an expenditure tied to a bank account.
     * This will store the expenditure in the ExpenditureList in the bank account.
     *
     * @param accountName The Bank account name.
     * @param expenditure     The instance of the expenditure.
     * @param ui      Required for printing.
     * @param type    Type of bank to add expenditure into.
     * @throws BankException If bank account does not exist.
     */
    public void bankListAddExpenditure(String accountName, Transaction expenditure, Ui ui, String type)
            throws BankException {
        String capitalAccountName = accountName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalAccountName.equals(capitalCurrentBankName)) {
                currentBank.addInExpenditure(expenditure, ui, type);
                try {
                    exportBankList();
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("There is no account with the name: " + accountName);
        throw new BankException("There is no account with the name: " + accountName);
    }

    /**
     * Edits an expenditure from the transactionList in the bank account.
     *
     * @param transactionNumber       The transaction number.
     * @param editFromBank The name of the bank account.
     * @param description         The description of the expenditure.
     * @param amount       The amount of the expenditure.
     * @param date         The date of the expenditure.
     * @param category     The category of the expenditure.
     * @param ui           required for printing.
     * @throws BankException        If bank account does not exist.
     * @throws TransactionException If incorrect date format.
     */
    public void bankListEditExpenditure(int transactionNumber, String editFromBank, String description,
            String amount, String date, String category, Ui ui) throws BankException, TransactionException {
        String capitalEditFromBank = editFromBank.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalCurrentBankName.equals(capitalEditFromBank)) {
                currentBank.editExpenditureDetails(transactionNumber, description, amount, date, category, ui);
                try {
                    exportBankList();
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + editFromBank);
        throw new BankException("Cannot find bank with name: " + editFromBank);
    }

    /**
     * Deletes an expenditure from the transactionList in the bank account.
     *
     * @param transactionNumber         The transaction number.
     * @param deleteFromBank The name of the bank account.
     * @param ui             required for printing.
     * @param isCreditCardBill Is affecting a credit card bill.
     * @throws TransactionException If invalid transaction.
     * @throws BankException        If bank account does not exist.
     */
    public void bankListDeleteExpenditure(
            int transactionNumber, String deleteFromBank, Ui ui, boolean isCreditCardBill)
            throws TransactionException, BankException {
        String capitalDeleteFromBank = deleteFromBank.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalCurrentBankName.equals(capitalDeleteFromBank)) {
                currentBank.deleteExpenditure(transactionNumber, ui, isCreditCardBill);
                try {
                    exportBankList();
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your deletes to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + deleteFromBank);
        throw new BankException("Cannot find bank with name: " + deleteFromBank);
    }

    /**
     * Adds a deposit tied to a bank account.
     * This will store the expenditure in the transactionList in the bank account.
     *
     * @param accountName  The Bank account name.
     * @param deposit      The instance of the deposit.
     * @param ui       Required for printing.
     * @param bankType Type of bank to add deposit into
     * @throws BankException If bank name does not exist.
     */
    public void bankListAddDeposit(String accountName, Transaction deposit, Ui ui, String bankType)
            throws BankException {
        String capitalAccountName = accountName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalAccountName.equals(capitalCurrentBankName)) {
                currentBank.addDepositTransaction(deposit, ui, bankType);
                try {
                    exportBankList();
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + accountName);
        throw new BankException("Cannot find bank with name: " + accountName);

    }

    /**
     * Edits a deposit from the transactionList in the bank account.
     *
     * @param transactionNumber       The transaction number.
     * @param editFromBank The name of the bank account.
     * @param description         The description of the deposit.
     * @param amount       The amount of the deposit.
     * @param date         The date of the deposit.
     * @param ui           required for printing.
     * @throws BankException        If bank name does not exist.
     * @throws TransactionException If incorrect date format.
     */
    public void bankListEditDeposit(int transactionNumber, String editFromBank, String description,
            String amount, String date, Ui ui) throws BankException, TransactionException {
        String capitalEditFromBank = editFromBank.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalEditFromBank.equals(capitalCurrentBankName)) {
                currentBank.editDepositDetails(transactionNumber, description, amount, date, ui);
                try {
                    exportBankList();
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + editFromBank);
        throw new BankException("Cannot find bank with name: " + editFromBank);
    }

    /**
     * Deletes a deposit from the transactionList in the bank account.
     *
     * @param accountName The name of the bank account.
     * @param index   The transaction number.
     * @param ui      required for printing.
     * @param isCardBill Is affecting credit card bill deposit.
     * @throws BankException        If bank account does not exist.
     * @throws TransactionException If transaction is not a deposit.
     */
    public void bankListDeleteDeposit(String accountName, int index, Ui ui, boolean isCardBill)
            throws BankException, TransactionException {
        String capitalAccountName = accountName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalAccountName.equals(capitalCurrentBankName)) {
                currentBank.deleteDepositTransaction(index, ui, isCardBill);
                try {
                    exportBankList();
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your deletions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + accountName);
        throw new BankException("Cannot find bank with name: " + accountName);
    }

    /**
     * Checks if the bond exists before adding.
     *
     * @param accountName the bank account name.
     * @param bond    the bond object.
     * @throws BankException If bank does not exist.
     * @throws BondException If duplicate bond name found.
     */
    public void bankListIsBondExist(String accountName, Bond bond) throws BankException, BondException {
        String capitalAccountName = accountName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalAccountName.equals(capitalCurrentBankName)) {
                currentBank.investmentCheckBondExist(bond);
                logger.info(bond.getName() + " exists");
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + accountName);
        throw new BankException("Cannot find bank with name: " + accountName);
    }

    /**
     * Adds a bond to a bank account in the bankList.
     *
     * @param accountName name of bank account.
     * @param bond    bond object.
     * @param ui      required for printing.
     * @throws BankException If bank account does not exist.
     */
    public void bankListAddBond(String accountName, Bond bond, Ui ui) throws BankException {
        String capitalAccountName = accountName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalAccountName.equals(capitalCurrentBankName)) {
                currentBank.addBondToInvestmentAccount(bond, ui);
                try {
                    exportBankList();
                    currentBank.exportInvestmentBondList(Integer.toString(i));
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + accountName);
        throw new BankException("Cannot find bank with name: " + accountName);
    }

    /**
     * Edits the bond in the bank account.
     *
     * @param bankName the name of the bank.
     * @param bondName the name of the bond to edit.
     * @param year     the new year of the bond.
     * @param rate     the new rate
     * @param ui       required for printing.
     * @throws BankException if the bank does not exist.
     */
    public void bankListEditBond(String bankName, String bondName, String year, String rate, Ui ui)
            throws BankException, BondException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                currentBank.investmentEditBond(bondName, year, rate, ui);
                try {
                    exportBankList();
                    currentBank.exportInvestmentBondList(Integer.toString(i));
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Deletes bond from the bondList in the specified investment account.
     *
     * @param bankName the name of the bank account.
     * @param bondName the name of the bond to delete.
     * @param ui       required for printing.
     * @throws BankException if the bank is not found.
     */
    public void bankListDeleteBond(String bankName, String bondName, Ui ui) throws BankException, BondException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                currentBank.investmentDeleteBond(bondName, ui);
                try {
                    exportBankList();
                    currentBank.exportInvestmentBondList(Integer.toString(i));
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your deletions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Gets the bond object from the investment account if it exists.
     *
     * @param bankName the name of the bank account.
     * @param bondName the name of the bond to retrieve.
     * @return the bond object.
     * @throws BankException if the bank does not exist.
     * @throws BondException if the bond does not exist.
     */
    public Bond bankListGetBond(String bankName, String bondName) throws BankException, BondException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                return currentBank.investmentGetBond(bondName);
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Lists the bonds in the bank specified bank account.
     *
     * @param bankName   the name of the bank account.
     * @param ui         required for printing.
     * @param displayNum the number of bonds to display.
     * @throws BankException If bank account does not exist.
     * @throws BondException If there are no bonds.
     */
    public void bankListListBond(String bankName, Ui ui, int displayNum) throws BankException, BondException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                currentBank.investmentListBond(displayNum, ui);
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Prints bank details.
     *
     * @param num                Represents the numbering of the bank.
     * @param bank               The bank object to be printed.
     * @param isMultiplePrinting Represents whether the function will be called for printing once or multiple
     *                           time
     * @param ui                 The object use for printing.
     */
    private void printOneBank(int num, Bank bank, boolean isMultiplePrinting, Ui ui) throws BankException {
        if (!isMultiplePrinting) {
            ui.printBankHeader();
        }

        if (INVESTMENT.equals(bank.getType())) {
            ui.printInvestment(num, bank.getAccountName(), bank.getType(),
                    "$" + new DecimalFormat("0.00").format(bank.getCurrentAmount()));
        } else if (SAVING.equals(bank.getType())) {
            ui.printSaving(num, bank.getAccountName(), bank.getType(),
                    "$" + new DecimalFormat("0.00").format(bank.getCurrentAmount()),
                    "$" + new DecimalFormat("0.00").format(bank.getIncome()));
        }
        if (!isMultiplePrinting) {
            ui.printDivider();
        }
    }

    /**
     * Prints the bank header details once only when listing of multiple bank.
     *
     * @param num Represents the current number of bank being listed.
     * @param ui  The object use for printing.
     */
    private void printOneHeader(int num, Ui ui) {
        if (num == ISZERO) {
            ui.printBankHeader();
        }
    }

    /**
     * Adds a new recurring expenditure to the specified bank account.
     *
     * @param bankName                Name of bank account.
     * @param newRecurringExpenditure New recurring expenditure to be added.
     * @param ui                      Used for printing.
     * @throws BankException        If bank is not found or is an investment account.
     * @throws TransactionException If the recurring expenditure list is full.
     */
    public void bankListAddRecurringExpenditure(String bankName, Transaction newRecurringExpenditure, Ui ui)
            throws BankException, TransactionException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                currentBank.savingAddRecurringExpenditure(newRecurringExpenditure, ui);
                try {
                    exportBankList();
                    if (currentBank.getType().equals(INVESTMENT)) {
                        currentBank.exportInvestmentBondList(Integer.toString(i));
                    }
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    currentBank.exportBankRecurringTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Deletes the recurring expenditure of the specified index from the specified bank account.
     *
     * @param bankName Name of bank account.
     * @param index    Index of recurring expenditure.
     * @param ui       Used for printing.
     * @throws BankException        If bank is not found or is an investment account.
     * @throws TransactionException There are 0 recurring expenditures or index is out of range.
     */
    public void bankListDeleteRecurringExpenditure(String bankName, int index, Ui ui)
            throws BankException, TransactionException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                currentBank.savingDeleteRecurringExpenditure(index, ui);
                try {
                    exportBankList();
                    if (currentBank.getType().equals(INVESTMENT)) {
                        currentBank.exportInvestmentBondList(Integer.toString(i));
                    }
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    currentBank.exportBankRecurringTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your deletions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Lists all recurring expenditures from the specified bank account.
     *
     * @param bankName Name of bank account.
     * @param ui       Used for printing.
     * @throws BankException        If bank is not found or is an investment account.
     * @throws TransactionException There are 0 recurring expenditures.
     */
    public void bankListListRecurringExpenditure(String bankName, Ui ui)
            throws BankException, TransactionException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                currentBank.savingListRecurringExpenditure(ui);
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Updates the recurring expenditure of the specified index from the specified bank account.
     *
     * @param bankName Name of bank account.
     * @param index    Index of recurring expenditure.
     * @param ui       Used for printing.
     * @throws BankException        If bank is not found or is an investment account.
     * @throws TransactionException There are 0 recurring expenditures or index is out of range.
     */
    public void bankListEditRecurringExpenditure(
            String bankName, int index, String description, String amount, String category, Ui ui)
            throws BankException, TransactionException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            String currentBankType = currentBank.getType();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                currentBank.savingEditRecurringExpenditure(index, description, amount, category, ui);
                try {
                    exportBankList();
                    if (currentBankType.equals(INVESTMENT)) {
                        currentBank.exportInvestmentBondList(Integer.toString(i));
                    }
                    currentBank.exportBankTransactionList(Integer.toString(i));
                    currentBank.exportBankRecurringTransactionList(Integer.toString(i));
                    logger.info("Successfully exported " + currentBankName + " details");
                } catch (IOException e) {
                    logger.warning("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Updates all recurring transactions from all banks.
     *
     * @param ui Used for printing,
     * @throws BankException If income cannot be added.
     */
    public void bankListUpdateRecurringTransactions(Ui ui) throws BankException {
        for (int i = 0; i < getBankListSize(); i++) {
            bankLists.get(i).updateRecurringTransactions(ui);
            try {
                exportBankList();
                if (bankLists.get(i).getType().equals(INVESTMENT)) {
                    bankLists.get(i).exportInvestmentBondList(Integer.toString(i));
                }
                if (bankLists.get(i).getType().equals(SAVING)) {
                    bankLists.get(i).exportBankRecurringTransactionList(Integer.toString(i));
                }
                bankLists.get(i).exportBankTransactionList(Integer.toString(i));
            } catch (IOException | BankException e) {
                logger.warning("Error trying to save your additions to disk. Your data is"
                        + " at risk, but we will try again, feel free to continue using the program.");
                ui.printError("Error trying to save your updates to disk. Your data is"
                        + " at risk, but we will try again, feel free to continue using the program.");
            }
        }
    }

    /**
     * Checks whether the bank object to transfer the fund actually exist in the list.
     *
     * @param accountName the bank account name.
     * @param amount  the amount to transfer.
     * @throws BankException If bank does not exist.
     */
    public String getTransferBankType(String accountName, double amount) throws BankException {
        String capitalAccountName = accountName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalAccountName.equals(capitalCurrentBankName)) {
                checkSufficientForTransfer(bankLists.get(i), amount);
                logger.info("Successfully found bank type for transfer");
                return currentBank.getType();
            }
        }
        logger.warning("Unable to transfer fund as the sender bank account does not exist: "
                + accountName);
        throw new BankException("Unable to transfer fund as the sender bank account does not exist: "
                + accountName);
    }

    /**
     * Checks whether the bank object to receive the fund actually exist in the list.
     *
     * @param accountName the bank account name.
     * @throws BankException If bank does not exist.
     */
    public String getReceiveBankType(String accountName) throws BankException {
        String capitalAccountName = accountName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalAccountName.equals(capitalCurrentBankName)) {
                logger.info("Successfully found bank type for receive");
                return bankLists.get(i).getType();
            }
        }
        logger.warning("Unable to transfer fund as the receiving bank account does not exist: "
                + accountName);
        throw new BankException("Unable to transfer fund as the receiving bank account does not exist: "
                + accountName);
    }

    /**
     * Checks whether the bank object has sufficient amount to transfer.
     *
     * @param bank   the bank object.
     * @param amount the amount to be transferred.
     * @throws BankException If bank does not have sufficient fund.
     */
    private void checkSufficientForTransfer(Bank bank, double amount) throws BankException {
        if (bank.getCurrentAmount() >= amount) {
            return;
        }
        logger.warning("Insufficient amount for transfer in this bank: " + bank.getAccountName());
        throw new BankException("Insufficient amount for transfer in this bank: " + bank.getAccountName());
    }

    /**
     * Checks investment account specified by the user actually exist. If exist search for bond within the
     * investment account.
     *
     * @param bondName The bond name to be searched for.
     * @param investmentName The name of the investment account.
     * @param ui       The object required for printing.
     * @throws BankException If investment account does not exist.
     * @throws BondException If no bonds could be found.
     */
    public void checkInvestmentAccountExist(String bondName, String investmentName, Ui ui)
            throws BankException, BondException {
        String capitalInvestmentName = investmentName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalInvestmentName.equals(capitalCurrentBankName)
                    && INVESTMENT.equals(currentBank.getType())) {
                logger.info("Found Investment account to search for bonds");
                currentBank.findBondInInvestment(bondName, ui);
                return;
            }
        }
        logger.warning("Investment account with the following name "
                + "does not exist for search: " + investmentName);
        throw new BankException("Investment account with the following name "
                + "does not exist for search: " + investmentName);
    }

    /**
     * Finds either the savings or investment account that matches with the name specified by user.
     *
     * @param accountName The name to be matched against.
     * @param type    The type of object to find such as savings or investment object.
     * @throws BankException If there is no matches for saving or investment object.
     */
    public void findBankAccount(String accountName, String type, Ui ui) throws BankException {
        ArrayList<Bank> tempBankList = new ArrayList<Bank>();
        String matchingWord = accountName.toUpperCase();

        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalCurrentBankName.contains(matchingWord)
                    && type.equals(bankLists.get(i).getType())) {
                tempBankList.add(bankLists.get(i));
            }
        }
        logger.info("Search for bank account completed");
        if (tempBankList.isEmpty() && SAVING.equals(type)) {
            logger.warning("Savings account with the following keyword could not be found: "
                    + accountName);
            throw new BankException(
            "Savings account with the following keyword could not be found: " + accountName);
        } else if (tempBankList.isEmpty() && INVESTMENT.equals(type)) {
            logger.warning("Investment account with the following keyword could not be found: "
                    + accountName);
            throw new BankException(
            "Investment account with the following keyword could not be found: " + accountName);
        }

        for (int i = ISZERO; i < tempBankList.size(); i++) {
            printOneHeader(i, ui);
            printOneBank((i + ONE_INDEX), tempBankList.get(i), ISMULTIPLE, ui);
        }
        ui.printDivider();
        logger.info("Successfully found matching bank account.");
    }

    /**
     * Finds matching bank transactions from the account specified by the user.
     *
     * @param bankName    The name of the bank object to search for matching bank transaction.
     * @param fromDate    The date to search from.
     * @param toDate      The date to search until.
     * @param description The description keyword to match against.
     * @param category    The category keyword to match against.
     * @param ui          The object required for printing.
     * @throws BankException        If bank name specified does not exist.
     * @throws TransactionException If parsing of date fails.
     */
    public void bankListFindTransaction(String bankName, String fromDate, String toDate,
            String description, String category, Ui ui) throws BankException, TransactionException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                logger.info("Found bank account to search for transaction");
                currentBank.findTransaction(fromDate, toDate, description, category, ui);
                return;
            }
        }
        logger.warning("Bank with the following name does not exist: " + bankName);
        throw new BankException("Bank with the following name does not exist: " + bankName);
    }

    /**
     * Finds matching recurring expenditure from the savings account specified by the user.
     *
     * @param bankName    The name of the bank object to search for matching recurring expenditure.
     * @param description The description keyword to match against.
     * @param category    The category keyword to match against.
     * @param ui          The object required for printing.
     * @throws BankException If bank name specified does not exist.
     */
    public void bankListFindRecurringExpenditure(String bankName, String description, String category, Ui ui)
            throws BankException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            String currentBankType = currentBank.getType();
            if (capitalBankName.equals(capitalCurrentBankName) && SAVING.equals(currentBankType)) {
                logger.info("Found bank account to search for recurring expenditure");
                currentBank.findRecurringExpenditure(description, category, ui);
                return;
            }
        }
        logger.warning("Savings account with the following name does not exist: " + bankName);
        throw new BankException("Savings account with the following name does not exist: " + bankName);
    }

    /**
     * Prepares the bankList for exporting of bank name and type of the bank account.
     *
     * @return ArrayList of String arrays for containing each bank in the bank list.
     */
    private ArrayList<String[]> prepareExportBankListNamesAndType() throws BankException {
        ArrayList<String[]> exportArrayList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        SimpleDateFormat exportDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String nextIncomeDate = "";
        exportArrayList.add(new String[]{"accountName","type","amount","income","nextIncomeDate"});
        for (int i = 0; i < getBankListSize(); i++) {
            String accountType = bankLists.get(i).getType();
            nextIncomeDate = "";
            if (SAVING.equals(accountType)) {
                nextIncomeDate = exportDateFormat.format(bankLists.get(i).getNextIncomeDate());
            }
            double amount = bankLists.get(i).getCurrentAmount();
            double income = 0;
            String stringAmount = decimalFormat.format(amount);
            try {
                income = bankLists.get(i).getIncome();
            } catch (BankException e) {
                income = 0;
            }
            String accountName = bankLists.get(i).getAccountName();
            String stringIncome = decimalFormat.format(income);
            exportArrayList.add(new String[]{accountName,accountType,stringAmount,stringIncome,
                nextIncomeDate});
        }
        return exportArrayList;
    }

    /**
     * Writes the data of the bank list that was prepared to permanent storage.
     *
     * @throws IOException when unable to write to file.
     */
    private void exportBankList() throws IOException, BankException {
        ArrayList<String[]> inputData = prepareExportBankListNamesAndType();
        storage.writeFile(inputData,PROFILE_BANK_LIST_FILE_NAME);
    }

    /**
     * Imports bonds loaded from save file into respective investment accounts.
     *
     * @param bankName bank name the bond should be imported to.
     * @param newBond an instance of the bond to be imported.
     * @throws BankException if the bank account does not support this feature.
     */
    public void bankListImportNewBonds(String bankName, Bond newBond) throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankName.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).importNewBonds(newBond);
            }
        }
    }

    /**
     * Imports deposits loaded from save file into respective bank accounts.
     *
     * @param bankName bank name the deposits should be imported to.
     * @param deposit an instance of the deposit to be imported.
     * @param bankType the type of bank account.
     * @throws BankException if the bank account does not support this feature.
     */
    public void bankListImportNewDeposit(String bankName, Transaction deposit, String bankType)
            throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                bankLists.get(i).importNewDeposit(deposit, bankType);
            }
        }
    }

    /**
     * Imports expenditures loaded from save file into respective bank accounts.
     *
     * @param bankName bank name the deposits should be imported to.
     * @param expenditure an instance of the expenditure to be imported.
     * @param type type of expenditure.
     * @throws BankException if the bank account does not support this feature.
     */
    public void bankListImportNewExpenditure(String bankName, Transaction expenditure, String type)
            throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                bankLists.get(i).importNewExpenditure(expenditure,type);
                return;
            }
        }
        logger.warning("There is no account with the name: " + bankName);
        throw new BankException("There is no account with the name: " + bankName);
    }

    /**
     * Imports banks loaded from save file into bankList.
     *
     * @param newBank an instance of the bank account to be imported.
     */
    public void bankListImportNewBank(Bank newBank) {
        bankLists.add(newBank);
    }

    /**
     * Imports recurring expenditures from save file into respective bank accounts.
     *
     * @param bankName bank name the deposits should be imported to.
     * @param newRecurringExpenditure an instance of the expenditure to be imported.
     * @throws BankException if the bank account does not support this feature.
     */
    public void bankListImportNewRecurringExpenditure(String bankName, Transaction newRecurringExpenditure)
            throws BankException {
        for (int i = 0; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                bankLists.get(i).importNewRecurringExpenditure(newRecurringExpenditure);
            }
        }
    }

    /**
     * Checks if the bond list of the specified bank is full.
     *
     * @param bankName Name of investment account.
     * @return If the bond list from the specified investment account is full.
     * @throws BankException If used on savings account or investment account does not exist.
     */
    public boolean bankListIsBondListFull(String bankName) throws BankException {
        for (int i = 0; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                return bankLists.get(i).investmentIsBondListFull();
            }
        }
        logger.warning("Cannot find bank with name: " + bankName);
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Gets the index of the transaction object in specific bank that is a card bill expenditure
     * with specified card id and bill date.
     *
     * @param bankName  The name of the bank to search for transaction id.
     * @param cardId    The bill card id to look for in transaction list.
     * @param billDate  The bill card date to look for in transaction list.
     * @return Index of the expenditure transaction object if found. If not found, return -1.
     * @throws BankException If bank account does not support this feature.
     */
    public int bankListGetCardBillExpenditureId(String bankName, UUID cardId, YearMonth billDate)
            throws BankException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                return currentBank.getCardBillExpenditureId(cardId, billDate);
            }
        }
        return -1;
    }

    /**
     * Gets the index of the transaction object in specific bank that is a card bill deposit
     * with specified card id and bill date.
     *
     * @param bankName  The name of the bank to search for transaction id.
     * @param cardId    The bill card id to look for in transaction list.
     * @param billDate  The bill card date to look for in transaction list.
     * @return Index of the deposit transaction object if found. If not found, return -1.
     * @throws BankException If bank account does not support this feature.
     */
    public int bankListGetCardBillDepositId(String bankName, UUID cardId, YearMonth billDate)
            throws BankException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                return currentBank.getCardBillDepositId(cardId, billDate);
            }
        }
        return -1;
    }

    /**
     * Checks if the receiving bank can receive the amount from the transfer without overflowing.
     *
     * @param bankName Receiving bank name.
     * @param amount Amount to receive.
     * @throws BankException If bank amount overflows after receiving.
     */
    public void bankListCheckTransferExceed(String bankName, double amount) throws BankException {
        String capitalBankName = bankName.toUpperCase();
        for (int i = ISZERO; i < getBankListSize(); i++) {
            Bank currentBank = bankLists.get(i);
            String currentBankName = currentBank.getAccountName();
            String capitalCurrentBankName = currentBankName.toUpperCase();
            if (capitalBankName.equals(capitalCurrentBankName)) {
                currentBank.enoughForTransfer(amount);
            }
        }
    }
}
