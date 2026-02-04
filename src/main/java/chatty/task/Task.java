package chatty.task;

/**
 * Represents a generic task in the Chatty application.
 *
 * <p>A {@code Task} has a description and a completion status.
 * Specific task types such as {@code Todo}, {@code Deadline}, and
 * {@code Event} extend this class.</p>
 */
public class Task {
    /**
     * The description of the task.
     */
    protected final String name;
    protected boolean isComplete;

    /**
     * Constructs a {@code Task} with the given description.
     * The task is initially marked as incomplete.
     *
     * @param name the description of the task
     */
    public Task(String name) {
        this.name = name;
        this.isComplete = false;
    }

    /**
     * Returns a string representation of the task for display and storage.
     *
     * <p>The string includes the completion status icon followed by
     * the task description.</p>
     *
     * @return a formatted string representing the task
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.name;
    }

    public String getStatusIcon() {
        return (isComplete ? "X" : " ");
    }

    public void setComplete() {
        this.isComplete = true;
    }

    public void setIncomplete() {
        this.isComplete = false;
    }

    public String getType() {
        return "todo";
    }

    public String getName() {
        return this.name;
    }
}
