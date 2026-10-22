package edu.itc.se.lab01.task5;

public class OrderedBank {

    public static void transfer(
            Bank.Account from,
            Bank.Account to,
            long amount
    ) {
        if (from == to) {
            return;
        }

        Bank.Account first = from.id < to.id ? from : to;
        Bank.Account second = from.id < to.id ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.balance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
            }
        }
    }
}