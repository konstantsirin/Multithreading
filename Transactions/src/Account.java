public class Account implements Comparable<Account> {
    private long money = 0;
    private String accNumber;
    private volatile Boolean isBlocked = false;

    public Account(String accNumber) {
        setAccNumber(accNumber);
    }

    public Account(String accNumber, long money) {
        setAccNumber(accNumber);
        this.money = money;
    }

    public long getMoney() {
        return money;
    }

    public synchronized boolean setMoney(long money) {
        if (money >= 0) {
            this.money = money;
            return true;
        }
        return false;
    }

    public String getAccNumber() {
        return accNumber;
    }

    private void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public void blockedAccount() {
        isBlocked = true;
    }

    public void unblockedAccount() {
        isBlocked = false;
    }

    public Boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public int compareTo(Account a) {
        return this.getAccNumber().compareTo(a.getAccNumber());
    }
}
