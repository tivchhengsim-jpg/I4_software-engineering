package edu.itc.se.lab01.task5;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderedMain {

    public static void main(String[] args)
            throws InterruptedException {

        Bank.Account[] accounts = new Bank.Account[4];

        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Bank.Account(i, 1_000);
        }

        AtomicInteger completed = new AtomicInteger();
        Thread[] workers = new Thread[8];

        long start = System.nanoTime();

        for (int w = 0; w < workers.length; w++) {
            workers[w] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();

                for (int i = 0; i < 100_000; i++) {
                    int fromIndex = random.nextInt(accounts.length);

                    // Choose a different destination account.
                    int toIndex = random.nextInt(accounts.length - 1);

                    if (toIndex >= fromIndex) {
                        toIndex++;
                    }

                    OrderedBank.transfer(
                            accounts[fromIndex],
                            accounts[toIndex],
                            random.nextInt(1, 50)
                    );

                    completed.incrementAndGet();
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

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Completed transfer attempts: " + completed.get());
        System.out.println("Total balance: " + total + " (expected 4000)");
        System.out.println("Correct balance: " + (total == 4_000));
        System.out.println("Elapsed time: " + elapsedMs + " ms");
    }
}