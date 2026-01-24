import java.util.ArrayList;
import java.util.Scanner;

public class Chatty {
    public static void main(String[] args) throws ChattyExceptions {
        System.out.println("Hello! I'm Chatty");
        System.out.printf("What can I do for you?%n%n");
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> storage = new ArrayList<>();

        while (true) {
            try {
                String input = sc.nextLine().trim(); // Handles leading / trailing spaces
                if (input.isBlank()) {
                    ChattyExceptions.emptyCommand();
                    continue;
                }
                String[] inputArr = input.split("\\s+");
                String command = inputArr[0].toLowerCase();

                if (input.equals("bye")) { // If user types bye
                    System.out.println("Bye. Hope to see you again!");
                    break;
                } else if (input.equalsIgnoreCase("list")) { // if user types list
                    //                Loop through all elements in array and print name
                    System.out.printf("Here are the tasks in your list:%n");
                    for (int i = 0; i < storage.size(); i++) {
                        System.out.printf("%d. %s%n", i + 1, storage.get(i).toString());
                    }
                    System.out.printf("%n");
                } else if (command.equals("mark") || command.equals("unmark")) { //if user wants to mark a task
                    int taskNum = 0;
                    // Handle if user does not specify a task number / task number is invalid
                    try {
                        if (input.isBlank()) {
                            ChattyExceptions.emptyCommand();
                            continue;
                        }

                        taskNum = Integer.parseInt(inputArr[1]) - 1;
                        if (taskNum < 0 || taskNum >= storage.size()) {
                            ChattyExceptions.invalidTaskNumber();
                            continue;
                        }

                    } catch (NumberFormatException e) {
                        ChattyExceptions.nonIntegerTaskNumber();
                        continue;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        ChattyExceptions.missingTaskNumber();
                        continue;
                    } catch (ChattyExceptions e) {
                        System.out.println(e.getMessage());
                        continue;
                    }

                    if (command.equals("mark")) {
                        // Mark task as complete
                        storage.get(taskNum).markComplete();

                        // Completion message
                        System.out.printf("Nice! I've marked this task as done:%n");
                        System.out.printf("%s%n%n",
                                storage.get(taskNum).toString());
                    } else { // unmark
                        storage.get(taskNum).markIncomplete();
                        //Completion Message
                        System.out.printf("OK, I've marked this task as not done yet :%n");
                        System.out.printf("%s%n%n",
                                storage.get(taskNum).toString());
                    }
                } else if (command.equals("deadline") || command.equals("event") || command.equals("todo")) { // add task to storage
                    //Handle Errors
                    if (inputArr.length < 2) {
                        ChattyExceptions.emptyDescription(command);
                        continue;
                    }
                    if (command.equals("deadline")) { // parse input
                        int byIndex = input.indexOf("/by");
                        // If Format is incorrect
                        if (byIndex == -1 ) {
                            ChattyExceptions.invalidDeadlineFormat();
                            continue;
                        }
                        String name = input.substring("deadline".length() + 1, input.indexOf("/"));
                        String date = input.substring(byIndex + 4);
                        //                Task to add
                        Deadline toAdd = new Deadline(name, date);
                        //                Add to storage
                        storage.add(toAdd);
                        System.out.printf("Got it. I've added this task:%n");
                        System.out.println(toAdd.toString());
                    } else if (command.equals("event")) {
                        int fromIndex = input.indexOf("/from");
                        int toIndex = input.indexOf("/to");

                        // Check if /from and /to exist and in correct order
                        if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                            ChattyExceptions.invalidEventFormat();
                            continue;
                        }
                        // Parse input
                        String name = input.substring("event".length() + 1, input.indexOf("/"));
                        String from = input.substring(fromIndex + 6, toIndex - 1);
                        String to = input.substring(toIndex + 4);
                        // Check for empty fields
                        if (name.isEmpty() || from.isEmpty() || to.isEmpty()) {
                            ChattyExceptions.emptyEventFields();
                            continue;
                        }
                        // Task to add
                        Event toAdd = new Event(name, from, to);
                        //                Add to storage
                        storage.add(toAdd);
                        System.out.printf("Got it. I've added this task:%n");
                        System.out.println(toAdd.toString());
                    } else {
                        Todo toAdd = new Todo(input.substring("todo".length() + 1));
                        System.out.printf("Got it. I've added this task:%n");
                        storage.add(toAdd);
                        System.out.println(toAdd.toString());
                    }
                    System.out.printf("Now you have %d task(s) in the list.%n%n", storage.size());
                } else {
                    ChattyExceptions.unknownCommand();
                }
            } catch (ChattyExceptions e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
        sc.close();
    }
}
