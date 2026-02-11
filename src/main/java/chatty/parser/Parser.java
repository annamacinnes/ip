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
        if (input.split("\\s+").length < 2) {
            ChattyExceptions.emptyDescription(command.name().toLowerCase());
        }
        switch (command) {
        case DEADLINE:
            int byIndex = input.indexOf("/by");
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
            if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
                ChattyExceptions.invalidEventFormat();
            }
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

    /**
     * Parses a task description from a file and reconstructs the corresponding {@link Task}.
     *
     * @param taskDescription the task description string from the file
     * @return a {@link Task} object representing the saved task
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static Task parseFileTaskName(String taskDescription) throws ChattyExceptions {
        int startNameIndex = taskDescription.indexOf("] ") + 2;
        if (taskDescription.contains("[T]")) {
            return new Todo(taskDescription.substring(startNameIndex));
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");

                String substring = taskDescription.substring(startNameIndex, taskDescription.indexOf("(")).trim();
                if (taskDescription.contains("[D]")) {
                    String date = taskDescription.substring(
                            taskDescription.indexOf("(by: ") + 5,
                            taskDescription.length() - 1
                    ).trim();
                    return new Deadline(substring, LocalDate.parse(date, formatter));

                } else if (taskDescription.contains("[E]")) {
                    String from = taskDescription.substring(
                            taskDescription.indexOf("from: ") + 6,
                            taskDescription.indexOf(" to:")
                    ).trim();
                    String to = taskDescription.substring(
                            taskDescription.indexOf("to: ") + 4,
                            taskDescription.length() - 1
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

    public static String handleCommandType(Chatty.Command command,
                                         TaskList taskList, String input) throws ChattyExceptions, IOException {
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
        return output;
    }
}
