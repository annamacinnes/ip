package chatty.task;

import chatty.Chatty;
import chatty.ChattyExceptions;
import chatty.parser.Parser;
import chatty.ui.Ui;

import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
/**
 * Manages a collection of {@code Task} objects.
 *
 * <p>The {@code TaskList} class provides methods to add, remove,
 * retrieve, search, mark, and filter tasks. It also supports
 * writing tasks to a file and retrieving tasks due on a specific date.
 *
 * <p>This class acts as the main container for all tasks in the Chatty application.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty {@code TaskList}.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the task list.
     *
     * @param task The {@code Task} to be added.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The total number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param i The index of the task (0-based).
     * @return The {@code Task} at the specified index.
     * @throws AssertionError If the index is out of bounds.
     */
    public Task get(int i) {
        assert i >= 0 && i < tasks.size() : "Task index out of bounds";
        return tasks.get(i);
    }

    /**
     * Removes the task at the specified index.
     *
     * @param i The index of the task (0-based).
     * @throws AssertionError If the index is out of bounds.
     */
    public void remove(int i) {
        assert i >= 0 && i < tasks.size() : "Task index out of bounds";
        tasks.remove(i);
    }

    /**
     * Checks whether the task list is empty.
     *
     * @return {@code true} if the task list contains no tasks,
     *         {@code false} otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns a formatted string representation of all tasks in the list.
     *
     * @return A numbered list of tasks as a {@code String}.
     */
    public String list() {
        int i = 1;
        String list = "";
        for (Task task : tasks) {
            list += String.format("%d. %s%n", i, task.toString());
            i++;
        }
        list += String.format("%n");
        return list;
    }

    /**
     * Returns a new {@code TaskList} containing tasks whose names
     * contain the specified keyword.
     *
     * @param keyword The keyword to search for.
     * @return A new {@code TaskList} containing matching tasks.
     */
    public TaskList find(String keyword) {
        TaskList taskList = new TaskList();
        for (Task task: tasks) {
            if (task.getName().contains(keyword)) {
                taskList.add(task);
            }
        }
        return taskList;
    }

    /**
     * Writes all tasks in the list to the specified {@code FileWriter}.
     *
     * @param fw The {@code FileWriter} used to write task data.
     * @throws RuntimeException If an {@code IOException} occurs during writing.
     */
    public void writeToFile(FileWriter fw) {
        int i = 1;
        for (Task task : tasks) {
            try {
                fw.write(String.format("%d. %s%n", i, task.toString()));
                i++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns a formatted string of tasks due on a specified date.
     *
     * <p>The input must contain a date in ISO-8601 format (yyyy-MM-dd).
     * The method checks:
     * <ul>
     *   <li>Events occurring on the specified date (including spanning events)</li>
     *   <li>Deadlines due on the specified date</li>
     * </ul>
     *
     * @param input The user input containing the date.
     * @return A formatted message containing relevant tasks,
     *         or a message indicating no relevant tasks were found.
     * @throws ChattyExceptions If the input is invalid or improperly formatted.
     */
    public String getTasksDueOn(String input) throws ChattyExceptions {
        if (input.split("\\s+").length < 2) {
            ChattyExceptions.emptyDescription("due");
        }
        TaskList toPrint = new TaskList();
        try {
            LocalDate dateToFind = LocalDate.parse(input.split("\\s+")[1]);
            for (Task task : tasks) {
                if (task.getType().equalsIgnoreCase("event")) {
                    Event event = (Event) task;
                    if (event.getStartDate().isEqual(dateToFind)
                            || event.getEndDate().isEqual(dateToFind)
                            || (event.getEndDate().isAfter(dateToFind) && event.getStartDate().isBefore(dateToFind))) {
                        toPrint.add(event);
                    }
                } else if ((task.getType().equalsIgnoreCase("deadline"))) {
                    Deadline deadline = (Deadline) task;
                    if (deadline.getDeadline().isEqual(dateToFind)) {
                        toPrint.add(deadline);
                    }
                }
            }
            if (toPrint.isEmpty()) {
                return Ui.noRelevantTaskMessage();
            } else {
                return Ui.relevantTasksMessage(dateToFind) + toPrint.list();
            }
        } catch (DateTimeException e) {
            ChattyExceptions.invalidDateFormat();
        }
        return null;
    }

    /**
     * Marks tasks as complete or incomplete.
     *
     * @param input The user input containing the tasks to mark.
     * @return A formatted message containing the marked tasks.
     * @throws ChattyExceptions If the input is invalid or improperly formatted.
     */
    public String markTask(Chatty.Command command, String input)
            throws ChattyExceptions {
        ArrayList<Integer> taskIndexes = Parser.parseTaskIndex(input, this);
        assert !taskIndexes.isEmpty(): "List of task indexes to mark should not be empty";
        assert taskIndexes != null : "List of task indexes should not be null";
        String toReturn = "";

        for (Integer taskIndex : taskIndexes) {
            assert taskIndex >= 0 && taskIndex < tasks.size() : "Parsed task index invalid";
            switch (command) {
            case MARK:
                this.get(taskIndex).setComplete();
                toReturn += Ui.markTaskMessage(taskIndex, this);
                break;
            case UNMARK:
                tasks.get(taskIndex).setIncomplete();
                toReturn +=  Ui.unmarkTaskMessage(taskIndex, this);
                break;
            case DELETE:
                Task task = this.get(taskIndex);
                this.remove(taskIndex);
                toReturn += Ui.deleteTaskMessage(task, this);
                break;
            }

        }
        return toReturn;
    }

}