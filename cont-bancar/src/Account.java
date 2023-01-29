import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    public static class AccountRegistry {
        private final List<String> transactions;
        private final Lock lock;
        private final Condition addedFiveTransactionsConditionVariable;
        private final Condition finishedPrintingConditionVariable;

        public AccountRegistry() {
            transactions = new ArrayList<>();
            lock = new ReentrantLock();
            addedFiveTransactionsConditionVariable = lock.newCondition();
            finishedPrintingConditionVariable = lock.newCondition();
        }

        public void addTransaction(String transaction) {
            transactions.add(transaction);
        }

        List<String> getTransactions() {
            return transactions;
        }

        public Lock getLock() {
            return lock;
        }

        public Condition getAddedFiveTransactionsConditionVariable() {
            return addedFiveTransactionsConditionVariable;
        }

        public Condition getFinishedPrintingConditionVariable() {
            return finishedPrintingConditionVariable;
        }
    }
    private int sumRON;
    private int sumEUR;
    private final Lock lockRON;
    private final Lock lockEUR;
    private final AtomicInteger oldSize;

    public AccountRegistry getAccountRegistry() {
        return accountRegistry;
    }

    private final AccountRegistry accountRegistry;

    public Account(AtomicInteger oldSize) {
        this.lockRON = new ReentrantLock();
        this.lockEUR = new ReentrantLock();
        this.sumRON = 0;
        this.sumEUR = 0;
        this.accountRegistry = new AccountRegistry();
        this.oldSize = oldSize;
    }

    public void depositRON(long id, int sum) {
        lockRON.lock();

        sumRON += sum;

        accountRegistry.getLock().lock();

        while (accountRegistry.getTransactions().size() == oldSize.get() + 5) {
            try {
                accountRegistry.getFinishedPrintingConditionVariable().await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        accountRegistry.addTransaction("index_user: " + id + ", tip_valuta: RON, tip_tranzactie: depunere, suma: " + sum);
        if (accountRegistry.getTransactions().size() % 5 == 0) {
            accountRegistry.getAddedFiveTransactionsConditionVariable().signal();
        }

        accountRegistry.getLock().unlock();

        lockRON.unlock();
    }

    public void depositEUR(long id, int sum) {
        lockEUR.lock();

        sumEUR += sum;

        accountRegistry.getLock().lock();

        while (accountRegistry.getTransactions().size() == oldSize.get() + 5) {
            try {
                accountRegistry.getFinishedPrintingConditionVariable().await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        accountRegistry.addTransaction("index_user: " + id + ", tip_valuta: EUR, tip_tranzactie: depunere, suma: " + sum);
        if (accountRegistry.getTransactions().size() % 5 == 0) {
            accountRegistry.getAddedFiveTransactionsConditionVariable().signal();
        }

        accountRegistry.getLock().unlock();

        lockEUR.unlock();
    }

    public void withdrawRON(long id, int sum) {
        lockRON.lock();

        if (sumRON < sum) {
            System.out.println("Cannot withdraw RON - existing: " + sumRON + " - sum: " + sum);
        } else {
            sumRON -= sum;

            accountRegistry.getLock().lock();

            while (accountRegistry.getTransactions().size() == oldSize.get() + 5) {
                try {
                    accountRegistry.getFinishedPrintingConditionVariable().await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            accountRegistry.addTransaction("index_user: " + id + ", tip_valuta: RON, tip_tranzactie: retragere, suma: " + sum);
            if (accountRegistry.getTransactions().size() % 5 == 0) {
                accountRegistry.getAddedFiveTransactionsConditionVariable().signal();
            }

            accountRegistry.getLock().unlock();
        }

        lockRON.unlock();
    }

    public void withdrawEUR(long id, int sum) {
        lockEUR.lock();

        if (sumEUR < sum) {
            System.out.println("Cannot withdraw EUR - existing: " + sumEUR + " - sum: " + sum);
        } else {
            sumEUR -= sum;

            accountRegistry.getLock().lock();

            while (accountRegistry.getTransactions().size() == oldSize.get() + 5) {
                try {
                    accountRegistry.getFinishedPrintingConditionVariable().await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            accountRegistry.addTransaction("index_user: " + id + ", tip_valuta: EUR, tip_tranzactie: retragere, suma: " + sum);
            if (accountRegistry.getTransactions().size() % 5 == 0) {
                accountRegistry.getAddedFiveTransactionsConditionVariable().signal();
            }

            accountRegistry.getLock().unlock();
        }

        lockEUR.unlock();
    }
}
