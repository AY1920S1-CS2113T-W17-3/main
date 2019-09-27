package owlmoney;

import owlmoney.logic.parser.ParseCommand;
import owlmoney.logic.parser.exception.ParserException;
import owlmoney.ui.Ui;

/**
 * The main class.
 */

class Main {

    private Ui ui;
    private ParseCommand parser;
    //private Storage storage;

    /**
     * Initializes a new Duke session.
     */
    private Main() {
        ui = new Ui();
        parser = new ParseCommand();
        /*storage = new Storage("data/data.txt");
        try {
            tasks = new TaskList(storage.readFile());
        } catch (FileNotFoundException e) {
            ui.printError("Could not read tasks from disk, will start with empty file");
            tasks = new TaskList();
        }*/
    }

    /**
     * Starts up the initialized Duke session.
     */
    private void run() {
        boolean hasExited = false;
        ui.greet();
        while (parser.hasNextLine()) {
            try {
                parser.parseLine();
            } catch (ParserException exceptionMessage) {
                ui.printError(exceptionMessage.toString());
            }
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}

