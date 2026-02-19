package chatty.ui;

import chatty.task.Task;
import chatty.task.TaskList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Ui {
    public static String printWelcomeMessage() {
        return "Hello! I'm Chatty! " + String.format("What can I do for you?%n%n");
    }

    /**
     * Returns a formatted string representation of all tasks in the list.
     *
     * @return A numbered list of tasks as a {@code String}.
     */
    public static String listTasks(TaskList tasks) {
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
     * Returns a formatted string representation of tasks that were just marked as complete.
     *
     * @return A numbered list of tasks as a {@code String}.
     */
    public static String markTaskMessage(TaskList markedTasks) {
        String output = String.format("Nice! I've marked these tasks as done:%n");
        output += listTasks(markedTasks);
        return output;
    }

    /**
     * Returns a formatted string representation of tasks that were just marked as incomplete.
     *
     * @return A numbered list of tasks as a {@code String}.
     */
    public static String unmarkTaskMessage(TaskList unmarkedTasks) {
        String output = String.format("OK, I've marked these tasks as not done yet:%n");
        output += listTasks(unmarkedTasks);
        return output;
    }

    /**
     * Returns a formatted string representation of tasks that were just removed from the list.
     *
     * @return A numbered list of tasks as a {@code String}.
     */
    public static String deleteTaskMessage(TaskList tasks, TaskList deletedTasks) {
        String output = String.format("Noted. I've removed these tasks:%n");
        output += listTasks(deletedTasks) + String.format("Now you have %d task(s) left in the list.%n%n", tasks.size());
        return output;
    }

    public static String loadErrorMessage(String e) {
        return String.format("Something went wrong: %s%n", e);
    }

    public static String listTaskMessage(TaskList tasks) {
        return tasks.isEmpty()
                ? String.format("There are no tasks in your list!%n")
                : String.format("Here are the tasks in your list:%n") + listTasks(tasks);
    }

    public static String dueTasksMessage(LocalDate dateToFind, TaskList tasks) {
        return tasks.isEmpty()
                ? String.format("There are no tasks relevant to this date!%n%n")
                : String.format("Here are the tasks relevant to %s%n",
                dateToFind.format(DateTimeFormatter.ofPattern("MMM dd yyyy")))
                + listTasks(tasks);
    }

    public static String matchingTasksMessage(TaskList tasks) {
        return tasks.isEmpty() ?
                String.format("There are no tasks in your list that match this description.%n%n")
                : String.format("Here are the matching tasks in your list:%n") + listTasks(tasks);
    }

    public static String addTaskMessage(Task task, TaskList taskList) {
        return String.format("Got it. I've added this task:%n")
                + String.format("%s%n",task.toString())
                + String.format("Now you have %d task(s) in the list.%n%n", taskList.size());
    }

}
