import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Storage {
    private static final String FILE_PATH = "/Users/annamacinnes/ip/text-ui-test/data/chatty.txt";

    public static void writeToFile(TaskList tasksToAdd) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(FILE_PATH)){
            tasksToAdd.writeToFile(fw);
        } catch (IOException e) {
            Ui.loadErrorMessage(e.getMessage());
        }
    }

    public static TaskList load() throws FileNotFoundException, ChattyExceptions {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        Scanner s = new Scanner(file);
        TaskList tasks = new TaskList();
        while (s.hasNext()) {
            String taskDescription = s.nextLine();
            tasks.add(Parser.parseFileTaskName(taskDescription));
            if (taskDescription.contains("[X]")) {
                tasks.get(tasks.size() - 1).markComplete();
            }
        }
        return tasks;
    }
}
