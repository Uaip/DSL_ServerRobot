package mysocketRobot;

public class user {
    // At present, there are only two parameters in user class
    private String name = "";
    private int amount = 0;

    /**
     * constructor
     * 
     * @param s
     * @param amount
     */
    public user(String s, int amount) {
        this.name = s;
        this.amount = amount;
    }

    /**
     * set user's amount of account
     * 
     * @param a
     */
    public void setamount(int a) {
        this.amount = a;
    }

    /**
     * set user's UserName
     * 
     * @param s
     */
    public void setName(String s) {
        this.name = s;
    }

    /**
     * 
     * @return this.name
     */
    public String getName() {
        return this.name;
    }

    /**
     * 
     * @return this.name
     */
    public int getAmount() {
        return this.amount;
    }

    public String toString() {
        return "name:\t" + this.name + "amount\t" + this.amount;
    }
}
