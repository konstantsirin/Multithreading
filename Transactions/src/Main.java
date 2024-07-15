
public class Main {

    public static void main(String[] args) {
        int countTransaction = 100000;
        Bank bank = new Bank();
        long sum = bank.getSumAllAccounts();
        System.out.println("Контрольная сумма: " + sum);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("==========================");
            if (bank.getSumAllAccounts() == sum) {
                System.out.println("Сумма осталось прежней");
            } else {
                System.out.println("Что-то пошло не так)))");
                System.out.println("Сумма была " + sum);
                System.out.println("Сумма стала " + bank.getSumAllAccounts());
            }
            if (bank.hasAccountToNegativeBalance()) {
                System.out.println("В банке имеются аккаунты с отрицательным балансом");
            } else {
                System.out.println("В банке акаунты с отрицательным балансом не найдены");
            }
            System.out.println("==========================");
            System.out.println();
        }));

        for (int i = 0; i < countTransaction; i++) {
            Thread thread = new Thread(() -> {
                try {
                    bank.generateRndTransaction();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }
    }

    public static int rnd(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }
}
