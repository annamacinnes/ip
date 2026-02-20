package chatty.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents tasks that have a deadline.
 *
 * <p>A {@code Deadline} task has a description and a date by which the task
 *  * should be completed.</p>
 */
public class Deadline extends Task{
    /**
     * The date by which the task has to be completed
     */
    protected LocalDate by;

    /**
     * Constructs a {@code Deadline} task with the specified name and deadline date.
     *
     * @param name the description of the task
     * @param by   the deadline date of the task
     */
    public Deadline(String name, LocalDate by) {
        super(name);
        this.by = by;
    }

    @Override
    public boolean willOccurOn(LocalDate date) {
        return this.getDeadline().isEqual(date);
    }

    public LocalDate getDeadline() {
        return this.by;
    }

    /**
     * Returns a string representation of the deadline task for storage and display.
     *
     * <p>The date is formatted using the pattern {@code "MMM dd yyyy"}.</p>
     *
     * @return a formatted string representing the deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + "(by: "
                + this.by.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
    }

}
