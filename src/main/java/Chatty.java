import java.util.Scanner;

public class Chatty {
    public static void main(String[] args) {
        System.out.println("Hello! I'm Chatty");
        System.out.println("What can I do for you?");
        Scanner sc = new Scanner(System.in);
        String[] storage = new String[100];
        int current = 0;
        while (true) {
            String inpt = sc.nextLine();
            if (inpt.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again!");
                break;
            } else if (inpt.equalsIgnoreCase("list")) {
                for (int i = 0; i < current; i++) {
                    System.out.printf("%d: %s%n", i + 1, storage[i]);
                }
            } else {
                storage[current] = inpt;
                current++;
                System.out.printf("added: %s%n", inpt);
            }
        }
        sc.close();
    }
}
