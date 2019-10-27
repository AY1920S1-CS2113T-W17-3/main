package owlmoney.model.bank;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
    private static final boolean ISMULTIPLE = true;
    private static final boolean ISSINGLE = false;
    private static final int ISZERO = 0;
    private static final String FILE_PATH = "data/";
    private static final String PROFILE_BANK_LIST_FILE_NAME = "profile_banklist.csv";
    private static final String INVESTMENT_BOND_LIST_FILE_NAME = "_investment_bondList.csv";
    private static final String INVESTMENT_TRANSACTION_LIST_FILE_NAME = "_investment_transactionList.csv";
    private static final String SAVING_TRANSACTION_LIST_FILE_NAME = "_saving_transactionList.csv";
    private static final String SAVING_RECURRING_TRANSACTION_LIST_FILE_NAME = "_saving_recurring_transactionList.csv";


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
        for (int i = 0; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName) && bankLists.get(i).getType().equals(SAVING)) {
                return bankLists.get(i);
            }
        }
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
            throw new BankException("There is already a bank account with the name " + newBank.getAccountName());
        }
        bankLists.add(newBank);
        ui.printMessage("Added new bank with following details: ");
        printOneBank(ONE_INDEX, newBank, ISSINGLE, ui);
        prepareExportBankListNamesAndType();
        try {
            exportBankList();
        } catch (IOException e) {
            ui.printError("Error trying to save your additions to disk. Your data is"
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
    public int getBankListSize() {
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
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if ((bankName.equals(bankLists.get(i).getAccountName()))
                    && (bankType.equals(bankLists.get(i).getType()))) {
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
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankName.equals(bankLists.get(i).getAccountName())) {
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
            throw new BankException("There are 0 bank accounts in your profile");
        }
        if (bankType.equals(SAVING) && getNumberOfAccountType(SAVING) == 1) {
            throw new BankException("There must be at least 1 savings account");
        }
        if (!bankAccountExists(bankName)) {
            throw new BankException("There are no bank accounts with name " + bankName);
        }
        if (hasCorrectBankNameAndType(bankName, bankType)) {
            return true;
        } else {
            throw new BankException(bankName + " is not not of type: " + bankType);
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
        if (canPassDeleteBankRequirements(bankName, bankType)) {
            for (int i = ISZERO; i < getBankListSize(); i++) {
                if (bankName.equals(bankLists.get(i).getAccountName())) {
                    Bank temp = bankLists.get(i);
                    bankLists.remove(i);
                    ui.printMessage("Removed bank with the following details: ");
                    printOneBank(ONE_INDEX, temp, ISSINGLE, ui);
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
     * @throws BankException If bank account does not exist.
     */
    public void bankListEditSavings(String bankName, String newName, String amount, String income, Ui ui)
            throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)
                    && "saving".equals(bankLists.get(i).getType())) {
                if (!(newName.isEmpty() || newName.isBlank())) {
                    compareBank(bankLists.get(i), newName);
                    bankLists.get(i).setAccountName(newName);
                }
                if (!(amount.isBlank() || amount.isEmpty())) {
                    bankLists.get(i).setCurrentAmount(Double.parseDouble(amount));
                }
                if (!(income.isEmpty() || income.isBlank())) {
                    bankLists.get(i).setIncome(Double.parseDouble(income));
                }
                ui.printMessage("New details of the account:");
                printOneBank(ONE_INDEX, bankLists.get(i), ISSINGLE, ui);
                try {
                    exportBankList();
                } catch (IOException e) {
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
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
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(newBankName) && !bankLists.get(i).equals(currentBank)) {
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
     * @throws BankException If duplicate bank name found.
     */
    public void bankListEditInvestment(String bankName, String newName, String amount, Ui ui)
            throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)
                    && "investment".equals(bankLists.get(i).getType())) {
                if (!(newName.isEmpty() || newName.isBlank())) {
                    compareBank(bankLists.get(i), newName);
                    bankLists.get(i).setAccountName(newName);
                }
                if (!(amount.isBlank() || amount.isEmpty())) {
                    bankLists.get(i).setCurrentAmount(Double.parseDouble(amount));
                }
                ui.printMessage("New details of the account:");
                printOneBank(ONE_INDEX, bankLists.get(i), ISSINGLE, ui);
                try {
                    exportBankList();
                } catch (IOException e) {
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                break;
            }
        }
    }

    /**
     * Lists all bank accounts in the BankList.
     *
     * @param ui required for printing.
     * @throws BankException If there are no specified bank accounts.
     */
    public void bankListListBankAccount(String bankType, Ui ui) throws BankException {
        if (getBankListSize() <= ISZERO) {
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
     * @param displayNum Number of expenditures to list.
     * @throws TransactionException If no expenditure is found.
     * @throws BankException        If bank name does not exist.
     */
    public void bankListListBankExpenditure(String bankToList, Ui ui, int displayNum)
            throws TransactionException, BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankToList.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).listAllExpenditure(ui, displayNum);
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + bankToList);
    }

    /**
     * Lists deposits in the bank account.
     *
     * @param bankToList The name of the bank account.
     * @param ui         required for printing.
     * @param displayNum Number of deposits to list.
     * @throws TransactionException If no deposit is found.
     * @throws BankException        If bank account does not exist.
     */
    public void bankListListBankDeposit(String bankToList, Ui ui, int displayNum)
            throws TransactionException, BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankToList.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).listAllDeposit(ui, displayNum);
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + bankToList);
    }

    /**
     * Adds an expenditure tied to a bank account.
     * This will store the expenditure in the ExpenditureList in the bank account.
     *
     * @param accName The Bank account name.
     * @param exp     The instance of the expenditure.
     * @param ui      Required for printing.
     * @param type    Type of bank to add expenditure into.
     * @throws BankException If bank account does not exist.
     */
    public void bankListAddExpenditure(String accName, Transaction exp, Ui ui, String type)
            throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(accName)) {
                bankLists.get(i).addInExpenditure(exp, ui, type);
                try {
                    exportBankList();
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        throw new BankException("There is no account with the name: " + accName);
    }

    /**
     * Edits an expenditure from the transactionList in the bank account.
     *
     * @param expNum       The transaction number.
     * @param editFromBank The name of the bank account.
     * @param desc         The description of the expenditure.
     * @param amount       The amount of the expenditure.
     * @param date         The date of the expenditure.
     * @param category     The category of the expenditure.
     * @param ui           required for printing.
     * @throws BankException        If bank account does not exist.
     * @throws TransactionException If incorrect date format.
     */
    public void bankListEditExpenditure(int expNum, String editFromBank, String desc,
            String amount, String date, String category, Ui ui) throws BankException, TransactionException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(editFromBank)) {
                bankLists.get(i).editExpenditureDetails(expNum, desc, amount, date, category, ui);
                try {
                    exportBankList();
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + editFromBank);
    }

    /**
     * Deletes an expenditure from the transactionList in the bank account.
     *
     * @param expNum         The transaction number.
     * @param deleteFromBank The name of the bank account.
     * @param ui             required for printing.
     * @throws TransactionException If invalid transaction.
     * @throws BankException        If bank account does not exist.
     */
    public void bankListDeleteExpenditure(int expNum, String deleteFromBank, Ui ui)
            throws TransactionException, BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (deleteFromBank.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).deleteExpenditure(expNum, ui);
                try {
                    exportBankList();
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your deletes to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + deleteFromBank);
    }

    /**
     * Adds a deposit tied to a bank account.
     * This will store the expenditure in the transactionList in the bank account.
     *
     * @param accName  The Bank account name.
     * @param dep      The instance of the deposit.
     * @param ui       Required for printing.
     * @param bankType Type of bank to add deposit into
     * @throws BankException If bank name does not exist.
     */
    public void bankListAddDeposit(String accName, Transaction dep, Ui ui, String bankType) throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(accName)) {
                bankLists.get(i).addDepositTransaction(dep, ui, bankType);
                try {
                    exportBankList();
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + accName);

    }

    /**
     * Edits a deposit from the transactionList in the bank account.
     *
     * @param expNum       The transaction number.
     * @param editFromBank The name of the bank account.
     * @param desc         The description of the deposit.
     * @param amount       The amount of the deposit.
     * @param date         The date of the deposit.
     * @param ui           required for printing.
     * @throws BankException        If bank name does not exist.
     * @throws TransactionException If incorrect date format.
     */
    public void bankListEditDeposit(int expNum, String editFromBank, String desc,
            String amount, String date, Ui ui) throws BankException, TransactionException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(editFromBank)) {
                bankLists.get(i).editDepositDetails(expNum, desc, amount, date, ui);
                try {
                    exportBankList();
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + editFromBank);
    }

    /**
     * Deletes a deposit from the transactionList in the bank account.
     *
     * @param accName The name of the bank account.
     * @param index   The transaction number.
     * @param ui      required for printing.
     * @throws BankException        If bank account does not exist.
     * @throws TransactionException If transaction is not a deposit.
     */
    public void bankListDeleteDeposit(String accName, int index, Ui ui) throws BankException, TransactionException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(accName)) {
                bankLists.get(i).deleteDepositTransaction(index, ui);
                try {
                    exportBankList();
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your deletions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + accName);
    }

    /**
     * Checks if the bond exists before adding.
     *
     * @param accName the bank account name.
     * @param bond    the bond object.
     * @throws BankException If bank does not exist.
     * @throws BondException If duplicate bond name found.
     */
    public void bankListIsBondExist(String accName, Bond bond) throws BankException, BondException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (accName.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).investmentCheckBondExist(bond);
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + accName);
    }

    /**
     * Adds a bond to a bank account in the bankList.
     *
     * @param accName name of bank account.
     * @param bond    bond object.
     * @param ui      required for printing.
     * @throws BankException If bank account does not exist.
     */
    public void bankListAddBond(String accName, Bond bond, Ui ui) throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (accName.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).addBondToInvestmentAccount(bond, ui);
                try {
                    exportBankList();
                    bankLists.get(i).exportInvestmentBondList(Integer.toString(i));
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + accName);
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
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankName.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).investmentEditBond(bondName, year, rate, ui);
                try {
                    exportBankList();
                    bankLists.get(i).exportInvestmentBondList(Integer.toString(i));
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
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
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankName.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).investmentDeleteBond(bondName, ui);
                try {
                    exportBankList();
                    bankLists.get(i).exportInvestmentBondList(Integer.toString(i));
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your deletions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
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
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankName.equals(bankLists.get(i).getAccountName())) {
                return bankLists.get(i).investmentGetBond(bondName);
            }
        }
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
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankName.equals(bankLists.get(i).getAccountName())) {
                bankLists.get(i).investmentListBond(displayNum, ui);
                return;
            }
        }
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
     * Retrieves the total amount in Bank Saving.
     *
     * @param savingName Represents the account name of Saving.
     * @return The total amount in Saving account.
     * @throws BankException If no bank of such name is found.
     */
    public double getSavingAmount(String savingName) throws BankException {
        for (int i = 0; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(savingName)) {
                return bankLists.get(i).getCurrentAmount();
            }
        }
        throw new BankException("Cannot find bank with name: " + savingName);
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
        for (int i = 0; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                bankLists.get(i).savingAddRecurringExpenditure(newRecurringExpenditure, ui);
                try {
                    exportBankList();
                    if (bankLists.get(i).getType().equals(INVESTMENT)) {
                        bankLists.get(i).exportInvestmentBondList(Integer.toString(i));
                    }
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                    bankLists.get(i).exportBankRecurringTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your additions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
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
        for (int i = 0; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                bankLists.get(i).savingDeleteRecurringExpenditure(index, ui);
                try {
                    exportBankList();
                    if (bankLists.get(i).getType().equals(INVESTMENT)) {
                        bankLists.get(i).exportInvestmentBondList(Integer.toString(i));
                    }
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                    bankLists.get(i).exportBankRecurringTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your deletions to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
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
        for (int i = 0; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                bankLists.get(i).savingListRecurringExpenditure(ui);
                return;
            }
        }
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
        for (int i = 0; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                bankLists.get(i).savingEditRecurringExpenditure(index, description, amount, category, ui);
                try {
                    exportBankList();
                    if (bankLists.get(i).getType().equals(INVESTMENT)) {
                        bankLists.get(i).exportInvestmentBondList(Integer.toString(i));
                    }
                    bankLists.get(i).exportBankTransactionList(Integer.toString(i));
                    bankLists.get(i).exportBankRecurringTransactionList(Integer.toString(i));
                } catch (IOException e) {
                    ui.printError("Error trying to save your edits to disk. Your data is"
                            + " at risk, but we will try again, feel free to continue using the program.");
                }
                return;
            }
        }
        throw new BankException("Cannot find bank with name: " + bankName);
    }

    /**
     * Updates all recurring transactions from all banks.
     *
     * @param ui Used for printing,
     */
    public void bankListUpdateRecurringTransactions(Ui ui) {
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
                ui.printError("Error trying to save your updates to disk. Your data is"
                        + " at risk, but we will try again, feel free to continue using the program.");
            }
        }
    }

    /**
     * Checks whether the bank object to transfer the fund actually exist in the list.
     *
     * @param accName the bank account name.
     * @param amount  the amount to transfer.
     * @throws BankException If bank does not exist.
     */
    public String bankListIsAccountExistToTransfer(String accName, double amount) throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (accName.equals(bankLists.get(i).getAccountName())) {
                bankListIsSufficientForTransfer(bankLists.get(i), amount);
                return bankLists.get(i).getType();
            }
        }
        throw new BankException("Unable to transfer fund as bank the sender bank account does not exist: "
                + accName);
    }

    /**
     * Checks whether the bank object to receive the fund actually exist in the list.
     *
     * @param accName the bank account name.
     * @throws BankException If bank does not exist.
     */
    public String bankListIsAccountExistToReceive(String accName) throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (accName.equals(bankLists.get(i).getAccountName())) {
                return bankLists.get(i).getType();
            }
        }
        throw new BankException("Unable to transfer fund as the receiving bank account does not exist: "
                + accName);
    }

    /**
     * Checks whether the bank object has sufficient amount to transfer.
     *
     * @param bank   the bank object.
     * @param amount the amount to be transferred.
     * @throws BankException If bank does not have sufficient fund.
     */
    private void bankListIsSufficientForTransfer(Bank bank, double amount) throws BankException {
        if (bank.getCurrentAmount() >= amount) {
            return;
        }
        throw new BankException("Insufficient amount for transfer in this bank: " + bank.getAccountName());
    }

    /**
     * Checks investment account specified by the user actually exist.
     *
     * @param investmentName The name of the investment account.
     * @return The investment account object will be return if found.
     * @throws BankException If investment account does not exist.
     */
    public Bank checkInvestmentAccountExist(String investmentName) throws BankException {
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (investmentName.equals(bankLists.get(i).getAccountName())
                    && INVESTMENT.equals(bankLists.get(i).getType())) {
                return bankLists.get(i);
            }
        }
        throw new BankException("Investment account with the following name "
                + "does not exist for search: " + investmentName);
    }

    /**
     * Finds either the savings or investment account that matches with the name specified by user.
     *
     * @param accName The name to be matched against.
     * @param type    The type of object to find such as savings or investment object.
     * @throws BankException If there is no matches for saving or investment object.
     */
    public void findBankAccount(String accName, String type, Ui ui) throws BankException {
        ArrayList<Bank> tempBankList = new ArrayList<Bank>();
        String matchingWord = accName.toUpperCase();

        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().toUpperCase().contains(matchingWord)
                    && type.equals(bankLists.get(i).getType())) {
                tempBankList.add(bankLists.get(i));
            }
        }
        if (tempBankList.isEmpty() && SAVING.equals(type)) {
            throw new BankException(
            "Savings account with the following keyword could not be found: " + accName);
        } else if (tempBankList.isEmpty() && INVESTMENT.equals(type)) {
            throw new BankException(
            "Investment account with the following keyword could not be found: " + accName);
        }

        for (int i = ISZERO; i < tempBankList.size(); i++) {
            printOneHeader(i, ui);
            printOneBank((i + ONE_INDEX), tempBankList.get(i), ISMULTIPLE, ui);
        }
        ui.printDivider();
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
        for (int i = ISZERO; i < getBankListSize(); i++) {
            if (bankLists.get(i).getAccountName().equals(bankName)) {
                bankLists.get(i).findTransaction(fromDate, toDate, description, category, ui);
                return;
            }
        }
        throw new BankException("Bank with the following name does not exist: " + bankName);
    }

    /**
     * Prepares the bankList for exporting of bank name and type of the bank account.
     *
     * @return ArrayList of String arrays for containing each bank in the bank list.
     */
    private ArrayList<String[]> prepareExportBankListNamesAndType() {
        ArrayList<String[]> exportArrayList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        exportArrayList.add(new String[]{"accountName","type","amount","income"});
        for (int i = 0; i < getBankListSize(); i++) {
            String accountName = bankLists.get(i).getAccountName();
            String accountType = bankLists.get(i).getType();
            double amount = bankLists.get(i).getCurrentAmount();
            double income = 0;
            String stringAmount = decimalFormat.format(amount);
            try {
                income = bankLists.get(i).getIncome();
            } catch (BankException e) {
                income = 0;
            }
            String stringIncome = decimalFormat.format(income);
            exportArrayList.add(new String[]{accountName,accountType,stringAmount,stringIncome});
        }
        return exportArrayList;
    }

    /**
     * Writes the data of the bank list that was prepared to permanent storage.
     *
     * @throws IOException when unable to write to file.
     */
    private void exportBankList() throws IOException {
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
        throw new BankException("There is no account with the name: " + bankName);
    }

    /**
     * Imports banks loaded from save file into bankList.
     * @param newBank an instance of the bank account to be imported.
     */
    public void bankListImportNewBank(Bank newBank) {
        bankLists.add(newBank);
    }

    /**
     * Imports recurring expenditures from save file into respective bank accounts.
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
}
