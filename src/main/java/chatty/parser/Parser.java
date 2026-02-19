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
import java.util.ArrayList;
import java.util.List;

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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final String BY_FLAG = "/by";
    private static final String FROM_FLAG = "/from";
    private static final String TO_FLAG = "/to";
    private static final String FILE_BY_PREFIX = "(by: ";
    private static final String FILE_FROM_PREFIX = "from: ";
    private static final String FILE_TO_PREFIX = "to: ";
    private static final String FILE_EVENT_SEPARATOR = " to:";
    private static final String FILE_EVENT_LABEL = "[E]";
    private static final String FILE_TODO_LABEL = "[T]";
    private static final String FILE_DEADLINE_LABEL = "[D]";
    private static final String FILE_STATUS_SUFFIX = "] ";

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
    public static ArrayList<Integer> parseTaskIndex(String input, TaskList storage) throws ChattyExceptions {
        String[] parts = input.split("\\s+");
        assert !input.isBlank(): "Input to parseTaskIndex should not be blank";
        assert storage != null : "TaskList should not be null";
        assert storage.size() >= 0 : "TaskList size cannot be negative";

        if (parts.length < 2) {
            ChattyExceptions.missingTaskNumber();
        }

        try {
            ArrayList<Integer> indexes = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                int index = Integer.parseInt(parts[i]) - 1;
                if (index < 0 || index >= storage.size()) {
                    ChattyExceptions.invalidTaskNumber();
                    return new ArrayList<>();
                }
                indexes.add(index);
            }
            return indexes;
        } catch (NumberFormatException e) {
            ChattyExceptions.nonIntegerTaskNumber();
            return null; // unreachable
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
     * Parses a {@code deadline} command input string and constructs a {@link Deadline} task.
     *
     * <p>The expected input format is:
     * <pre>
     * deadline &lt;description&gt; /by &lt;yyyy-MM-dd&gt;
     * </pre>
     *
     * <p>Example:
     * <pre>
     * deadline submit report /by 2026-03-01
     * </pre>
     *
     * @param input The full user input string containing the deadline command.
     * @return A {@link Deadline} object with the parsed description and due date.
     * @throws ChattyExceptions If:
     * <ul>
     *     <li>The {@code /by} flag is missing</li>
     *     <li>The description is empty</li>
     *     <li>The date is missing or not in valid {@code yyyy-MM-dd} format</li>
     * </ul>
     */
    public static Task parseDeadline(String input) throws ChattyExceptions {
        int byIndex = input.indexOf(BY_FLAG);
        if (byIndex == -1) {
            ChattyExceptions.invalidDeadlineFormat();
        }
        String DeadlineName = input.substring("deadline ".length(), input.indexOf(BY_FLAG));
        String date = input.substring(byIndex + BY_FLAG.length() + 1);
        try {
            LocalDate parsedBy = LocalDate.parse(date);
            return new Deadline(DeadlineName, parsedBy);
        } catch (DateTimeException e) {
            ChattyExceptions.invalidDateFormat();
        }
        throw new AssertionError("Unreachable code reached in parseDeadline");
    }

    /**
     * Parses an {@code event} command input string and constructs an {@link Event} task.
     *
     * <p>The expected input format is:
     * <pre>
     * event &lt;description&gt; /from &lt;yyyy-MM-dd&gt; /to &lt;yyyy-MM-dd&gt;
     * </pre>
     *
     * <p>Example:
     * <pre>
     * event project meeting /from 2026-03-01 /to 2026-03-02
     * </pre>
     *
     * @param input The full user input string containing the event command.
     * @return An {@link Event} object with the parsed description, start date, and end date.
     * @throws ChattyExceptions If:
     * <ul>
     *     <li>The {@code /from} or {@code /to} flag is missing</li>
     *     <li>The description, start date, or end date is empty</li>
     *     <li>Either date is not in valid {@code yyyy-MM-dd} format</li>
     * </ul>
     */
    public static Task parseEvent(String input) throws ChattyExceptions {
        int fromIndex = input.indexOf(FROM_FLAG);
        int toIndex = input.indexOf(TO_FLAG);
        if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
            ChattyExceptions.invalidEventFormat();
        }
        String name = input.substring("event ".length(), input.indexOf(FROM_FLAG));
        String from = input.substring(fromIndex + FROM_FLAG.length() + 1, toIndex - 1);
        String to = input.substring(toIndex + TO_FLAG.length() + 1);
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
        throw new AssertionError("Unreachable code reached in parseEvent");
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
        return switch (command) {
            case DEADLINE -> parseDeadline(input);
            case EVENT -> parseEvent(input);
            case TODO -> new Todo(input.substring("todo ".length()));
            default -> throw new AssertionError("Unreachable code reached in parseAddTaskCommand");
        };
    }

    /**
     * Parses a user input string to create a new {@link Deadline} object for add-task commands.
     *
     * @param taskDescription the description of the task from the file
     * @param taskName  the name of the task
     * @return a {@link Deadline} object corresponding to the input command
     */
    private static Deadline parseDeadlineFromFile(String taskDescription, String taskName) {
        String date = taskDescription.substring(
                        taskDescription.indexOf(FILE_BY_PREFIX) + FILE_BY_PREFIX.length(),
                        taskDescription.length() - 1)
                .trim();
        return new Deadline(taskName, LocalDate.parse(date, formatter));
    }

    /**
     * Parses a user input string to create a new {@link Event} object for add-task commands.
     *
     * @param taskDescription the description of the task from the file
     * @param taskName  the name of the task
     * @return a {@link Event} object corresponding to the input command
     */
    private static Event parseEventFromFile(String taskDescription, String taskName) {
        String from = taskDescription.substring(
                taskDescription.indexOf(FILE_FROM_PREFIX) + FILE_FROM_PREFIX.length(),
                taskDescription.indexOf(FILE_EVENT_SEPARATOR))
                .trim();
        String to = taskDescription.substring(
                taskDescription.indexOf(FILE_TO_PREFIX) + FILE_TO_PREFIX.length(),
                taskDescription.length() - 1)
                .trim();
        return new Event(
                taskName,
                LocalDate.parse(from, formatter),
                LocalDate.parse(to, formatter)
        );
    }

    /**
     * Parses a task description from a file and reconstructs the corresponding {@link Task}.
     *
     * @param taskDescription the task description string from the file
     * @return a {@link Task} object representing the saved task
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static Task parseTaskFromFile(String taskDescription) throws ChattyExceptions {
        assert taskDescription != null : "Task description from file should not be null";
        assert taskDescription.contains(FILE_STATUS_SUFFIX) : "Saved task format should contain closing bracket";

        int startOfNameIndex = taskDescription.indexOf(FILE_STATUS_SUFFIX) + FILE_STATUS_SUFFIX.length();

        if (taskDescription.contains(FILE_TODO_LABEL)) {
            return new Todo(taskDescription.substring(startOfNameIndex));
        } else {
            try {
                String taskName = taskDescription.substring(startOfNameIndex, taskDescription.indexOf("(")).trim();
                if (taskDescription.contains(FILE_DEADLINE_LABEL)) {
                    return parseDeadlineFromFile(taskDescription, taskName);

                } else if (taskDescription.contains(FILE_EVENT_LABEL)) {
                    return parseEventFromFile(taskDescription, taskName);
                }
            } catch (DateTimeException e) {
                ChattyExceptions.invalidDateFormat();
            }
        }
        throw new AssertionError("Unreachable code reached in parseTaskFromFile");
    }

    /**
     * Parses a task description from user input and reconstructs the corresponding {@link LocalDate}.
     *
     * @param input the task description string from the file
     * @return a {@link LocalDate} object representing the relevant task date
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static LocalDate parseDateToFind(String input) throws ChattyExceptions {
        if (input.split("\\s+").length < 2) {
            ChattyExceptions.emptyDescription("due");
        }
        return LocalDate.parse(input.split("\\s+")[1]);
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
    public static String executeCommand(Chatty.Command command,
                                         TaskList taskList, String input) throws ChattyExceptions, IOException {
        assert command != null : "Command should not be null";
        assert taskList != null : "TaskList should not be null";
        assert input != null : "Input should not be null";
        switch (command) {
        case LIST:
            return Ui.listTaskMessage(taskList);
        case DUE:
            return executeDueCommand(taskList, input);
        case MARK:
            return executeMarkCommand(taskList, input);
        case UNMARK:
            return executeUnmarkCommand(taskList, input);
        case DELETE:
            return executeDeleteCommand(taskList, input);
        case TODO, DEADLINE, EVENT:
            return executeAddTaskCommand(taskList, input, command);
        case FIND:
            return executeFindCommand(taskList, input);
        default:
            ChattyExceptions.unknownCommand();
        }
        throw new AssertionError("Unreachable code reached in executeCommand");
    }

    /**
     * Executes the Due command and returns the corresponding {@link Ui} message.
     *
     * @param input the task description string from the file
     * @param taskList the list of tasks to search from
     * @return a {@link String} object representing the corresponding {@link Ui} message
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static String executeDueCommand(TaskList taskList, String input) throws ChattyExceptions {
        LocalDate date = Parser.parseDateToFind(input);
        TaskList tasksDue = taskList.getTasksDueOn(date);
        return Ui.dueTasksMessage(date, tasksDue);
    }

    /**
     * Executes the Mark command and returns the corresponding {@link Ui} message.
     *
     * @param input the task description string from the file
     * @param taskList the list of tasks to mark tasks from
     * @return a {@link String} object representing the corresponding {@link Ui} message
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static String executeMarkCommand(TaskList taskList, String input) throws ChattyExceptions, IOException {
        List<Integer> taskIndexes = Parser.parseTaskIndex(input, taskList);
        assert taskIndexes != null;
        TaskList markedTasks = taskList.markTask(taskIndexes);
        Storage.writeToFile(taskList);
        return Ui.markTaskMessage(markedTasks);
    }

    /**
     * Executes the Unmark command and returns the corresponding {@link Ui} message.
     *
     * @param input the task description string from the file
     * @param taskList the list of tasks to unmark tasks from
     * @return a {@link String} object representing the corresponding {@link Ui} message
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static String executeUnmarkCommand(TaskList taskList, String input) throws ChattyExceptions, IOException {
        List<Integer> taskIndexes = parseTaskIndex(input, taskList);
        assert taskIndexes != null;
        TaskList unmarkedTasks = taskList.unmarkTask(taskIndexes);
        Storage.writeToFile(taskList);
        return Ui.unmarkTaskMessage(unmarkedTasks);
    }

    /**
     * Executes the Delete command and returns the corresponding {@link Ui} message.
     *
     * @param input the task description string from the file
     * @param taskList the list of tasks to delete tasks from
     * @return a {@link String} object representing the corresponding {@link Ui} message
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static String executeDeleteCommand(TaskList taskList, String input) throws ChattyExceptions, IOException {
        List<Integer> taskIndexes = parseTaskIndex(input, taskList);
        assert taskIndexes != null;
        TaskList deletedTasks = taskList.deleteTask(taskIndexes);
        Storage.writeToFile(taskList);
        return Ui.deleteTaskMessage(taskList, deletedTasks);
    }

    /**
     * Executes the (TODO/DEADLINE/EVENT) command and returns the corresponding {@link Ui} message.
     *
     * @param command the command that invoked this execution (TODO, DEADLINE, EVENT)
     * @param input the task description string from the file
     * @param taskList the list of tasks to add tasks to
     * @return a {@link String} object representing the corresponding {@link Ui} message
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static String executeAddTaskCommand(TaskList taskList, String input, Chatty.Command command)
            throws IOException, ChattyExceptions {
        Task toAdd = parseAddTaskCommand(command, input);
        taskList.add(toAdd);
        Storage.writeToFile(taskList);
        return Ui.addTaskMessage(toAdd, taskList);
    }

    /**
     * Executes the Find command and returns the corresponding {@link Ui} message.
     *
     * @param input the task description string from the file
     * @param taskList the list of tasks to find tasks from
     * @return a {@link String} object representing the corresponding {@link Ui} message
     * @throws ChattyExceptions if the date format is invalid or the input is malformed
     */
    public static String executeFindCommand(TaskList taskList, String input) throws ChattyExceptions {
        String keyword = parseKeywordToFind(input);
        TaskList tL = taskList.find(keyword);
        return Ui.matchingTasksMessage(tL);
    }
}
