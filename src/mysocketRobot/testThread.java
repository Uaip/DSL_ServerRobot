package mysocketRobot;


public class testThread {
    user a = null;
    int step = 100;

    public void speak() {
        // String name = "";
        // int amount = 0;
        // System.out.println("user name set:");
        // Scanner sc = new Scanner(System.in);
        // name = sc.nextLine();
        // System.out.println("user amount set:");
        // amount = sc.nextInt();
        // a = new user(name, amount);
        // System.out.print(a.getName() + a.getAmount());
        Thread t = Thread.currentThread();
        System.out.println(step--);
        for (int i = 0; i < 10; i++)
            System.out.print(step-- + " " + t.getName() + "\n");
        System.out.println();

    }
}
