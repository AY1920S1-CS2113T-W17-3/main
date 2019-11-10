package owlmoney.model.card;

import static owlmoney.commons.log.LogsCenter.getLogger;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import owlmoney.model.card.exception.CardException;
import owlmoney.model.transaction.Transaction;
import owlmoney.model.transaction.exception.TransactionException;
import owlmoney.storage.Storage;
import owlmoney.ui.Ui;

/**
 * Provides a layer of abstraction for the ArrayList that stores credit cards.
 */
public class CardList {
    private ArrayList<Card> cardLists;
    private static final int ONE_INDEX = 1;
    private static final boolean ISMULTIPLE = true;
    private static final boolean ISSINGLE = false;
    private static final int ISZERO = 0;
    private static final int MAX_CARD_LIMIT = 10;
    private Storage storage;
    private static final String PROFILE_CARD_LIST_FILE_NAME = "profile_cardlist.csv";
    private static final Logger logger = getLogger(CardList.class);


    /**
     * Creates an arrayList of Cards.
     *
     * @param storage for importing and exporting purposes.
     */
    public CardList(Storage storage) {
        cardLists = new ArrayList<Card>();
        this.storage = storage;
    }

    /**
     * Adds an instance of card into the CardList.
     *
     * @param newCard a new card object to be added.
     * @param ui      required for printing.
     * @throws CardException If duplicate credit card name found.
     */
    public void cardListAddCard(Card newCard, Ui ui) throws CardException {
        if (cardExists(newCard.getName())) {
            logger.warning("There is already a credit card with the name " + newCard.getName());
            throw new CardException("There is already a credit card with the name " + newCard.getName());
        }
        if (cardLists.size() >= MAX_CARD_LIMIT) {
            logger.warning("The maximum limit of 10 credit cards has been reached.");
            throw new CardException("The maximum limit of 10 credit cards has been reached.");
        }
        cardLists.add(newCard);
        ui.printMessage("Added a new card with the below details: ");
        printOneCard(ONE_INDEX, newCard, ISSINGLE, ui);
        logger.info("Successfully added a new card into the list.");
        try {
            exportCardList();
            logger.info("Successfully store a new card in storage.");
        } catch (IOException e) {
            ui.printError("Error trying to save your addition of cards to disk. Your data is"
                    + " at risk, but we will try again, feel free to continue using the program.");
            logger.warning("Error trying to save your addition of cards to disk. Your data is"
                    + " at risk, but we will try again, feel free to continue using the program.");
        }
    }

    /**
     * Deletes an instance of a card from the CardList.
     *
     * @param cardName name of the card to be deleted.
     * @param ui       required for printing.
     * @throws CardException If CardList is empty or card to be deleted do not exist.
     */
    public void cardListDeleteCard(String cardName, Ui ui) throws CardException {
        cardListCheckListEmpty();
        boolean isDeleted = false;
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                cardLists.remove(i);
                ui.printMessage("Card with the following details has been removed:");
                printOneCard(ONE_INDEX, currentCard, ISSINGLE, ui);
                isDeleted = true;
                logger.info("Successfully deleted the card from the list.");
                try {
                    exportCardList();
                    logger.info("Successfully deleted the card from storage.");
                } catch (IOException e) {
                    ui.printError("Error trying to save your deletion of cards to disk. "
                            + "Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                    logger.warning("Error trying to save your deletion of cards to disk. "
                            + "Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                }
                break;
            }
        }
        if (!isDeleted) {
            logger.warning("No such card exist for deletion.");
            throw new CardException("No such card exist for deletion.");
        }
    }

    /**
     * Throws CardException if CardList is empty.
     *
     * @throws CardException If CardList is empty.
     */
    private void cardListCheckListEmpty() throws CardException {
        if (cardLists.size() <= ISZERO) {
            logger.warning("There are 0 cards in your profile.");
            throw new CardException("There are 0 cards in your profile.");
        }
    }

    /**
     * Gets the size of the cardList which counts the number of credit cards object within.
     *
     * @return size of cardList.
     */
    int getCardListSize() {
        return cardLists.size();
    }

