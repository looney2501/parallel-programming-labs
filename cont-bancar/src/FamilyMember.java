import java.util.Random;

public class FamilyMember extends Thread {
    private final int iterations;
    private final Account account;
    private long id;

    public FamilyMember(int iterations, Account account) {
        this.iterations = iterations;
        this.account = account;
    }

    @Override
    public void run() {
        this.id = Thread.currentThread().getId();

        for (int i = 0; i < iterations; i++) {
            makeTransaction();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void makeTransaction() {
        String transactionType = generateTransactionType();
        String currency = generateCurrency();
        int sum = generateSum();
        if (transactionType.equals("depunere")) {
            if (currency.equals("RON")) {
                account.depositRON(id, sum);
            }
            if (currency.equals("EUR")) {
                account.depositEUR(id, sum);
            }
        } else {
            if (currency.equals("RON")) {
                account.withdrawRON(id, sum);
            }
            if (currency.equals("EUR")) {
                account.withdrawEUR(id, sum);
            }
        }
    }

    private String generateTransactionType() {
        Random r = new Random();
        return r.nextBoolean() ? "depunere" : "retragere";
    }

    private String generateCurrency() {
        Random r = new Random();
        return r.nextBoolean() ? "RON" : "EUR";
    }

    private int generateSum() {
        Random r = new Random();
        return r.nextInt(1, 1001);
    }
}
