import java.util.ArrayList;
import java.util.Scanner;

public class Chatty {

    public enum Command {
        TODO,
        DEADLINE,
        EVENT,
        MARK,
        UNMARK,
        DELETE,
        LIST,
        BYE,
        UNKNOWN // fallback for invalid commands
    }

    public static void main(String[] args) {
        System.out.println("Hello! I'm Chatty");
        System.out.printf("What can I do for you?%n%n");
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> storage = new ArrayList<>();
        boolean inLoop = true;

        while (inLoop) {
            try {
                String input = sc.nextLine().trim(); // Handles leading / trailing spaces
                if (input.isBlank()) {
                    ChattyExceptions.emptyCommand();
                    continue;
                }
                Command command = parseCommand(input);
                switch(command) {
                    case BYE:
                        System.out.println("Bye. Hope to see you again!");
                        inLoop = false;
                        break;
                    case LIST:
                        System.out.printf("Here are the tasks in your list:%n");
                        for (int i = 0; i < storage.size(); i++) {
                            System.out.printf("%d. %s%n", i + 1, storage.get(i).toString());
                        }
                        System.out.printf("%n");
                        break;

                    case MARK:
                    case UNMARK:
                    case DELETE:
                        handleIndexCommand(command, input, storage);
                        break;

                    case TODO:
                    case DEADLINE:
                    case EVENT:
                        addTask(command, storage, input);
                        break;
                    default:
                        ChattyExceptions.unknownCommand();
                }
            } catch (ChattyExceptions e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static Command parseCommand(String input) throws ChattyExceptions {
        String firstWord = input.split("\\s+")[0].toLowerCase();
        try {
            return Command.valueOf(firstWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Command.UNKNOWN;
        }
    }

    private static int parseTaskIndex(String input, ArrayList<Task> storage)
            throws ChattyExceptions {
        String[] parts = input.split("\\s+");

        if (parts.length < 2) {
            ChattyExceptions.missingTaskNumber();
        }

        try {
            int index = Integer.parseInt(parts[1]) - 1;
            if (index < 0 || index >= storage.size()) {
                ChattyExceptions.invalidTaskNumber();
            }
            return index;
        } catch (NumberFormatException e) {
            ChattyExceptions.nonIntegerTaskNumber();
            return -1; // unreachable
        }
    }

    private static void handleIndexCommand(Command command, String input, ArrayList<Task> storage)
            throws ChattyExceptions{
        int taskNum = parseTaskIndex(input, storage);
        switch(command) {
        case MARK:
            storage.get(taskNum).markComplete();

            System.out.printf("Nice! I've marked this task as done:%n");
            System.out.printf("%s%n%n", storage.get(taskNum).toString());
            break;

        case UNMARK:
            storage.get(taskNum).markIncomplete();

            System.out.printf("OK, I've marked this task as not done yet :%n");
            System.out.printf("%s%n%n", storage.get(taskNum).toString());
            break;

        case DELETE:
            System.out.printf("Noted. I've removed this task:%n%s%n", storage.get(taskNum).toString());
            storage.remove(taskNum);
            System.out.printf("Now you have %d task(s) left in the list.%n%n", storage.size());
            break;
        }
    }

    private static void addTask(Command command,ArrayList<Task> storage, String input) throws ChattyExceptions {
        if (input.split("\\s+").length < 2) {
            ChattyExceptions.emptyDescription(command.name().toLowerCase());
        }
        switch(command) {
            case DEADLINE:
                int byIndex = input.indexOf("/by");
                // If format is incorrect
                if (byIndex == -1 ) {
                    ChattyExceptions.invalidDeadlineFormat();
                }
                String DeadlineName = input.substring("deadline".length() + 1, input.indexOf("/"));
                String date = input.substring(byIndex + 4);
                Deadline toAdd = new Deadline(DeadlineName, date);
                storage.add(toAdd);
                System.out.printf("Got it. I've added this task:%n");
                System.out.println(toAdd);
                break;
            case EVENT:
                int fromIndex = input.indexOf("/from");
                int toIndex = input.indexOf("/to");

                // Check if /from and /to exist and in correct order
                if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                    ChattyExceptions.invalidEventFormat();
                }
                // Parse input
                String name = input.substring("event".length() + 1, input.indexOf("/"));
                String from = input.substring(fromIndex + 6, toIndex - 1);
                String to = input.substring(toIndex + 4);
                // Check for empty fields
                if (name.isEmpty() || from.isEmpty() || to.isEmpty()) {
                    ChattyExceptions.emptyEventFields();
                }

                Event event = new Event(name, from, to);
                storage.add(event);
                System.out.printf("Got it. I've added this task:%n");
                System.out.println(event);
                break;
            case TODO:
                Todo todo = new Todo(input.substring("todo".length() + 1));
                System.out.printf("Got it. I've added this task:%n");
                storage.add(todo);
                System.out.println(todo);
                break;
        }
        System.out.printf("Now you have %d task(s) in the list.%n%n", storage.size());
    }
}

