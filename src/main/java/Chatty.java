import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;


public class Chatty {

    private static final String FILE_PATH = "/Users/annamacinnes/ip/text-ui-test/data/chatty.txt";
    public enum Command {
        TODO,
        DEADLINE,
        EVENT,
        MARK,
        UNMARK,
        DELETE,
        LIST,
        BYE,
        DUE,
        UNKNOWN // fallback for invalid commands
    }


    public static void main(String[] args) throws FileNotFoundException, ChattyExceptions {
        System.out.println("Hello! I'm Chatty");
        System.out.printf("What can I do for you?%n%n");
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> storage = load();
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
                    case DUE:
                        getTasksDueOn(input, storage);
                        break;
                    case MARK:
                    case UNMARK:
                    case DELETE:
                        handleIndexCommand(command, input, storage);
                        writeToFile(storage);
                        break;
                    case TODO:
                    case DEADLINE:
                    case EVENT:
                        addTask(command, storage, input);
                        writeToFile(storage);
                        break;
                    default:
                        ChattyExceptions.unknownCommand();
                }
            } catch (ChattyExceptions | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void writeToFile(ArrayList<Task> tasksToAdd) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(FILE_PATH)){
            int i = 0;
            for (Task task: tasksToAdd) {
                i++;
                fw.write(String.format("%d. %s%n", i, task.toString()));
            }
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    private static ArrayList<Task> load() throws FileNotFoundException, ChattyExceptions {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        Scanner s = new Scanner(file);
        ArrayList<Task> tasks = new ArrayList<>();
        while (s.hasNext()) {
            String taskDescription = s.nextLine();
            int startNameIndex = taskDescription.indexOf("] ") + 2;
            if (taskDescription.contains("[T]")) {
                tasks.add(new Todo(taskDescription.substring(startNameIndex)));
            } else {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");

                    if (taskDescription.contains("[D]")) {
                        String name = taskDescription.substring(startNameIndex, taskDescription.indexOf("(")).trim();
                        String date = taskDescription.substring(
                                taskDescription.indexOf("(by: ") + 5,
                                taskDescription.length() - 1
                        ).trim();
                        tasks.add(new Deadline(name, LocalDate.parse(date, formatter)));

                    } else if (taskDescription.contains("[E]")) {
                        String name = taskDescription.substring(startNameIndex, taskDescription.indexOf("(")).trim();
                        String from = taskDescription.substring(
                                taskDescription.indexOf("from: ") + 6,
                                taskDescription.indexOf(" to:")
                        ).trim();
                        String to = taskDescription.substring(
                                taskDescription.indexOf("to: ") + 4,
                                taskDescription.length() - 1
                        ).trim();
                        tasks.add(new Event(
                                name,
                                LocalDate.parse(from, formatter),
                                LocalDate.parse(to, formatter)
                        ));
                    }

                } catch (DateTimeException e) {
                    ChattyExceptions.invalidDateFormat();
                }
            }
            if (taskDescription.contains("[X]")) {
                tasks.get(tasks.size() - 1).markComplete();
            }
        }
        return tasks;
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


    private static void addTask(Command command,ArrayList<Task> storage, String input) throws ChattyExceptions, DateTimeException {
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
                try {
                    LocalDate parsedBy = LocalDate.parse(date);
                    Deadline toAdd = new Deadline(DeadlineName, parsedBy);
                    storage.add(toAdd);
                } catch (DateTimeException e) {
                    ChattyExceptions.invalidDateFormat();
                }
                System.out.printf("Got it. I've added this task:%n");
                System.out.println(storage.get(storage.size() - 1));
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
                if (name.isEmpty() || from.isEmpty() || to.isEmpty()) {
                    ChattyExceptions.emptyEventFields();
                }
                try {
                    LocalDate parsedFrom = LocalDate.parse(from);
                    LocalDate parsedTo = LocalDate.parse(to);
                    Event event = new Event(name, parsedFrom, parsedTo);
                    storage.add(event);
                } catch (DateTimeException e) {
                    ChattyExceptions.invalidDateFormat();
                }
                // Check for empty fields
                System.out.printf("Got it. I've added this task:%n");
                System.out.println(storage.get(storage.size() - 1));
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


    private static void getTasksDueOn(String input, ArrayList<Task> storage) throws ChattyExceptions {
        if (input.split("\\s+").length < 2) {
            ChattyExceptions.emptyDescription("due");
        }
        try {
            LocalDate dateToFind = LocalDate.parse(input.split("\\s+")[1]);
            ArrayList<Task> toPrint = getTasks(storage, dateToFind);
            if (toPrint.isEmpty()) {
                System.out.printf("There are no tasks relevant to this date!%n%n");
            } else {
                int i = 1;
                System.out.printf("Here are the tasks relevant to %s%n",
                        dateToFind.format(DateTimeFormatter.ofPattern("MMM dd yyyy")));
                for (Task t: toPrint) {
                    System.out.printf("%d. %s%n", i ,t.toString());
                    i++;
                }
                System.out.printf("%n");
            }
        } catch (DateTimeException e) {
            ChattyExceptions.invalidDateFormat();
        }
    }

    private static ArrayList<Task> getTasks(ArrayList<Task> storage, LocalDate dateToFind) {
        ArrayList<Task> toPrint = new ArrayList<>();
        for (Task task: storage) {
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
        return toPrint;
    }
}



