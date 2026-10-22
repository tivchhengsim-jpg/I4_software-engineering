package edu.itc.se.lab01.task5;

public class Bank {

    public static final class Account {
        final int id;
        private long balance;

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

    // Deliberately broken: lock order follows transfer direction.
    public static void transfer(
            Account from,
            Account to,
            long amount
    ) {
        synchronized (from) {
            synchronized (to) {
                if (from.balance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
            }
        }
    }
}