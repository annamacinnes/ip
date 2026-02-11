package chatty.ui;

import chatty.task.Task;
import chatty.task.TaskList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Ui {
    public static String printWelcomeMessage() {
        return "Hello! I'm chatty.Chatty" + String.format("What can I do for you?%n%n");
    }

    public static String printByeMessage() {
        return "Bye. Hope to see you again!";
    }

    public static String markTaskMessage(int taskNum, TaskList storage) {
        return String.format("Nice! I've marked this task as done:%n")
                + String.format("%s%n%n", storage.get(taskNum).toString());
    }

    public static String unmarkTaskMessage(int taskNum, TaskList storage) {
        return String.format("OK, I've marked this task as not done yet :%n")
                + String.format("%s%n%n", storage.get(taskNum).toString());
    }

    public static String deleteTaskMessage(Task task, TaskList storage) {
        return String.format("Noted. I've removed this task:%n%s%n", task.toString())
                + String.format("Now you have %d task(s) left in the list.%n%n", storage.size());
    }

    public static String loadErrorMessage(String e) {
        return String.format("Something went wrong: %s%n", e);
    }

    public static String listTaskMessage() {
        return String.format("Here are the tasks in your list:%n");
    }

    public static String noRelevantTaskMessage() {
        return String.format("There are no tasks relevant to this date!%n%n");
    }

    public static String relevantTasksMessage(LocalDate dateToFind) {
        return String.format("Here are the tasks relevant to %s%n",
                dateToFind.format(DateTimeFormatter.ofPattern("MMM dd yyyy")));
    }

    public static String matchingTasksMessage() {
        return String.format("Here are the matching tasks in your list:%n");
    }

    public static String noMatchingTasksMessage() {
        return String.format("There are no tasks in your list that match this description.%n%n");
    }

    public static String addTaskMessage(Task task, TaskList taskList) {
        return String.format("Got it. I've added this task:%n")
                + String.format("%s%n",task.toString())
                + String.format("Now you have %d task(s) in the list.%n%n", taskList.size());
    }

}
