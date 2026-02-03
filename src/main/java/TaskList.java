import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TaskList {
    private final ArrayList<Task> storage;

    public TaskList() {
        this.storage = new ArrayList<>();
    }

    public void add(Task task) {
        storage.add(task);
    }

    public int size() {
        return storage.size();
    }

    public Task get(int i) {
        return storage.get(i);
    }

    public void remove(int i) {
        storage.remove(i);
    }

    public Boolean isEmpty() {
        return storage.isEmpty();
    }

    public void list() {
        int i = 1;
        for (Task task: storage) {
            System.out.printf("%d. %s%n", i ,task.toString());
            i++;
        }
        System.out.printf("%n");
    }

    public void writeToFile(FileWriter fw) {
        int i = 1;
        for (Task task: storage) {
            try {
                fw.write(String.format("%d. %s%n", i, task.toString()));
                i++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getTasksDueOn(String input) throws ChattyExceptions {
        if (input.split("\\s+").length < 2) {
            ChattyExceptions.emptyDescription("due");
        }
        TaskList toPrint = new TaskList();
        try {
            LocalDate dateToFind = LocalDate.parse(input.split("\\s+")[1]);
            for (Task task: storage) {
                if (task.getType().equalsIgnoreCase("event")) {
                    Event event = (Event) task;
                    if (event.getStartDate().isEqual(dateToFind)
                            || event.getEndDate().isEqual(dateToFind)
                            || (event.getEndDate().isAfter(dateToFind) && event.getStartDate().isBefore(dateToFind))) {
                        toPrint.add(event);
                    }
                } else if ((task.getType().equalsIgnoreCase("deadline"))) {
                    Deadline deadline = (Deadline) task;
                    if (deadline.getDeadline().isEqual(dateToFind)) {
                        toPrint.add(deadline);
                    }
                }
            }
            if (toPrint.isEmpty()) {
                Ui.noRelevantTaskMessage();
            } else {
                Ui.relevantTasksMessage(dateToFind);
                toPrint.list();
            }
        } catch (DateTimeException e) {
            ChattyExceptions.invalidDateFormat();
        }
    }

    public void markTask(Chatty.Command command, String input)
            throws ChattyExceptions{
        int taskNum = Parser.parseTaskIndex(input, this);
        switch(command) {
            case MARK:
                this.get(taskNum).markComplete();
                Ui.markTaskMessage(taskNum, this);
                break;

            case UNMARK:
                storage.get(taskNum).markIncomplete();
                Ui.unmarkTaskMessage(taskNum, this);
                break;

            case DELETE:
                Ui.deleteTaskMessage(taskNum, this);
                this.remove(taskNum);
                break;
        }
    }




}