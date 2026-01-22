import java.util.Scanner;

public class Chatty {
    public static void main(String[] args) {
        System.out.println("Hello! I'm Chatty");
        System.out.println("What can I do for you?");
        Scanner sc = new Scanner(System.in);
        Task[] storage = new Task[100];
        int current = 0;

        while (true) {
            String input = sc.nextLine();
            String[] inputArr = input.split("\\s+");

            if (input.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again!");
                break;
            } else if (input.equalsIgnoreCase("list")) {
                for (int i = 0; i < current; i++) {
                    System.out.printf("%d.[%s] %s%n", i + 1, storage[i].getStatusIcon(), storage[i].getName());
                }
            } else if (inputArr[0].equalsIgnoreCase("mark")) {
                int taskNum = Integer.parseInt(inputArr[1]) - 1;
                storage[taskNum].markComplete();
                System.out.println("Nice! I've marked this task as done:");
                System.out.printf("[%s] %s%n", storage[taskNum].getStatusIcon(), storage[taskNum].getName());
            } else if (inputArr[0].equalsIgnoreCase("unmark")){
                int taskNum = Integer.parseInt(inputArr[1]) - 1;
                storage[taskNum].markIncomplete();
                System.out.println("OK, I've marked this task as not done yet :");
                System.out.printf("[%s] %s%n", storage[taskNum].getStatusIcon(), storage[taskNum].getName());
            } else {
                storage[current] = new Task(input);
                current++;
                System.out.printf("added: %s%n", input);
            }
        }
        sc.close();
    }
}
