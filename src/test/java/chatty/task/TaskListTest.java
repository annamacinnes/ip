package chatty.task;

import chatty.ChattyExceptions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskListTest {

    /* =========================
       add(), size(), isEmpty()
       ========================= */

    @Test
    public void constructor_newList_isEmpty() {
        TaskList list = new TaskList();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    public void add_task_increasesSize() {
        TaskList list = new TaskList();
        list.add(new Todo("read"));

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    /* =========================
       get()
       ========================= */

    @Test
    public void get_validIndex_returnsCorrectTask() {
        TaskList list = new TaskList();
        Todo t = new Todo("read");
        list.add(t);

        assertEquals(t, list.get(0));
    }

    /* =========================
       remove()
       ========================= */

    @Test
    public void remove_validIndex_reducesSize() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));

        list.remove(0);

        assertEquals(1, list.size());
        assertEquals("b", list.get(0).getName());
    }

    /* =========================
       find()
       ========================= */

    @Test
    public void find_existingKeyword_returnsMatchingTasks() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("write code"));
        list.add(new Todo("book flight"));

        TaskList result = list.find("book");

        assertEquals(2, result.size());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("read"));

        TaskList result = list.find("xyz");

        assertTrue(result.isEmpty());
    }

    /* =========================
       getTasksDueOn()
       ========================= */

    @Test
    public void getTasksDueOn_deadlineOnDate_returnsTask() throws ChattyExceptions {
        TaskList list = new TaskList();

        Deadline deadline = new Deadline(
                "submit",
                LocalDate.of(2026, 3, 1)
        );
        list.add(deadline);

        TaskList result =
                list.getTasksDueOn(LocalDate.of(2026, 3, 1));

        assertEquals(1, result.size());
    }

    @Test
    public void getTasksDueOn_eventSpanningDate_returnsTask() throws ChattyExceptions {
        TaskList list = new TaskList();

        Event event = new Event(
                "conference",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 3)
        );
        list.add(event);

        TaskList result =
                list.getTasksDueOn(LocalDate.of(2026, 3, 2));

        assertEquals(1, result.size());
    }

    @Test
    public void getTasksDueOn_noMatchingTasks_returnsEmptyList() throws ChattyExceptions {
        TaskList list = new TaskList();
        list.add(new Todo("read"));

        TaskList result =
                list.getTasksDueOn(LocalDate.of(2026, 3, 1));

        assertTrue(result.isEmpty());
    }

    /* =========================
       markTask()
       ========================= */

    @Test
    public void markTask_validIndexes_marksTasksComplete() {
        TaskList list = new TaskList();
        Todo t1 = new Todo("a");
        Todo t2 = new Todo("b");
        list.add(t1);
        list.add(t2);

        List<Integer> indexes = List.of(0, 1);
        TaskList marked = list.markTask(indexes);

        assertEquals(2, marked.size());
        assertTrue(t1.isComplete());
        assertTrue(t2.isComplete());
    }

    /* =========================
       unmarkTask()
       ========================= */

    @Test
    public void unmarkTask_validIndexes_marksTasksIncomplete() {
        TaskList list = new TaskList();
        Todo t1 = new Todo("a");
        list.add(t1);

        t1.setComplete();
        assertTrue(t1.isComplete());

        list.unmarkTask(List.of(0));

        assertFalse(t1.isComplete());
    }

    /* =========================
       deleteTask()
       ========================= */

    @Test
    public void deleteTask_singleIndex_removesCorrectTask() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));

        TaskList deleted = list.deleteTask(List.of(0));

        assertEquals(1, deleted.size());
        assertEquals(1, list.size());
        assertEquals("b", list.get(0).getName());
    }

    @Test
    public void deleteTask_multipleIndexes_removesCorrectTasks() {
        TaskList list = new TaskList();
        list.add(new Todo("a")); // 0
        list.add(new Todo("b")); // 1
        list.add(new Todo("c")); // 2

        TaskList deleted = list.deleteTask(List.of(0, 2));

        assertEquals(2, deleted.size());
        assertEquals(1, list.size());
        assertEquals("b", list.get(0).getName());
    }
}