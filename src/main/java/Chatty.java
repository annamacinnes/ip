import java.util.ArrayList;
import java.util.Scanner;

public class Chatty {
    public static void main(String[] args) {
        System.out.println("Hello! I'm Chatty");
        System.out.println("What can I do for you?");
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> storage = new ArrayList<>();

        while (true) {
            String input = sc.nextLine();
            String[] inputArr = input.split("\\s+");
            String command = inputArr[0].toLowerCase();

            if (input.equalsIgnoreCase("bye")) { // If user types bye
                System.out.printf("%nBye. Hope to see you again!");
                break;
            } else if (input.equalsIgnoreCase("list")) { // if user types list
//                Loop through all elements in array and print name
                System.out.printf("%nHere are the tasks in your list:%n");
                for (int i = 0; i < storage.size(); i++) {
                    System.out.printf("%d. %s%n", i + 1, storage.get(i).toString());
                }

            } else if (command.equalsIgnoreCase("mark")) { //if user wants to mark a task
//                Get task index in storage array
                int taskNum = Integer.parseInt(inputArr[1]) - 1;
//                Mark task as complete
                storage.get(taskNum).markComplete();

                // Completion message
                System.out.printf("%nNice! I've marked this task as done:%n");
                System.out.printf("%s%n",
                        storage.get(taskNum).toString());

            } else if (command.equalsIgnoreCase("unmark")){ //if user wants to unmark a task
//                Get task index in storage array
                int taskNum = Integer.parseInt(inputArr[1]) - 1;
//                Mark task as incomplete
                storage.get(taskNum).markIncomplete();

                //Completion Message
                System.out.printf("%nOK, I've marked this task as not done yet :%n");
                System.out.printf("%s%n",
                        storage.get(taskNum).toString());

            } else if (command.equals("deadline") || command.equals("event") || command.equals("todo")){ // add task to storage
                System.out.printf("%nGot it. I've added this task:%n");
//                Parse input
                if (command.equals("deadline")) {
                    String name = input.substring("deadline".length() + 1, input.indexOf("/"));
                    String date = input.substring(input.indexOf("/by") + 4);
//                Task to add
                    Deadline toAdd = new Deadline(name, date);
//                Add to storage
                    storage.add(toAdd);
                    System.out.println(toAdd.toString());
                } else if (command.equals("event")) {
                    // Parse input
                    String name = input.substring("event".length() + 1, input.indexOf("/"));
                    String from = input.substring(input.indexOf("/from") + 6, input.indexOf("/to") - 1);
                    String to = input.substring(input.indexOf("/to") + 4);
                    // Task to add
                    Event toAdd = new Event(name, from, to);
//                Add to storage
                    storage.add(toAdd);
                    System.out.println(toAdd.toString());
                } else {
                    Todo toAdd = new Todo(input.substring("todo".length() + 1));
                    storage.add(toAdd);
                    System.out.println(toAdd.toString());
                }
                System.out.printf("Now you have %d task(s) in the list.%n", storage.size());
            }
        }
        sc.close();
    }
}
