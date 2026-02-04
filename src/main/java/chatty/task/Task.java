package chatty.task;

public class Task {
    protected final String name;
    protected boolean isComplete;

    public Task(String name) {
        this.name = name;
        this.isComplete = false;
    }

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
}
