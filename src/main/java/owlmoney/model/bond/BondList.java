package owlmoney.model.bond;

import java.text.DecimalFormat;
import java.util.ArrayList;

import owlmoney.model.bond.exception.BondException;
import owlmoney.ui.Ui;

/**
 * Provides a layer of abstraction for the ArrayList that stores bond objects.
 */
public class BondList {
    private ArrayList<Bond> bondLists;
    private static final int ONE_INDEX = 1;
    private static final boolean ISMULTIPLE = true;
    private static final boolean ISSINGLE = false;
    private static final int ISZERO = 0;
    private static final int MAX_LIST_SIZE = 20;

    /**
     * Creates an arrayList of bonds.
     */
    public BondList() {
        bondLists = new ArrayList<Bond>();
    }

    /**
     * Lists the bonds in the bondList.
     *
     * @param displayNum bond number.
     * @param ui         required for display.
     * @throws BondException if there are no bonds.
     */
    public void listBond(int displayNum, Ui ui) throws BondException {
        if (getSize() <= ISZERO) {
            throw new BondException("There are no bonds");
        } else {
            int counter = displayNum;
            for (int i = bondLists.size() - 1; i >= ISZERO; i--) {
                printOneHeader(counter, displayNum, ui);
                printOneBond((i + ONE_INDEX), bondLists.get(i), ISMULTIPLE, ui);
                counter--;
                if (counter <= ISZERO || i == ISZERO) {
                    ui.printDivider();
                }
                if (counter <= ISZERO) {
                    break;
                }
            }
        }
    }

    /**
     * Add bond to list.
     *
     * @param bond bond object.
     * @param ui   required for printing.
     */
    public void addBondToList(Bond bond, Ui ui) {
        bondLists.add(bond);
        ui.printMessage("Bond with the following details has been added: ");
        printOneBond(ONE_INDEX, bond, ISSINGLE, ui);
    }

    /**
     * Gets the size of the bondList.
     *
     * @return the size of the bondList.
     */
    public int getSize() {
        return bondLists.size();
    }

    /**
     * Checks if the bond exists.
     *
     * @param bond the bond object that the user is expecting to add.
     * @throws BondException If duplicate bond name is found.
     */
    public void bondExist(Bond bond) throws BondException {
        String bondName = bond.getName();
        String capitalBondName = bondName.toUpperCase();
        for (int i = ISZERO; i < getSize(); i++) {
            Bond currentBond = bondLists.get(i);
            String currentBondName = currentBond.getName();
            String capitalCurrentBondName = currentBondName.toUpperCase();
            if (capitalBondName.equals(capitalCurrentBondName)) {
                throw new BondException("Bond with the name: " + bond.getName() + " already exists");
            }
        }
    }

    /**
     * Removes the bond from the bondList.
     *
     * @param bondName the name of the bond.
     * @param ui       required for printing.
     */
    public void removeBondFromList(String bondName, Ui ui) throws BondException {
        if (getSize() == ISZERO) {
            throw new BondException("There are no bonds");
        }
        String capitalBondName = bondName.toUpperCase();
        for (int i = ISZERO; i < getSize(); i++) {
            Bond currentBond = bondLists.get(i);
            String currentBondName = currentBond.getName();
            String capitalCurrentBondName = currentBondName.toUpperCase();
            if (capitalBondName.equals(capitalCurrentBondName)) {
                bondLists.remove(i);
                ui.printMessage("Bond with the following details has been deleted: ");
                printOneBond(ONE_INDEX, currentBond, ISSINGLE, ui);
                return;
            }
        }
        throw new BondException("There are no bonds with the name: " + bondName);
    }

    /**
     * Gets the bond object from the bondList by specifying the name.
     *
     * @param bondName the name of the bond to retrieve.
     * @return the bond object.
     * @throws BondException if the bond does not exist.
     */
    public Bond getBond(String bondName) throws BondException {
        String capitalBondName = bondName.toUpperCase();
        for (int i = ISZERO; i < getSize(); i++) {
            Bond currentBond = bondLists.get(i);
            String currentBondName = currentBond.getName();
            String capitalCurrentBondName = currentBondName.toUpperCase();
            if (capitalBondName.equals(capitalCurrentBondName)) {
                return currentBond;
            }
        }
        throw new BondException("There are no bonds with the name: " + bondName);
    }

    /**
     * Gets the bond object from the bondList by specifying the bond index in the bondList.
     *
     * @return the bond object.
     */
    public Bond get(int bondIndex) {
        return bondLists.get(bondIndex);
    }

