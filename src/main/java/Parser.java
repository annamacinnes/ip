import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Parser {

    public static Chatty.Command parseCommand(String input) throws ChattyExceptions {
        String firstWord = input.split("\\s+")[0].toLowerCase();
        try {
            return Chatty.Command.valueOf(firstWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Chatty.Command.UNKNOWN;
        }
    }

    public static int parseTaskIndex(String input, TaskList storage)
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

    public static Task parseAddTaskCommand(Chatty.Command command, String input) throws ChattyExceptions {
        if (input.split("\\s+").length < 2) {
            ChattyExceptions.emptyDescription(command.name().toLowerCase());
        }
        switch (command) {
            case DEADLINE:
                int byIndex = input.indexOf("/by");
                // If format is incorrect
                if (byIndex == -1) {
                    ChattyExceptions.invalidDeadlineFormat();
                }
                String DeadlineName = input.substring("deadline".length() + 1, input.indexOf("/"));
                String date = input.substring(byIndex + 4);
                try {
                    LocalDate parsedBy = LocalDate.parse(date);
                    return new Deadline(DeadlineName, parsedBy);
                } catch (DateTimeException e) {
                    ChattyExceptions.invalidDateFormat();
                }
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
                    return new Event(name, parsedFrom, parsedTo);
                } catch (DateTimeException e) {
                    ChattyExceptions.invalidDateFormat();
                }
                break;
            case TODO:
                return new Todo(input.substring("todo".length() + 1));
        }
        return new Task(" ");
    }

    public static Task parseFileTaskName(String taskDescription) throws ChattyExceptions {
        int startNameIndex = taskDescription.indexOf("] ") + 2;
        if (taskDescription.contains("[T]")) {
            return new Todo(taskDescription.substring(startNameIndex));
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");

                if (taskDescription.contains("[D]")) {
                    String name = taskDescription.substring(startNameIndex, taskDescription.indexOf("(")).trim();
                    String date = taskDescription.substring(
                            taskDescription.indexOf("(by: ") + 5,
                            taskDescription.length() - 1
                    ).trim();
                    return new Deadline(name, LocalDate.parse(date, formatter));

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
                    return new Event(
                            name,
                            LocalDate.parse(from, formatter),
                            LocalDate.parse(to, formatter)
                    );
                }
            } catch (DateTimeException e) {
                ChattyExceptions.invalidDateFormat();
            }
        }
        return new Task(" ");
    }
}
