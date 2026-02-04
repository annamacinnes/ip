package chatty.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task that occurs over a period of time.
 *
 * <p>An {@code Event} has a description, a start date, and an end date.</p>
 */
public class Event extends Task {
    /**
     * The start date of the event.
     */
    protected LocalDate from;
    /**
     * The end date of the event.
     */
    protected LocalDate to;

    /**
     * Constructs an {@code Event} task with the specified name, start date,
     * and end date.
     *
     * @param name the description of the event
     * @param from the start date of the event
     * @param to   the end date of the event
     */
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

    /**
     * Returns a string representation of the event task for storage and display.
     *
     * <p>The start and end dates are formatted using the pattern
     * {@code "MMM dd yyyy"}.</p>
     *
     * @return a formatted string representing the event task
     */
    @Override
    public String toString() {
        return "[E]"
                + super.toString()
                + "(from: "
                + this.from.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                + " to: "
                + this.to.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                + ")";
    }
}
