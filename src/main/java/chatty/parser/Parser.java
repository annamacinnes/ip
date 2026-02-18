package chatty.parser;

import chatty.Chatty;
import chatty.ChattyExceptions;
import chatty.storage.Storage;
import chatty.task.*;
import chatty.ui.Ui;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The {@code Parser} class is responsible for interpreting user input and
 * converting it into commands and task objects that the Chatty application can handle.
 * It also parses task descriptions stored in files to reconstruct {@link Task} objects.
 *
 * <p>It supports parsing of the following command types:
 * <ul>
 *     <li>TODO</li>
 *     <li>DEADLINE</li>
 *     <li>EVENT</li>
 * </ul>
 *
 * <p>It also provides utility methods to extract task indices and parse task data from saved files.
 */
public class Parser {

    /**
     * Parses a user input string and converts it into a {@link Chatty.Command}.
     *
     * @param input the raw input string from the user
     * @return the corresponding {@link Chatty.Command}, or {@link Chatty.Command#UNKNOWN}
     *         if the input does not match any known command
     */
    public static Chatty.Command parseCommand(String input) {
        assert input != null : "Input to parseCommand should not be null";
        assert !input.isBlank(): "Input to parseCommand should not be blank";

        String firstWord = input.split("\\s+")[0].toLowerCase();
        try {
            return Chatty.Command.valueOf(firstWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Chatty.Command.UNKNOWN;
        }
    }

    /**
     * Parses the task index from a command input string.
     *
     * @param input   the raw input string containing the task index
     * @param storage the {@link TaskList} containing current tasks
     * @return the zero-based index of the task
     * @throws ChattyExceptions if the input is missing a task number,
     *                           if the task number is invalid, or if it's not an integer
     */
    public static int parseTaskIndex(String input, TaskList storage) throws ChattyExceptions {
        String[] parts = input.split("\\s+");
        assert !input.isBlank(): "Input to parseTaskIndex should not be blank";
        assert storage != null : "TaskList should not be null";
        assert storage.size() >= 0 : "TaskList size cannot be negative";

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

    /**
     * Parses the keyword from a command input string.
     *
     * @param input the raw input string containing the task index
     * @return the keyword to find in the list of tasks
     * @throws ChattyExceptions if the input is missing the keyword
     */
    public static String parseKeywordToFind(String input) throws ChattyExceptions {
        String keyword = input.substring(4).trim();

        if (keyword.isEmpty()) {
            ChattyExceptions.emptyDescription("find command");
        }

        return keyword;
    }

    /**
     * Parses a user input string to create a new {@link Task} object for add-task commands.
     *
     * @param command the {@link Chatty.Command} type (TODO, DEADLINE, EVENT)
     * @param input   the raw input string from the user
     * @return a {@link Task} object corresponding to the input command
     * @throws ChattyExceptions if the task description is missing or the input format is invalid
     */
    public static Task parseAddTaskCommand(Chatty.Command command, String input) throws ChattyExceptions {
        assert command != null: "Command should not be null";
        assert input != null: "Input should not be null";

        if (input.split("\\s+").length < 2) {
            ChattyExceptions.emptyDescription(command.name().toLowerCase());
        }
        switch (command) {
        case DEADLINE:
            int byIndex = input.indexOf("/by");
            if (byIndex == -1) {
                ChattyExceptions.invalidDeadlineFormat();
            }
            String DeadlineName = input.substring("deadline ".length(), input.indexOf("/"));
            String date = input.substring(byIndex + "/by ".length());
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
            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                ChattyExceptions.invalidEventFormat();
            }
            String name = input.substring("event ".length(), input.indexOf("/"));
            String from = input.substring(fromIndex + "/from ".length(), toIndex - 1);
            String to = input.substring(toIndex + "/to ".length());
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
            return new Todo(input.substring("todo ".length()));
        }
        return new Task(" ");
    }

    /**
     * Parses a task description from a file and reconstructs the corresponding {@link Task}.
     *
     * @param taskDescription the task description string from the file
     * @return a {@link Task} object representing the saved task
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static Task parseFileTaskName(String taskDescription) throws ChattyExceptions {
        assert taskDescription != null : "Task description from file should not be null";
        assert taskDescription.contains("] ") : "Saved task format should contain closing bracket";

        int startNameIndex = taskDescription.indexOf("] ") + "] ".length();
        if (taskDescription.contains("[T]")) {
            return new Todo(taskDescription.substring(startNameIndex));
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");

                String substring = taskDescription.substring(startNameIndex, taskDescription.indexOf("(")).trim();
                if (taskDescription.contains("[D]")) {
                    String date = taskDescription.substring(
                            taskDescription.indexOf("(by: ") + "(by: ".length(), taskDescription.length() - 1).trim();
                    return new Deadline(substring, LocalDate.parse(date, formatter));

                } else if (taskDescription.contains("[E]")) {
                    String from = taskDescription.substring(
                            taskDescription.indexOf("from: ") + "from: ".length(), taskDescription.indexOf(" to:")
                    ).trim();
                    String to = taskDescription.substring(
                            taskDescription.indexOf("to: ") + "to: ".length(), taskDescription.length() - 1
                    ).trim();
                    return new Event(
                            substring,
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

    /**
     * Handles a user command and executes the corresponding operation.
     *
     * <p>This method acts as the central dispatcher for command execution.
     * Based on the provided {@code command}, it performs actions such as:
     * <ul>
     *     <li>Listing tasks</li>
     *     <li>Adding tasks</li>
     *     <li>Marking, unmarking, or deleting tasks</li>
     *     <li>Searching for tasks</li>
     *     <li>Filtering tasks by due date</li>
     *     <li>Exiting the application</li>
     * </ul>
     *
     * <p>For commands that modify the task list (e.g., ADD, MARK, DELETE),
     * the updated task list is written to persistent storage.
     *
     * @param command  The {@code Chatty.Command} representing the user’s command type.
     * @param taskList The {@code TaskList} containing all current tasks.
     * @param input    The full user input string associated with the command.
     * @return A formatted message string to be displayed to the user.
     *
     * @throws ChattyExceptions If the command is invalid or if parsing fails.
     * @throws IOException If an error occurs while writing to storage.
     */
    public static String handleCommandType(Chatty.Command command,
                                         TaskList taskList, String input) throws ChattyExceptions, IOException {
        assert command != null : "Command should not be null";
        assert taskList != null : "TaskList should not be null";
        assert input != null : "Input should not be null";

        String output = "";
        switch (command) {
        case BYE:
            output = Ui.printByeMessage();
            break;
        case LIST:
            output = Ui.listTaskMessage() + taskList.list();
            break;
        case DUE:
            output = taskList.getTasksDueOn(input);
            break;
        case MARK, UNMARK, DELETE:
            output = taskList.markTask(command, input);
            Storage.writeToFile(taskList);
            break;
        case TODO, DEADLINE, EVENT:
            Task toAdd = Parser.parseAddTaskCommand(command, input);
            taskList.add(toAdd);
            output = Ui.addTaskMessage(toAdd, taskList);
            Storage.writeToFile(taskList);
            break;
        case FIND:
            String keyword = Parser.parseKeywordToFind(input);
            TaskList tL = taskList.find(keyword);
            if (tL.isEmpty()) {
                output = Ui.noMatchingTasksMessage();
            } else {
                output = Ui.matchingTasksMessage();
                output += tL.list();
            }
            break;
        default:
            ChattyExceptions.unknownCommand();
        }
        assert output != null : "Output message should never be null";
        return output;
    }
}
