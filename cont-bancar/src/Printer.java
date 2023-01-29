import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Printer extends Thread {
    private final Account.AccountRegistry accountRegistry;
    private final AtomicBoolean isFinished;
    private final AtomicInteger oldLength;

    public Printer(Account.AccountRegistry accountRegistry, AtomicBoolean isFinished, AtomicInteger oldSize) {
        this.accountRegistry = accountRegistry;
        this.isFinished = isFinished;
        this.oldLength = oldSize;
    }

    @Override
    public void run() {
        while (!isFinished.get()) {
            accountRegistry.getLock().lock();

            while (accountRegistry.getTransactions().size() % 5 != 0 || oldLength.get() == accountRegistry.getTransactions().size() || accountRegistry.getTransactions().size() == 0) {
                try {
                    if (!isFinished.get()) {
                        accountRegistry.getAddedFiveTransactionsConditionVariable().await();
                    } else {
                        break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (accountRegistry.getTransactions().size() % 5 == 0) {
                printTransactions();
                oldLength.set(accountRegistry.getTransactions().size());

                accountRegistry.getFinishedPrintingConditionVariable().signalAll();
            }

            accountRegistry.getLock().unlock();
        }
    }

    private void printTransactions() {
        System.out.println("Transactions:");
        accountRegistry.getTransactions().forEach(System.out::println);
        System.out.println();
    }
}
