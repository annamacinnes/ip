package chatty.task;

import chatty.Chatty;
import chatty.ChattyExceptions;
import chatty.parser.Parser;
import chatty.ui.Ui;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a collection of {@code Task} objects.
 *
 * <p>The {@code TaskList} class provides methods to add, remove,
 * retrieve, search, mark, and filter tasks. It also supports
 * writing tasks to a file and retrieving tasks due on a specific date.
 *
 * <p>This class acts as the main container for all tasks in the Chatty application.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
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
     * Returns a formatted string of tasks due on a specified date.
     *
     * <p>The input must contain a date in ISO-8601 format (yyyy-MM-dd).
     * The method checks:
     * <ul>
     *   <li>Events occurring on the specified date (including spanning events)</li>
     *   <li>Deadlines due on the specified date</li>
     * </ul>
     *
     * @param date The date the user searched for.
     * @return A formatted message containing relevant tasks,
     *         or a message indicating no relevant tasks were found.
     * @throws ChattyExceptions If the input is invalid or improperly formatted.
     */
    public TaskList getTasksDueOn(LocalDate date) throws ChattyExceptions {
        TaskList tasksDueOn = new TaskList();
        try {
            for (Task task : tasks) {
                if (task.willOccurOn(date)) {
                    tasksDueOn.add(task);
                }
            }
        } catch (DateTimeException e) {
            ChattyExceptions.invalidDateFormat();
        }
        return tasksDueOn;
    }

    /**
     * Marks tasks as complete.
     *
     * @param taskIndexes the list of task indexes the user wishes to mark.
     * @return A formatted message containing the marked tasks.
     * @throws ChattyExceptions If the input is invalid or improperly formatted.
     */
    public TaskList markTask(List<Integer> taskIndexes) {
        assert !taskIndexes.isEmpty() : "List of task indexes to mark should not be empty";
        assert taskIndexes != null : "List of task indexes should not be null";

        TaskList markedTasks = new TaskList();
        for (Integer taskIndex : taskIndexes) {
            assert taskIndex >= 0 && taskIndex < tasks.size() : "Parsed task index invalid";
            this.get(taskIndex).setComplete();
            markedTasks.add(tasks.get(taskIndex));
        }
        return markedTasks;
    }

    /**
     * Deletes tasks.
     *
     * @param taskIndexes the list of task indexes the user wishes to delete.
     * @return A formatted message containing the deleted tasks.
     * @throws ChattyExceptions If the input is invalid or improperly formatted.
     */
    public TaskList deleteTask(List<Integer> taskIndexes) {
        TaskList deletedTasks = new TaskList();

        taskIndexes.sort(Collections.reverseOrder());

        for (Integer taskIndex : taskIndexes) {
            Task task = this.get(taskIndex);
            deletedTasks.add(task);
            this.remove(taskIndex);
        }
        return deletedTasks;
    }

    /**
     * Marks tasks as incomplete.
     *
     * @param taskIndexes the list of task indexes the user wishes to unmark.
     * @return A formatted message containing the unmarked tasks.
     * @throws ChattyExceptions If the input is invalid or improperly formatted.
     */
    public TaskList unmarkTask(List<Integer> taskIndexes) {
        TaskList unmarkedTasks = new TaskList();

        for (Integer taskIndex : taskIndexes) {
            tasks.get(taskIndex).setIncomplete();
            unmarkedTasks.add(tasks.get(taskIndex));
        }
        return unmarkedTasks;
    }

}