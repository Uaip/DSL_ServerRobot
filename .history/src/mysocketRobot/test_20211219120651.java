package mysocketRobot;

public class test {
    public static void main(String[] args) {
        new test().Integerset();
    }

    public void Integerset() {
        try {
            int s = Integer.parseInt("--9");
        } catch (NumberFormatException e) {
            System.out.println("格式错误");
        }
    }
}
