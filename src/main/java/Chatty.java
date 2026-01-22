import java.util.Scanner;

public class Chatty {
    public static void main(String[] args) {
        System.out.println("Hello! I'm Chatty");
        System.out.println("What can I do for you?");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String inpt = sc.nextLine();
            if (inpt.equals("Bye") || inpt.equals("bye")) {
                System.out.println("Bye. Hope to see you again!");
                break;
            } else {
                System.out.println(inpt);
            }
        }
        sc.close();
    }
}
