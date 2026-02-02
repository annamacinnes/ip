public class ChattyExceptions extends Exception {
    // Constructor to pass custom messages
    public ChattyExceptions(String message) {
        super(message);
    }

    public static void emptyCommand() throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Please type a command!%n"));
    }

    public static void invalidTaskNumber() throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Invalid task number!%n"));
    }

    public static void missingTaskNumber() throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Please specify the task number after the command.%n"));
    }

    public static void nonIntegerTaskNumber() throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Please enter a valid integer for the task number.%n"));
    }

    public static void emptyDescription(String command) throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Oops! The description after a " + command + " cannot be empty.%n"));
    }

    public static void invalidDeadlineFormat() throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Invalid format. Use: deadline <description> /by <date>%n"));
    }

    public static void invalidEventFormat() throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Invalid format. Correct usage: event <name> /from <start> /to <end>%n"));
    }

    public static void emptyEventFields() throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Event name, start time, and end time cannot be empty.%n"));
    }

    public static void unknownCommand() throws ChattyExceptions {
        throw new ChattyExceptions(String.format("Sorry, I don't know what that means!%n"));
    }

    public static void invalidDateFormat() throws ChattyExceptions {
        throw new ChattyExceptions((String.format("Invalid date format. Dates should be formatted as yyyy-mm-dd")));
    }
}
