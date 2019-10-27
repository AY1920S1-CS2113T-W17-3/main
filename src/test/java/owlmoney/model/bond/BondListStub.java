package owlmoney.model.bond;

import java.util.ArrayList;

import owlmoney.model.bond.exception.BondException;
import owlmoney.ui.Ui;

public class BondListStub extends BondList {
    private ArrayList<Bond> bondLists;
    private static final int ONE_INDEX = 1;
    private static final int ISZERO = 0;

    /**
     * Creates an arrayList of bonds.
     */
    BondListStub() {
        bondLists = new ArrayList<Bond>();
    }

    /**
     * Lists the bonds in the bondList.
     *
     * @param displayNum bond number.
     * @param ui         required for display.
     * @throws BondException if there are no bonds.
     */
    @Override
    public void listBond(int displayNum, Ui ui) throws BondException {
        if (bondLists.size() <= ISZERO) {
            throw new BondException("There are no bonds");
        } else {
            for (int i = bondLists.size() - ONE_INDEX; i >= ISZERO; i--) {
                System.out.println("bond: " + i);
            }
        }
    }

    /**
     * Add bond to list.
     *
     * @param bond bond object.
     * @param ui   required for printing.
     */
    @Override
    public void addBondToList(Bond bond, Ui ui) {
        bondLists.add(bond);
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
    @Override
    public void bondExist(Bond bond) throws BondException {
        for (int i = ISZERO; i < getSize(); i++) {
            if (bond.getName().equals(bondLists.get(i).getName())) {
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
        for (int i = ISZERO; i < getSize(); i++) {
            if (bondName.equals(bondLists.get(i).getName())) {
                bondLists.remove(i);
                return;
            }
        }
    }

    /**
     * Gets the bond object from the bondList.
     *
     * @param bondName the name of the bond to retrieve.
     * @return the bond object.
     * @throws BondException if the bond does not exist.
     */
    public Bond getBond(String bondName) throws BondException {
        for (int i = ISZERO; i < getSize(); i++) {
            if (bondName.equals(bondLists.get(i).getName())) {
                return bondLists.get(i);
            }
        }
        throw new BondException("There are no bonds with the name: " + bondName);
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
    @Override
    public void editBond(String bondName, String year, String rate, Ui ui) throws BondException {
        for (int i = ISZERO; i < getSize(); i++) {
            if (bondName.equals(bondLists.get(i).getName())) {
                editBondYear(year, i);
                editBondRate(rate, i);
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
}
