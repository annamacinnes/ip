package chatty.storage;

import chatty.ChattyExceptions;
import chatty.parser.Parser;
import chatty.task.Task;
import chatty.task.TaskList;
import chatty.ui.Ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * The {@code Storage} class handles saving tasks to disk and loading them back
 * into memory for the Chatty application.
 *
 * <p>Tasks are stored as plain text in a file, with each line representing
 * one task. When loading, task strings are parsed to reconstruct the
 * appropriate {@link Task} objects.</p>
 */
public class Storage {

    /**
     * The file path where Chatty task data is stored.
     */
    private static final String FILE_PATH =
            "/Users/annamacinnes/ip/text-ui-test/data/chatty.txt";

    /**
     * Writes the current task list to the storage file.
     *
     * <p>If the parent directories do not exist, they will be created
     * automatically. Existing file contents will be overwritten.</p>
     *
     * @param tasksToAdd the {@link TaskList} containing tasks to be saved
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public static void writeToFile(TaskList tasksToAdd) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            tasksToAdd.writeToFile(fw);
        } catch (IOException e) {
            Ui.loadErrorMessage(e.getMessage());
        }
    }

    /**
     * Loads tasks from the storage file and reconstructs them into a {@link TaskList}.
     *
     * <p>Each line in the file is parsed into a {@link Task} using the
     * {@link Parser}. If a task line indicates completion, the task
     * will be marked as completed.</p>
     *
     * @return a {@link TaskList} containing all loaded tasks
     * @throws FileNotFoundException if the storage file does not exist
     * @throws ChattyExceptions if a task cannot be parsed correctly
     */
    public static TaskList load() throws FileNotFoundException, ChattyExceptions {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        Scanner s = new Scanner(file);

        TaskList tasks = new TaskList();
        while (s.hasNext()) {
            String taskDescription = s.nextLine();
            tasks.add(Parser.parseTaskFromFile(taskDescription));

            if (taskDescription.contains("[X]")) {
                tasks.get(tasks.size() - 1).setComplete();
            }
        }
        return tasks;
    }
}
