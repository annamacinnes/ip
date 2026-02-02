import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getStartDate() {
        return this.from;
    }

    public LocalDateTime getEndDate() {
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
