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
        BYE,
        DUE,
        FIND,
        UNKNOWN // fallback for invalid commands
    }

    /**
     * Starts the Chatty application.
     *
     * <p>This method loads existing tasks from storage, displays the welcome
     * message, and continuously processes user input until the exit command
     * is issued.</p>
     *
     * @param args command-line arguments (not used)
     * @throws FileNotFoundException if the storage file cannot be found
     * @throws ChattyExceptions if an unrecoverable application error occurs
     */
    public static void main(String[] args)
            throws FileNotFoundException, ChattyExceptions {

        Ui.printWelcomeMessage();
        Scanner sc = new Scanner(System.in);
        TaskList taskList = Storage.load();
        boolean inLoop = true;

        while (inLoop) {
            try {
                String input = sc.nextLine().trim();

                if (input.isBlank()) {
                    ChattyExceptions.emptyCommand();
                    continue;
                }

                Command command = Parser.parseCommand(input);

                switch (command) {
                case BYE:
                    Ui.printByeMessage();
                    inLoop = false;
                    break;

                case LIST:
                    Ui.listTaskMessage();
                    taskList.list();
                    break;

                case DUE:
                    taskList.getTasksDueOn(input);
                    break;

                case MARK, UNMARK, DELETE:
                    taskList.markTask(command, input);
                    Storage.writeToFile(taskList);
                    break;

                case TODO, DEADLINE, EVENT:
                    Task toAdd = Parser.parseAddTaskCommand(command, input);
                    taskList.add(toAdd);
                    Ui.addTaskMessage(toAdd, taskList);
                    Storage.writeToFile(taskList);
                    break;
                case FIND:
                    String keyword = Parser.parseKeywordToFind(input);
                    TaskList tL = taskList.find(keyword);
                    if (tL.isEmpty()) {
                        Ui.noMatchingTasksMessage();
                    } else {
                        Ui.matchingTasksMessage();
                        tL.list();
                    }
                    break;
                default:
                    ChattyExceptions.unknownCommand();
                }
            } catch (ChattyExceptions | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
