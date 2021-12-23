package mysocketRobot;

public class user {
    private String name = "";
    private int amount = 0;

    public user(String s, int amount) {
        this.name = s;
        this.amount = amount;
    }

    public void setamount(int a) {
        this.amount = a;
    }

    public void setName(String s) {
        this.name = s;
    }

    public String getName() {
        return this.name;
    }

    public int getAmount() {
        return this.amount;
    }

    public String toString() {
        return "name:\t" + this.name + "amount\t" + this.amount;
    }
}
