package chatty.ui;

import chatty.task.Task;
import chatty.task.TaskList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Ui {
    public static void printWelcomeMessage() {
        System.out.println("Hello! I'm chatty.Chatty");
        System.out.printf("What can I do for you?%n%n");
    }

    public static void printByeMessage() {
        System.out.println("Bye. Hope to see you again!");
    }

    public static void addedTaskMessage(TaskList storage) {
        System.out.printf("Got it. I've added this task:%n");
        System.out.println(storage.get(storage.size() - 1));
    }

    public static void markTaskMessage(int taskNum, TaskList storage) {
        System.out.printf("Nice! I've marked this task as done:%n");
        System.out.printf("%s%n%n", storage.get(taskNum).toString());
    }

    public static void unmarkTaskMessage(int taskNum, TaskList storage) {
        System.out.printf("OK, I've marked this task as not done yet :%n");
        System.out.printf("%s%n%n", storage.get(taskNum).toString());
    }

    public static void deleteTaskMessage(int taskNum, TaskList storage) {
        System.out.printf("Noted. I've removed this task:%n%s%n", storage.get(taskNum).toString());
        System.out.printf("Now you have %d task(s) left in the list.%n%n", storage.size() - 1);
    }

    public static void loadErrorMessage(String e) {
        System.out.printf("Something went wrong: %s%n", e);
    }

    public static void listTaskMessage() {
        System.out.printf("Here are the tasks in your list:%n");
    }

    public static void noRelevantTaskMessage() {
        System.out.printf("There are no tasks relevant to this date!%n%n");
    }

    public static void relevantTasksMessage(LocalDate dateToFind) {
        System.out.printf("Here are the tasks relevant to %s%n",
                dateToFind.format(DateTimeFormatter.ofPattern("MMM dd yyyy")));
    }

    public static void addTaskMessage(Task task, TaskList taskList) {
        System.out.printf("Got it. I've added this task:%n");
        System.out.printf("%s%n",task.toString());
        System.out.printf("Now you have %d task(s) in the list.%n%n", taskList.size());
    }

}
