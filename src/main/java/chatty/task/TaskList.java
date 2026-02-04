package chatty.task;

import chatty.Chatty;
import chatty.ChattyExceptions;
import chatty.parser.Parser;
import chatty.ui.Ui;

import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public int size() {
        return tasks.size();
    }

    public Task get(int i) {
        return tasks.get(i);
    }

    public void remove(int i) {
        tasks.remove(i);
    }

    public Boolean isEmpty() {
        return tasks.isEmpty();
    }

    public void list() {
        int i = 1;
        for (Task task : tasks) {
            System.out.printf("%d. %s%n", i, task.toString());
            i++;
        }
        System.out.printf("%n");
    }

    public void writeToFile(FileWriter fw) {
        int i = 1;
        for (Task task : tasks) {
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
            for (Task task : tasks) {
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
            throws ChattyExceptions {
        int taskNum = Parser.parseTaskIndex(input, this);
        switch (command) {
        case COMMAND_MARK:
            this.get(taskNum).setComplete();
            Ui.markTaskMessage(taskNum, this);
            break;

        case COMMAND_UNMARK:
            tasks.get(taskNum).setIncomplete();
            Ui.unmarkTaskMessage(taskNum, this);
            break;

        case COMMAND_DELETE:
            Ui.deleteTaskMessage(taskNum, this);
            this.remove(taskNum);
            break;
        }
    }


}