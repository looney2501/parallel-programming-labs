import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        AtomicBoolean isFinished = new AtomicBoolean(false);
        AtomicInteger oldSize = new AtomicInteger(0);
        Account account = new Account(oldSize);

        FamilyMember[] familyMembers = new FamilyMember[3];
        for (int i = 0; i < familyMembers.length; i++) {
            familyMembers[i] = new FamilyMember(20, account);
            familyMembers[i].start();
        }

        Printer printer = new Printer(account.getAccountRegistry(), isFinished, oldSize);
        printer.start();

        for (int i = 0; i < familyMembers.length; i++) {
            try {
                familyMembers[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        isFinished.set(true);
        account.getAccountRegistry().getLock().lock();
        account.getAccountRegistry().getAddedFiveTransactionsConditionVariable().signal();
        account.getAccountRegistry().getLock().unlock();

        try {
            printer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
