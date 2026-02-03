package chatty.task;

public class Task {
    protected final String name;
    protected boolean complete;

    public Task(String name) {
        this.name = name;
        this.complete = false;
    }

    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.name;
    }

    public String getStatusIcon() {
        return (complete ? "X" : " ");
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void markComplete() {
        this.complete = true;
    }

    public void markIncomplete() {
        this.complete = false;
    }

    public String getType() {
        return "todo";
    }
}