    /**
     * Checks if the credit card name that the user specified exists.
     *
     * @param cardName name of credit card.
     * @return the result specifying whether the credit card name already exists.
     */
    private boolean cardExists(String cardName) {
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Throws CardException if the credit card name that the user specified does not exists.
     *
     * @param cardName name of credit card.
     * @throws CardException if the credit card name that the user specified does not exists.
     */
    public void checkCardExists(String cardName) throws CardException {
        boolean isExist = false;
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                isExist = true;
            }
        }
        if (!isExist) {
            logger.warning("Credit card " + cardName + " does not exist!");
            throw new CardException("Credit card " + cardName + " does not exist!");
        }
    }

    /**
     * Checks if new card name is unique.
     *
     * @param currentCard The card to be changed.
     * @param newCardName The new name of the card.
     * @throws CardException If new card name is not unique.
     */
    private void compareCard(Card currentCard, String newCardName) throws CardException {
        String capitalNewCardName = newCardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card checkCard = cardLists.get(i);
            String checkCardName = checkCard.getName();
            String capitalCheckCardName = checkCardName.toUpperCase();
            if (capitalCheckCardName.equals(capitalNewCardName) && !checkCard.equals(currentCard)) {
                logger.warning("There is already a credit card with the name: " + newCardName);
                throw new CardException("There is already a credit card with the name: " + newCardName);
            }
        }
    }

    /**
     * Checks if all credit card expenditures have been paid else cannot edit card limit.
     *
     * @param card The card object.
     * @throws CardException If credit card contains unpaid expenditures.
     */
    private void checkUnpaidCannotEditLimit(Card card) throws CardException {
        if (!card.isEmpty()) {
            logger.warning("Card limit cannot be edited if there are unpaid expenditures");
            throw new CardException("Card limit cannot be edited if there are unpaid expenditures");
        }
    }

    /**
     * Edits the credit card details.
     *
     * @param cardName Credit Card to be edited.
     * @param newName  New name of credit card if any.
     * @param limit    New limit of credit card if any.
     * @param rebate   New rebate of credit card if any.
     * @param ui       Required for printing.
     * @throws CardException If card cannot be found or new card name already exist.
     */
    public void cardListEditCard(String cardName, String newName, String limit, String rebate, Ui ui)
            throws CardException {
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                if (!(newName == null || newName.isBlank())) {
                    compareCard(currentCard, newName);
                    currentCard.setName(newName);
                }
                if (!(limit == null || limit.isBlank())) {
                    this.checkUnpaidCannotEditLimit(currentCard);
                    currentCard.setLimit(Double.parseDouble(limit));
                }
                if (!(rebate == null || rebate.isBlank())) {
                    currentCard.setRebate(Double.parseDouble(rebate));
                }
                ui.printMessage("New details of the cards: ");
                printOneCard(ONE_INDEX, cardLists.get(i), ISSINGLE, ui);
                logger.info("Successfully edited the card in the list.");
                try {
                    exportCardList();
                    logger.info("Successfully edited the card in the storage.");
                } catch (IOException e) {
                    ui.printError("Error trying to save your editions of cards to disk. "
                            + "Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                    logger.warning("Error trying to save your editions of cards to disk. "
                            + "Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Card could not be found for editing card details.");
        throw new CardException("Card could not be found for editing card details.");
    }

    /**
     * Lists all credit cards details.
     *
     * @param ui Required for printing.
     * @throws CardException If CardList is empty.
     */
    public void cardListListCards(Ui ui) throws CardException {
        cardListCheckListEmpty();
        ui.printCardHeader();
        for (int i = ISZERO; i < cardLists.size(); i++) {
            printOneCard((i + ONE_INDEX), cardLists.get(i), ISMULTIPLE, ui);
        }
        ui.printDivider();
        logger.info("Successfully list the card details.");
    }

    /**
     * Adds an expenditure tied to a credit card.
     * This will store the expenditure in the ExpenditureList in the credit card.
     *
     * @param cardName    The credit card name.
     * @param expenditure The instance of the expenditure.
     * @param ui          Required for printing.
     * @param type        Type of account to add expenditure into
     * @throws CardException If the credit card name cannot be found.
     */
    public void cardListAddExpenditure(String cardName, Transaction expenditure, Ui ui, String type)
            throws CardException {
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                currentCard.addInExpenditure(expenditure, ui, type);
                logger.info("Successfully added card expenditure to the list.");
                try {
                    cardLists.get(i).exportCardPaidTransactionList(Integer.toString(i));
                    cardLists.get(i).exportCardUnpaidTransactionList(Integer.toString(i));
                    logger.info("Successfully added card expenditure to the storage.");
                } catch (IOException exceptionMessage) {
                    ui.printError("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                    logger.warning("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Card cannot be found for adding expenditure:" + cardName);
        throw new CardException("Card cannot be found for adding expenditure:" + cardName);
    }

    /**
     * Lists expenditures in the credit card.
     *
     * @param cardToList The name of the credit card.
     * @param ui         required for printing.
     * @param displayNum Number of expenditures to list.
     * @throws CardException        If the credit card name cannot be found.
     * @throws TransactionException If no expenditure found or no expenditure is in the list.
     */
    public void cardListListCardExpenditure(String cardToList, Ui ui, int displayNum)
            throws TransactionException, CardException {
        String capitalCardToList = cardToList.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardToList.equals(capitalCurrentCardName)) {
                cardLists.get(i).listAllExpenditure(ui, displayNum);
                logger.info("Listing card expenditure.");
                return;
            }
        }
        logger.warning("Card cannot be found to list expenditure: " + cardToList);
        throw new CardException("Card cannot be found to list expenditure: " + cardToList);
    }

    /**
     * Deletes an expenditure from the transactionList in the card object.
     *
     * @param transactionNumber     The transaction number.
     * @param deleteFromAccountCard The name of the card.
     * @param ui                    Required for printing.
     * @throws CardException        If card does not exist.
     * @throws TransactionException If invalid transaction.
     */
    public void cardListDeleteExpenditure(int transactionNumber, String deleteFromAccountCard, Ui ui)
            throws CardException, TransactionException {
        String capitalDeleteFromAccountCard = deleteFromAccountCard.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalDeleteFromAccountCard.equals(capitalCurrentCardName)) {
                currentCard.deleteExpenditure(transactionNumber, ui);
                logger.info("Successfully deleted card expenditure from the list.");
                try {
                    cardLists.get(i).exportCardPaidTransactionList(Integer.toString(i));
                    cardLists.get(i).exportCardUnpaidTransactionList(Integer.toString(i));
                    logger.info("Successfully deleted card expenditure from the storage.");
                } catch (IOException exceptionMessage) {
                    ui.printError("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                    logger.warning("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Card cannot be found for deleting expenditure: " + deleteFromAccountCard);
        throw new CardException("Card cannot be found for deleting expenditure: " + deleteFromAccountCard);
    }

    /**
     * Edits an expenditure from the transactionList in the card object.
     *
     * @param transactionNumber The transaction number.
     * @param editFromCard      The name of the card.
     * @param description              The description of the expenditure.
     * @param amount            The amount of the expenditure.
     * @param date              The date of the expenditure.
     * @param category          The category of the expenditure.
     * @param ui                Required for printing.
     * @throws CardException        If card does not exist.
     * @throws TransactionException If incorrect date format.
     */
    public void cardListEditExpenditure(int transactionNumber, String editFromCard, String description,
            String amount, String date, String category, Ui ui) throws CardException, TransactionException {
        String capitalEditFromCard = editFromCard.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalEditFromCard.equals(capitalCurrentCardName)) {
                cardLists.get(i).editExpenditureDetails(transactionNumber, description, amount, date,
                        category, ui);
                logger.info("Successfully edited card expenditure in the list.");
                try {
                    cardLists.get(i).exportCardPaidTransactionList(Integer.toString(i));
                    cardLists.get(i).exportCardUnpaidTransactionList(Integer.toString(i));
                    logger.info("Successfully edited card expenditure in the storage.");
                } catch (IOException exceptionMessage) {
                    ui.printError("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                    logger.warning("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                }
                return;
            }
        }
        logger.warning("Card cannot be found for editing expenditure: " + editFromCard);
        throw new CardException("Card cannot be found for editing expenditure: " + editFromCard);
    }

    /**
     * Prints card details.
     *
     * @param num                Represents the numbering of the card.
     * @param card               The card object to be printed.
     * @param isMultiplePrinting Represents whether the function will be called for printing once or
     *                           multiple time
     * @param ui                 The object use for printing.
     */
    private void printOneCard(int num, Card card, boolean isMultiplePrinting, Ui ui) {
        if (!isMultiplePrinting) {
            ui.printCardHeader();
        }
        ui.printCard(num, card.getName(),
                "$" + new DecimalFormat("0.00").format(card.getLimit()),
                "$" + new DecimalFormat("0.00").format(card.getRemainingLimitNow()),
                new DecimalFormat("0.00").format(card.getRebate()) + "%");
        if (!isMultiplePrinting) {
            ui.printDivider();
        }
    }

    /**
     * Prints the card header details once only when listing of multiple card.
     *
     * @param num Represents the current number of card being listed.
     * @param ui  The object use for printing.
     */
    private void printOneCardHeader(int num, Ui ui) {
        if (num == ISZERO) {
            ui.printBankHeader();
        }
    }

    /**
     * Finds card that matches the name provided by user.
     *
     * @param cardName The name of the card to match against.
     * @param ui       The object required for printing.
     * @throws CardException If there is no matches for card object.
     */
    public void findCard(String cardName, Ui ui) throws CardException {
        ArrayList<Card> tempCardList = new ArrayList<Card>();
        String matchingWord = cardName.toUpperCase();

        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCurrentCardName.contains(matchingWord)) {
                tempCardList.add(cardLists.get(i));
            }
        }
        logger.info("Search for card completed");
        if (tempCardList.isEmpty()) {
            logger.warning("Card with the following keyword could not be found: " + cardName);
            throw new CardException("Card with the following keyword could not be found: " + cardName);
        }

        for (int i = ISZERO; i < tempCardList.size(); i++) {
            printOneCardHeader(i, ui);
            printOneCard((i + ONE_INDEX), tempCardList.get(i), ISMULTIPLE, ui);
        }
        ui.printDivider();
        logger.info("Successfully found matching card.");
    }

    /**
     * Finds matching card transactions from the card specified by the user.
     *
     * @param cardName    The name of the card object to be searched for matching transaction.
     * @param fromDate    The date to search from.
     * @param toDate      The date to search until.
     * @param description The description keyword to match against.
     * @param category    The category keyword to match against.
     * @param ui          The object required for printing.
     * @throws CardException        If card with the name does not exist.
     * @throws TransactionException If parsing of date fails.
     */
    public void cardListFindTransaction(String cardName, String fromDate, String toDate,
            String description, String category, Ui ui) throws CardException, TransactionException {
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                logger.info("Found card to search for matching transaction.");
                currentCard.findTransaction(fromDate, toDate, description, category, ui);
                return;
            }
        }
        logger.warning("Card with the following name does not exist: " + cardName);
        throw new CardException("Card with the following name does not exist: " + cardName);
    }

    /**
     * Returns the total unpaid expenditure amount based on the specified date.
     *
     * @param cardName The credit card to search the expenditures from.
     * @param date     The YearMonth date of the expenditures to search.
     * @return The total unpaid expenditure amount based on the specified date.
     * @throws CardException If card does not exist.
     */
    public double getUnpaidBillAmount(String cardName, YearMonth date) throws CardException {
        checkCardExists(cardName);
        double billAmount = 0;
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                billAmount = currentCard.getUnpaidBillAmount(date);
            }
        }
        return billAmount;
    }

    /**
     * Returns the total paid expenditure amount based on the specified date.
     *
     * @param cardName The credit card to search the expenditures from.
     * @param date     The YearMonth date of the expenditures to search.
     * @return The total unpaid expenditure amount based on the specified date.
     * @throws CardException If card does not exist.
     */
    public double getPaidBillAmount(String cardName, YearMonth date) throws CardException {
        checkCardExists(cardName);
        double billAmount = 0;
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                billAmount = currentCard.getPaidBillAmount(date);
            }
        }
        return billAmount;
    }

    /**
     * Returns the monthly rebate of the credit card.
     *
     * @param cardName The credit card to get the monthly rebate information from.
     * @return The monthly rebate of the credit card.
     * @throws CardException If card does not exist.
     */
    public double getRebateAmount(String cardName) throws CardException {
        checkCardExists(cardName);
        double rebateAmount = 0;
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                rebateAmount = currentCard.getRebate();
            }
        }
        return rebateAmount;
    }

    /**
     * Returns the id of the credit card.
     *
     * @param cardName The credit card to get the id from.
     * @return The id of the credit card.
     * @throws CardException If card does not exist.
     */
    public UUID getCardId(String cardName) throws CardException {
        checkCardExists(cardName);
        UUID id = null;
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                id = cardLists.get(i).getId();
            }
        }
        return id;
    }

    /**
     * Transfers expenditures from unpaid list to paid list.
     *
     * @param cardName The credit card of which the expenditures to transfer.
     * @param cardDate The YearMonth date of expenditures to transfer.
     * @param type     Type of expenditure (card or bank).
     * @throws TransactionException If invalid transaction when deleting.
     */
    public void transferExpUnpaidToPaid(String cardName, YearMonth cardDate, String type)
            throws TransactionException {
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                currentCard.transferExpUnpaidToPaid(cardDate, type);
                logger.info("Transferring expenditure from unpaid to paid for card.");
                try {
                    cardLists.get(i).exportCardPaidTransactionList(Integer.toString(i));
                    cardLists.get(i).exportCardUnpaidTransactionList(Integer.toString(i));
                    logger.info("Successfully store expenditure in unpaid and paid for card to storage.");
                } catch (IOException exceptionMessage) {
                    logger.warning("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                    throw new TransactionException("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                }
            }
        }
    }

    /**
     * Transfers expenditures from paid list to unpaid list.
     *
     * @param cardName The credit card of which the expenditures to transfer.
     * @param cardDate The YearMonth date of expenditures to transfer.
     * @param type     Type of expenditure (card or bank).
     * @throws TransactionException If invalid transaction when deleting.
     */
    public void transferExpPaidToUnpaid(String cardName, YearMonth cardDate, String type)
            throws TransactionException {
        String capitalCardName = cardName.toUpperCase();
        for (int i = ISZERO; i < getCardListSize(); i++) {
            Card currentCard = cardLists.get(i);
            String currentCardName = currentCard.getName();
            String capitalCurrentCardName = currentCardName.toUpperCase();
            if (capitalCardName.equals(capitalCurrentCardName)) {
                currentCard.transferExpPaidToUnpaid(cardDate, type);
                logger.info("Transferring expenditure from paid to unpaid for card.");
                try {
                    cardLists.get(i).exportCardPaidTransactionList(Integer.toString(i));
                    cardLists.get(i).exportCardUnpaidTransactionList(Integer.toString(i));
                    logger.info("Successfully store expenditure in paid and unpaid for card to storage.");
                } catch (IOException exceptionMessage) {
                    logger.warning("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                    throw new TransactionException("Error trying to save your card expenditure"
                            + " to disk. Your data is at risk, but we will try again, "
                            + "feel free to continue using the program.");
                }
            }
        }
    }

    /**
     * Prepares the cardList for exporting of bank name and type of the bank account.
     *
     * @return ArrayList of String arrays for containing each card in the card list.
     */
    private ArrayList<String[]> prepareExportCardList() {
        ArrayList<String[]> exportArrayList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        exportArrayList.add(new String[] {"cardName", "cardLimit", "rebateRate", "uuid"});
        for (int i = 0; i < cardLists.size(); i++) {
            String cardName = cardLists.get(i).getName();
            double cardLimit = cardLists.get(i).getLimit();
            String stringCardLimit = decimalFormat.format(cardLimit);
            double rebateRate = cardLists.get(i).getRebate();
            String stringRebateRate = decimalFormat.format(rebateRate);
            UUID uuid = cardLists.get(i).getId();
            String stringUuid = uuid.toString();
            exportArrayList.add(new String[] {cardName, stringCardLimit, stringRebateRate, stringUuid});
        }
        return exportArrayList;
    }

    /**
     * Writes the data of the card list that was prepared into permanent storage.
     *
     * @throws IOException when unable to write to file.
     */
    private void exportCardList() throws IOException {
        ArrayList<String[]> inputData = prepareExportCardList();
        storage.writeFile(inputData, PROFILE_CARD_LIST_FILE_NAME);
    }

    /**
     * Imports cards loaded from save file into card list.
     * .
     *
     * @param newCard an instance of the card to be imported.
     */
    public void cardListImportNewCard(Card newCard) throws CardException {
        if (cardExists(newCard.getName())) {
            logger.warning("There is already a credit card with the name " + newCard.getName());
            throw new CardException("There is already a credit card with the name " + newCard.getName());
        }
        if (cardLists.size() >= MAX_CARD_LIMIT) {
            logger.warning("The maximum limit of 10 credit cards has been reached.");
            throw new CardException("The maximum limit of 10 credit cards has been reached.");
        }
        cardLists.add(newCard);
    }

    /**
     * Imports unpaid card expenditures from save file into the card's unpaid list.
     *
     * @param cardName       the name of the card to tie the expenditure to.
     * @param newExpenditure an instance of expenditure to be imported.
     */
    public void cardListImportNewUnpaidCardExpenditure(String cardName, Transaction newExpenditure) {
        for (int i = 0; i < cardLists.size(); i++) {
            if (cardLists.get(i).getName().equals(cardName)) {
                cardLists.get(i).importNewUnpaidExpenditure(newExpenditure);
            }
        }
    }

    /**
     * Imports paid card expenditures from save file into card's paid list.
     *
     * @param cardName       the name of the card to tie the expenditure to.
     * @param newExpenditure an instance of expenditure to be imported.
     */
    public void cardListImportNewPaidCardExpenditure(String cardName, Transaction newExpenditure) {
        for (int i = 0; i < cardLists.size(); i++) {
            if (cardLists.get(i).getName().equals(cardName)) {
                cardLists.get(i).importNewPaidExpenditure(newExpenditure);
            }
        }
    }
}
