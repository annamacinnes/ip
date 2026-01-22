public class Task {
    private final String name;
    private boolean complete;

    public Task(String name) {
        this.name = name;
        this.complete = false;
    }

    public String getName() {
        return this.name;
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
}
