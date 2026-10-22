package edu.itc.se.lab01.task5;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockBank {

    public static final class Account {
        final int id;
        private long balance;
        private final ReentrantLock lock = new ReentrantLock();

        public Account(int id, long balance) {
            this.id = id;
            this.balance = balance;
        }

        public long balance() {
            return balance;
        }

        public void withdraw(long amount) {
            balance -= amount;
        }

        public void deposit(long amount) {
            balance += amount;
        }
    }

    private final AtomicLong retries = new AtomicLong();

    public long retries() {
        return retries.get();
    }

    public void transfer(Account from, Account to, long amount)
            throws InterruptedException {

        if (from == to) {
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            if (from.lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                try {
                    if (to.lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                        try {
                            if (from.balance() >= amount) {
                                from.withdraw(amount);
                                to.deposit(amount);
                            }

                            return;
                        } finally {
                            to.lock.unlock();
                        }
                    }
                } finally {
                    from.lock.unlock();
                }
            }

            // All acquired locks are released before backing off.
            retries.incrementAndGet();

            Thread.sleep(
                    ThreadLocalRandom.current().nextInt(1, 6)
            );
        }

        throw new InterruptedException("Transfer interrupted");
    }
}