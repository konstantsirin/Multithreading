import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Bank {
    Integer countAccounts = 100000;
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final AtomicInteger currentNum = new AtomicInteger();

    public Bank() {
        addingAccountsToBank(countAccounts);
    }

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        System.out.printf("Проверка службы безопасности перевода на сумму %d с аккаунта %s на аккаунт %s\r\n",
                amount, fromAccountNum, toAccountNum
        );
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        Account accFrom = accounts.get(fromAccountNum);
        Account accTo = accounts.get(toAccountNum);

        Account firstLock = accFrom.compareTo(accTo) < 0 ? accFrom : accTo;
        Account secondLock = accFrom.compareTo(accTo) < 0 ? accTo : accFrom;

        if (accFrom.compareTo(accTo) != 0) {
            synchronized (firstLock) {
                synchronized (secondLock) {
                    if (accFrom.isBlocked() || accTo.isBlocked()) {
                        String blockedAcc = "";
                        if (accFrom.isBlocked()) blockedAcc = fromAccountNum;
                        if (accTo.isBlocked() && blockedAcc.isEmpty()) {
                            blockedAcc = toAccountNum;
                        } else {
                            blockedAcc += (", " + toAccountNum);
                        }
                        System.out.printf("Операция отклонена по причине блокировки одного или нескольких аккаунтов. Заблокированные аккаунты: %s\r\n", blockedAcc);
                    } else if (amount > 50000) {
                        boolean isDetectedSuspiciousActivity = isFraud(fromAccountNum, toAccountNum, amount);
                        if (isDetectedSuspiciousActivity) {
                            System.out.printf("Замечена подозрительная активность. Аккаунты %s и %s заблокированы!\r\n", fromAccountNum, toAccountNum);
                            accFrom.blockedAccount();
                            accTo.blockedAccount();
                        } else {
                            transferMoney(accFrom, accTo, amount);
                        }
                    } else {
                        transferMoney(accFrom, accTo, amount);
                    }
                }
            }
        }

    }

    private void transferMoney(Account accFrom, Account accTo, long amount) {
        System.out.printf("Перевод с аккаунта %s(Баланс: %d) на аккаунт %s(Баланс: %d). Сумма перевода: %d. ",
                accFrom.getAccNumber(), getBalance(accFrom.getAccNumber()),
                accTo.getAccNumber(), getBalance(accTo.getAccNumber()),
                amount
        );
        long moneyAccFrom = accFrom.getMoney();
        long moneyAccTo = accTo.getMoney();
        if (accFrom.setMoney(moneyAccFrom - amount)) {
            System.out.println("Операция одобрена.");
            accTo.setMoney(moneyAccTo + amount);
        } else {
            System.out.println("Недостаточно средств на аккаунте " + accFrom.getAccNumber() + "; Операция отклонена.");
        }
    }

    public void generateRndTransaction() throws InterruptedException {
        String accNumFrom = String.valueOf(Main.rnd(1, countAccounts));
        String accNumTo = String.valueOf(Main.rnd(1, countAccounts));

        int moneyForTransaction = Main.rnd(10000, 60000);
        transfer(accNumFrom, accNumTo, moneyForTransaction);

    }

    public long getBalance(String accountNum) {
        Account account = accounts.get(accountNum);
        return account.getMoney();
    }

    public long getSumAllAccounts() {
        long total = 0L;
        if (!accounts.isEmpty()) {
            total = accounts.values().parallelStream().map(Account::getMoney).reduce(0L, Long::sum);
        }
        return total;
    }

    private void addingAccountsToBank(int countAccounts) {
        for (int i = 0; i < countAccounts; i++) {
            Account acc = createAccount();
            String accNum = acc.getAccNumber();
            accounts.put(accNum, acc);
        }
    }

    private Account createAccount() {
        int currentAccNum = currentNum.incrementAndGet();
        int money = Main.rnd(0, 150000);
        return new Account(String.valueOf(currentAccNum), money);
    }

    public boolean hasAccountToNegativeBalance() {
        if (!accounts.isEmpty()) {
            return accounts.values().parallelStream().anyMatch(acc -> acc.getMoney() < 0);
        }
        return false;
    }
}
