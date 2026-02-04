package chatty.task;

import chatty.Chatty;
import chatty.ChattyExceptions;
import chatty.parser.Parser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class ParserTest {
    @Test
    public void parseCommand_validTodo_returnsTODO() throws ChattyExceptions {
        assertEquals(Chatty.Command.COMMAND_TODO, Parser.parseCommand("todo read book"));
    }

    @Test
    public void parseCommand_mixedCase_returnsCorrectCommand() throws ChattyExceptions {
        assertEquals(Chatty.Command.COMMAND_DEADLINE, Parser.parseCommand("DeAdLiNe hw"));
    }

    @Test
    public void parseCommand_unknownCommand_returnsUNKNOWN() throws ChattyExceptions {
        assertEquals(Chatty.Command.COMMAND_UNKNOWN, Parser.parseCommand("random text"));
    }

    @Test
    public void parseTaskIndex_validIndex_returnsCorrectIndex() throws ChattyExceptions {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));

        int index = Parser.parseTaskIndex("delete 2", list);
        assertEquals(1, index);
    }

    @Test
    public void parseTaskIndex_nonInteger_throwsException() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(ChattyExceptions.class, () ->
                Parser.parseTaskIndex("delete two", list));
    }

    @Test
    public void parseTaskIndex_outOfBounds_throwsException() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(ChattyExceptions.class, () ->
                Parser.parseTaskIndex("delete 3", list));
    }

    @Test
    public void parseAddTaskCommand_todo_createsTodo() throws ChattyExceptions {
        Task task = Parser.parseAddTaskCommand(
                Chatty.Command.COMMAND_TODO,
                "todo read book"
        );

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseAddTaskCommand_deadline_valid_createsDeadline() throws ChattyExceptions {
        Task task = Parser.parseAddTaskCommand(
                Chatty.Command.COMMAND_DEADLINE,
                "deadline submit report /by 2026-03-01"
        );

        assertInstanceOf(Deadline.class, task);
        Deadline d = (Deadline) task;
        assertEquals(LocalDate.of(2026, 3, 1), d.getDeadline());
    }

    @Test
    public void parseAddTaskCommand_deadline_missingBy_throwsException() {
        assertThrows(ChattyExceptions.class, () ->
                Parser.parseAddTaskCommand(
                        Chatty.Command.COMMAND_DEADLINE,
                        "deadline submit report"
                ));
    }

    @Test
    public void parseAddTaskCommand_event_valid_createsEvent() throws ChattyExceptions {
        Task task = Parser.parseAddTaskCommand(
                Chatty.Command.COMMAND_EVENT,
                "event conference /from 2026-03-01 /to 2026-03-03"
        );

        assertInstanceOf(Event.class, task);
    }

    @Test
    public void parseFileTaskName_todo_createsTodo() throws ChattyExceptions {
        Task task = Parser.parseFileTaskName("[T][ ] read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseFileTaskName_deadline_createsDeadline() throws ChattyExceptions {
        Task task = Parser.parseFileTaskName(
                "[D][ ] submit report (by: Mar 01 2026)"
        );

        assertInstanceOf(Deadline.class, task);
    }

    @Test
    public void parseFileTaskName_event_createsEvent() throws ChattyExceptions {
        Task task = Parser.parseFileTaskName(
                "[E][ ] conference (from: Mar 01 2026 to: Mar 03 2026)"
        );

        assertInstanceOf(Event.class, task);
    }

}
