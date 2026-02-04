package chatty;

import chatty.parser.Parser;
import chatty.storage.Storage;
import chatty.task.Task;
import chatty.task.TaskList;
import chatty.ui.Ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Chatty {
    public enum Command {
        COMMAND_TODO,
        COMMAND_DEADLINE,
        COMMAND_EVENT,
        COMMAND_MARK,
        COMMAND_UNMARK,
        COMMAND_DELETE,
        COMMAND_LIST,
        COMMAND_BYE,
        COMMAND_DUE,
        COMMAND_UNKNOWN // fallback for invalid commands
    }

    public static void main(String[] args) throws FileNotFoundException, ChattyExceptions {
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
                case COMMAND_BYE:
                    Ui.printByeMessage();
                    inLoop = false;
                    break;
                case COMMAND_LIST:
                    Ui.listTaskMessage();
                    taskList.list();
                    break;
                case COMMAND_DUE:
                    taskList.getTasksDueOn(input);
                    break;
                case COMMAND_MARK, COMMAND_UNMARK, COMMAND_DELETE:
                    taskList.markTask(command, input);
                    Storage.writeToFile(taskList);
                    break;
                case COMMAND_TODO, COMMAND_DEADLINE, COMMAND_EVENT:
                    Task toAdd = Parser.parseAddTaskCommand(command, input);
                    taskList.add(toAdd);
                    Ui.addTaskMessage(toAdd, taskList);
                    Storage.writeToFile(taskList);
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