    /**
     * Edits the bond details specifically.
     *
     * @param bondName the name of the bond to retrieve.
     * @param year     the new year of the bond.
     * @param rate     the new rate of the bond.
     * @param ui       required for printing.
     * @throws BondException If the bond does not exist or the year is smaller than the original.
     */
    public void editBond(String bondName, String year, String rate, Ui ui) throws BondException {
        String capitalBondName = bondName.toUpperCase();
        for (int i = ISZERO; i < getSize(); i++) {
            Bond currentBond = bondLists.get(i);
            String currentBondName = currentBond.getName();
            String capitalCurrentBondName = currentBondName.toUpperCase();
            if (capitalBondName.equals(capitalCurrentBondName)) {
                editBondYear(year, i);
                editBondRate(rate, i);
                ui.printMessage("Bond with the following details has been edited: ");
                printOneBond(ONE_INDEX, bondLists.get(i), ISSINGLE, ui);
                return;
            }
        }
        throw new BondException("There are no bonds with the name: " + bondName);
    }

    /**
     * Edits the bond rate specifically to a new rate.
     *
     * @param rate the new interest rate of the bond.
     * @param i    position of the bond in the bondList.
     */
    private void editBondRate(String rate, int i) {
        if (!(rate.isEmpty() || rate.isBlank())) {
            bondLists.get(i).setRate(Double.parseDouble(rate));
        }
    }

    /**
     * Edits the year of the bond.
     *
     * @param year the new year of the bond.
     * @param i    position of the bond in the bondList.
     * @throws BondException if the year is smaller than the original year.
     */
    private void editBondYear(String year, int i) throws BondException {
        if (!(year.isEmpty() || year.isBlank())) {
            int originalYear = bondLists.get(i).getYear();
            if (Integer.parseInt(year) < originalYear) {
                throw new BondException("The year can only be larger than: " + originalYear);
            }
            bondLists.get(i).setYear(Integer.parseInt(year));
        }
    }

    /**
     * Prints bond details.
     *
     * @param num                Represents the numbering of the bond.
     * @param bond               The bond object to be printed.
     * @param isMultiplePrinting Represents whether the function will be called for printing once or
     *                           multiple time
     * @param ui                 The object use for printing.
     */
    private void printOneBond(int num, Bond bond, boolean isMultiplePrinting, Ui ui) {
        if (!isMultiplePrinting) {
            ui.printBondHeader();
        }
        ui.printBond(num, bond.getName(),
                "$" + new DecimalFormat("0.00").format(bond.getAmount()),
                new DecimalFormat("0.00").format(bond.getYearlyCouponRate()),
                bond.getDate(), bond.getYear());
        if (!isMultiplePrinting) {
            ui.printDivider();
        }
    }

    /**
     * Prints the bond header details once only when listing of multiple transaction.
     *
     * @param counter    Represents the counter of the bond for printing.
     * @param displayNum Represents number of bond to list.
     * @param ui         The object use for printing.
     */
    private void printOneHeader(int counter, int displayNum, Ui ui) {
        if (counter == displayNum) {
            ui.printBondHeader();
        }
    }

    /**
     * Finds the bonds that matches with the keywords specified by the user.
     *
     * @param bondName The bondName keyword to match against.
     * @param ui      The object required for printing.
     * @throws BondException If no bonds could be found.
     */
    public void findBondInBondList(String bondName, Ui ui) throws BondException {
        ArrayList<Bond> tempBondList = new ArrayList<Bond>();
        String matchingWord = bondName.toUpperCase();

        for (int i = ISZERO; i < getSize(); i++) {
            Bond currentBond = bondLists.get(i);
            String currentBondName = currentBond.getName();
            String capitalcurrentBondName = currentBondName.toUpperCase();
            if (capitalcurrentBondName.contains(matchingWord)) {
                tempBondList.add(currentBond);
            }
        }
        if (tempBondList.isEmpty()) {
            throw new BondException("Bond with the following keyword could not be found: " + bondName);
        }
        ui.printBondHeader();
        for (int i = ISZERO; i < tempBondList.size(); i++) {
            printOneBond((i + ONE_INDEX), tempBondList.get(i), ISMULTIPLE, ui);
        }
        ui.printDivider();
    }

    /**
     * Imports bond to list.
     *
     * @param bond bond object.
     */
    public void importBondToList(Bond bond) {
        bondLists.add(bond);
    }

    /**
     * Checks if the bond list is full.
     *
     * @return If the bond list is full.
     */
    public boolean isListFull() {
        return getSize() >= MAX_LIST_SIZE;
    }
}
