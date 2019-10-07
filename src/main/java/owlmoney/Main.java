package owlmoney;

import java.util.Scanner;

import owlmoney.logic.command.Command;
import owlmoney.logic.parser.ParseCommand;
import owlmoney.logic.parser.exception.ParserException;
import owlmoney.model.profile.Profile;
import owlmoney.ui.Ui;

/**
 * The main class.
 */

class Main {

    private Ui ui;
    private ParseCommand parser;
    private Profile profile;
    //private Storage storage;

    /**
     * Initializes a new OwlMoney session.
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
     * Starts up the initialized OwlMoney session.
     */
    private void run() {
        boolean hasExited = false;
        //Temporary do this chunk
        ui.firstTimeRun();
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        profile = new Profile(username);
        ui.greet(profile.getUsername());
        // until above this line
        while (parser.hasNextLine()) {
            try {
                Command command = parser.parseLine();
                command.execute(profile, ui);
            } catch (ParserException exceptionMessage) {
                ui.printError(exceptionMessage.toString());
            }
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}

