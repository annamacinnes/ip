package chatty.task;

import java.time.LocalDate;

public class Todo extends Task {

    public Todo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public boolean willOccurOn(LocalDate date) {
        return false;
    }
}
