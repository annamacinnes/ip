package chatty.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;

    public Event(String name, LocalDate from, LocalDate to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    public LocalDate getStartDate() {
        return this.from;
    }

    public LocalDate getEndDate() {
        return this.to;
    }

    @Override
    public String getType() {
        return "event";
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + "(from: " + this.from.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                                            + " to: "
                                            + this.to.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                                            + ")";
    }
}
