package owlmoney.model.goals;

import owlmoney.model.goals.exception.GoalsException;
import owlmoney.ui.Ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * The GoalsList class that provides a layer of abstraction for the ArrayList that stores goals.
 */
public class GoalsList {
    private ArrayList<Goals> goalList;

    /**
     * Creates a instance of GoalsList that contains an arrayList of Goals.
     */
    public GoalsList() {
        goalList = new ArrayList<Goals>();
    }

    /**
     * Lists all goals in GoalsList.
     *
     * @param ui required for printing.
     */
    public void listGoals(Ui ui) {
        if (goalList.size() <= 0) {
            ui.printError("There are no goals set");
        } else {
            for (int i = 0; i < goalList.size(); i++) {
                ui.printMessage((i + 1) + ":\n" + goalList.get(i).getGoalsDetails());
            }
        }
    }

    /**
     * Adds an instance of goals into GoalsList.
     *
     * @param goals a new goal object.
     * @param ui    required for printing.
     * @throws GoalsException If a duplicate goal name is found.
     */
    public void addToGoals(Goals goals, Ui ui) throws GoalsException {
        if (goalExists(goals.getGoalsName())) {
            throw new GoalsException("There is already a goal with the same name " + goals.getGoalsName());
        }
        goalList.add(goals);
        ui.printMessage("Added goals: \n" + goals.getGoalsDetails());
    }

    /**
     * Deletes a goal from GoalsList.
     *
     * @param goalName The name of the goal.
     * @param ui       required for printing.
     * @throws GoalsException If trying to delete from empty GoalsList
     */
    public void deleteFromGoalList(String goalName, Ui ui) throws GoalsException {
        if (goalList.size() <= 0) {
            throw new GoalsException("You have yet to set any goals!");
        } else if (goalList.size() == 1) {
            ui.printError("You should have at least 1 goal set");
        } else {
            for (int i = 0; i < goalList.size(); i++) {
                if (goalList.get(i).getGoalsName().equals(goalName)) {
                    ui.printMessage("Removing " + goalList.get(i).getGoalsName());
                    goalList.remove(i);
                    ui.printMessage("Removed!");
                    break;
                }
            }
        }
    }

    /**
     * Compares if the current goal name is the same as the new intended goal name.
     *
     * @param currentGoals Current Goal Name of the Goal.
     * @param newGoalName  New Goal Name that user intends to change.
     * @throws GoalsException If there's a goal of the same name.
     */
    public void compareGoals(Goals currentGoals, String newGoalName) throws GoalsException {
        for (int i = 0; i < goalList.size(); i++) {
            if (goalList.get(i).getGoalsName().equals(newGoalName) && !goalList.get(i).equals(currentGoals)) {
                throw new GoalsException("There is already a goal with the same name " + newGoalName);
            }
        }
    }

    /**
     * Checks if a goal exists when wanting to add a new goal.
     *
     * @param goalName refers to the about-to-add goal name.
     * @return True if it exists and False if it doesn't.
     */
    private boolean goalExists(String goalName) {
        for (int i = 0; i < goalList.size(); i++) {
            if (goalName.equals(goalList.get(i).getGoalsName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Edits an instance of the goal.
     *
     * @param goalName To identify and retrieve the goal details.
     * @param amount   New amount of the goal.
     * @param date     New date to reach the goal.
     * @param newName  New name to identify the goal.
     * @param ui       required for printing.
     * @throws GoalsException If date is not in correct format, or changing to a name that already exists,
     *                        or no goal with the goalName.
     */
    public void editGoals(String goalName, String amount, String date, String newName, Ui ui) throws GoalsException {
        for (int i = 0; i < goalList.size(); i++) {
            System.out.println("GoalList:" + date);
            if (goalList.get(i).getGoalsName().equals(goalName)) {
                if (!(newName.isEmpty() || newName.isBlank())) {
                    compareGoals(goalList.get(i), newName);
                    goalList.get(i).setGoalsName(newName);
                }
                if (!(amount.isBlank() || amount.isEmpty())) {
                    goalList.get(i).setGoalsAmount(Double.parseDouble(amount));
                }
                if (!(date == null || date.isBlank() || date.isEmpty())) {
                    DateFormat temp = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        goalList.get(i).setGoalsDate(temp.parse(date));
                    } catch (ParseException e) {
                        //check handled in ParseEditGoals
                        throw new GoalsException(e.toString());
                    }
                }
                ui.printMessage("New details of goals changed: ");
                ui.printMessage(goalList.get(i).getGoalsDetails());
                return;
            }
        }
        throw new GoalsException("There are no goals with the name: " + goalName);
    }
}
