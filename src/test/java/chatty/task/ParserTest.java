package chatty.task;

import chatty.Chatty;
import chatty.ChattyExceptions;
import chatty.parser.Parser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    /* =========================
       parseCommand()
       ========================= */

    @Test
    public void parseCommand_validTodo_returnsTODO() {
        assertEquals(Chatty.Command.TODO,
                Parser.parseCommand("todo read book"));
    }

    @Test
    public void parseCommand_mixedCase_returnsCorrectCommand() {
        assertEquals(Chatty.Command.DEADLINE,
                Parser.parseCommand("DeAdLiNe hw"));
    }

    @Test
    public void parseCommand_unknownCommand_returnsUNKNOWN() {
        assertEquals(Chatty.Command.UNKNOWN,
                Parser.parseCommand("random text"));
    }

    @Test
    public void parseCommand_extraWhitespace_stillWorks() {
        assertEquals(Chatty.Command.TODO,
                Parser.parseCommand("   todo     read"));
    }

    /* =========================
       parseTaskIndex()
       ========================= */

    @Test
    public void parseTaskIndex_singleValidIndex_returnsCorrectZeroBased() throws ChattyExceptions {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));

        ArrayList<Integer> index =
                Parser.parseTaskIndex("delete 2", list);

        assertEquals(1, index.get(0));
    }

    @Test
    public void parseTaskIndex_multipleIndexes_returnsAll() throws ChattyExceptions {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));
        list.add(new Todo("c"));

        ArrayList<Integer> indexes =
                Parser.parseTaskIndex("delete 1 3", list);

        assertEquals(2, indexes.size());
        assertTrue(indexes.contains(0));
        assertTrue(indexes.contains(2));
    }

    @Test
    public void parseTaskIndex_missingIndex_throwsException() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(ChattyExceptions.class,
                () -> Parser.parseTaskIndex("delete", list));
    }

    @Test
    public void parseTaskIndex_nonInteger_throwsException() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(ChattyExceptions.class,
                () -> Parser.parseTaskIndex("delete two", list));
    }

    @Test
    public void parseTaskIndex_outOfBounds_throwsException() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(ChattyExceptions.class,
                () -> Parser.parseTaskIndex("delete 3", list));
    }

    /* =========================
       parseAddTaskCommand()
       ========================= */

    @Test
    public void parseAddTaskCommand_todo_createsTodo() throws ChattyExceptions {
        Task task = Parser.parseAddTaskCommand(
                Chatty.Command.TODO,
                "todo read book"
        );

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseAddTaskCommand_todo_emptyDescription_throwsException() {
        assertThrows(ChattyExceptions.class,
                () -> Parser.parseAddTaskCommand(
                        Chatty.Command.TODO,
                        "todo"
                ));
    }

    @Test
    public void parseAddTaskCommand_deadline_valid_createsDeadline() throws ChattyExceptions {
        Task task = Parser.parseAddTaskCommand(
                Chatty.Command.DEADLINE,
                "deadline submit report /by 2026-03-01"
        );

        Deadline d = (Deadline) task;
        assertEquals(LocalDate.of(2026, 3, 1),
                d.getDeadline());
    }

    @Test
    public void parseAddTaskCommand_deadline_invalidDate_throwsException() {
        assertThrows(ChattyExceptions.class,
                () -> Parser.parseAddTaskCommand(
                        Chatty.Command.DEADLINE,
                        "deadline submit report /by invalid-date"
                ));
    }

    @Test
    public void parseAddTaskCommand_deadline_missingBy_throwsException() {
        assertThrows(ChattyExceptions.class,
                () -> Parser.parseAddTaskCommand(
                        Chatty.Command.DEADLINE,
                        "deadline submit report"
                ));
    }

    @Test
    public void parseAddTaskCommand_event_valid_createsEvent() throws ChattyExceptions {
        Task task = Parser.parseAddTaskCommand(
                Chatty.Command.EVENT,
                "event conference /from 2026-03-01 /to 2026-03-03"
        );

        assertInstanceOf(Event.class, task);
    }

    @Test
    public void parseAddTaskCommand_event_missingTo_throwsException() {
        assertThrows(ChattyExceptions.class,
                () -> Parser.parseAddTaskCommand(
                        Chatty.Command.EVENT,
                        "event conference /from 2026-03-01"
                ));
    }

    /* =========================
       parseKeywordToFind()
       ========================= */

    @Test
    public void parseKeyword_valid_returnsKeyword() throws ChattyExceptions {
        String keyword = Parser.parseKeywordToFind("find book");
        assertEquals("book", keyword);
    }

    @Test
    public void parseKeyword_missingKeyword_throwsException() {
        assertThrows(ChattyExceptions.class,
                () -> Parser.parseKeywordToFind("find"));
    }

    /* =========================
       parseDateToFind()
       ========================= */

    @Test
    public void parseDateToFind_valid_returnsDate() throws ChattyExceptions {
        LocalDate date = Parser.parseDateToFind("due 2026-03-01");
        assertEquals(LocalDate.of(2026, 3, 1), date);
    }

    @Test
    public void parseDateToFind_missingDate_throwsException() {
        assertThrows(ChattyExceptions.class,
                () -> Parser.parseDateToFind("due"));
    }

    @Test
    public void parseDateToFind_invalidDate_throwsException() {
        assertThrows(Exception.class,
                () -> Parser.parseDateToFind("due invalid-date"));
    }

    /* =========================
       parseTaskFromFile()
       ========================= */

    @Test
    public void parseTaskFromFile_todo_createsTodo() throws ChattyExceptions {
        Task task = Parser.parseTaskFromFile("[T][ ] read book");
        assertInstanceOf(Todo.class, task);
    }

    @Test
    public void parseTaskFromFile_deadline_createsDeadline() throws ChattyExceptions {
        Task task = Parser.parseTaskFromFile(
                "[D][ ] submit report (by: Mar 01 2026)"
        );

        assertInstanceOf(Deadline.class, task);
    }

    @Test
    public void parseTaskFromFile_event_createsEvent() throws ChattyExceptions {
        Task task = Parser.parseTaskFromFile(
                "[E][ ] conference (from: Mar 01 2026 to: Mar 03 2026)"
        );

        assertInstanceOf(Event.class, task);
    }

    @Test
    public void parseTaskFromFile_invalidDate_throwsException() {
        assertThrows(ChattyExceptions.class,
                () -> Parser.parseTaskFromFile(
                        "[D][ ] submit report (by: InvalidDate)"
                ));
    }
}