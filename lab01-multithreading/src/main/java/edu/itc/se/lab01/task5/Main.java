package edu.itc.se.lab01.task5;

import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args)
            throws InterruptedException {

        Bank.Account[] accounts = new Bank.Account[4];

        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Bank.Account(i, 1_000);
        }

        System.out.println("Process ID: " + ProcessHandle.current().pid());
        System.out.println("Starting 8 transfer workers...");

        DeadlockWatchdog.start();

        Thread[] workers = new Thread[8];

        for (int w = 0; w < workers.length; w++) {
            workers[w] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();

                for (int i = 0; i < 100_000; i++) {
                    Bank.Account from = accounts[random.nextInt(accounts.length)];

                    Bank.Account to = accounts[random.nextInt(accounts.length)];

                    if (from != to) {
                        Bank.transfer(from, to, random.nextInt(1, 50));
                    }
                }
            }, "transfer-" + w);

            workers[w].start();
        }
        for (Thread worker : workers) {
            worker.join();
        }
        long total = 0;

        for (Bank.Account account : accounts) {
            total += account.balance();
        }

        System.out.println("Total balance: " + total + " (expected 4000)");
    }
}