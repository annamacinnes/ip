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
        assert tasksToAdd != null : "TaskList passed to writeToFile should not be null";
        assert FILE_PATH != null && !FILE_PATH.isBlank()
                : "FILE_PATH should not be null or blank";

        File file = new File(FILE_PATH);
        File parent = file.getParentFile();

        assert parent != null : "Parent directory should not be null";

        parent.mkdirs();
        assert parent.exists() : "Parent directory should exist after mkdirs()";

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
    public static TaskList load() throws ChattyExceptions, IOException {
        assert FILE_PATH != null && !FILE_PATH.isBlank()
                : "FILE_PATH should not be null or blank";

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.createNewFile();
        }
        File parent = file.getParentFile();

        assert parent != null : "Parent directory should not be null";

        parent.mkdirs();

        assert file.exists() : "File should exist before loading";

        Scanner s = new Scanner(file);
        assert s != null : "Scanner should not be null";
        TaskList tasks = new TaskList();
        while (s.hasNext()) {
            String taskDescription = s.nextLine();
            assert taskDescription != null : "Task description line should not be null";
            assert taskDescription.contains("[")
                    : "Saved task format should contain type indicator";

            Task parsedTask = Parser.parseTaskFromFile(taskDescription);

            assert parsedTask != null : "Parsed task should not be null";
            tasks.add(parsedTask);

            if (taskDescription.contains("[X]")) {
                assert tasks.size() > 0 : "There must be a task before marking complete";
                tasks.get(tasks.size() - 1).setComplete();
            }
        }
        return tasks;
    }
}
