package chatty;

import chatty.parser.Parser;
import chatty.storage.Storage;
import chatty.task.Task;
import chatty.task.TaskList;
import chatty.ui.Ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * The main entry point of the Chatty application.
 *
 * <p>{@code Chatty} coordinates user input, command parsing, task management,
 * storage operations, and user interface output. It runs an input-processing
 * loop until the user exits the application.</p>
 */
public class Chatty {
    /**
     * Represents all supported commands in the Chatty application.
     *
     * <p>{@code COMMAND_UNKNOWN} is used as a fallback for invalid commands.</p>
     */
    public enum Command {
        TODO,
        DEADLINE,
        EVENT,
        MARK,
        UNMARK,
        DELETE,
        LIST,
        DUE,
        FIND,
        UNKNOWN // fallback for invalid commands
    }

    private final TaskList taskList;

    public Chatty() throws IOException, ChattyExceptions {
        taskList = Storage.load();
    }

    public String getResponse(String input) {
        try {
            if (input.isBlank()) {
                ChattyExceptions.emptyCommand();
            }

            Command command = Parser.parseCommand(input);
            return Parser.executeCommand(command, taskList, input);
        } catch (ChattyExceptions e) {
            return e.getMessage();
        } catch (IOException e) {
            return "File error: " + e.getMessage();
        }
    }
}
