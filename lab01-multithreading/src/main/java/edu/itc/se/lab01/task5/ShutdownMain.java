package edu.itc.se.lab01.task5;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class ShutdownMain {

    public static void main(String[] args)
            throws InterruptedException {

        TryLockBank bank = new TryLockBank();
        TryLockBank.Account[] accounts = new TryLockBank.Account[4];

        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new TryLockBank.Account(i, 1_000);
        }

        AtomicLong completed = new AtomicLong();
        Thread[] workers = new Thread[8];

        for (int w = 0; w < workers.length; w++) {
            workers[w] = new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                long localCompleted = 0;

                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        int fromIndex = random.nextInt(accounts.length);

                        int toIndex = random.nextInt(accounts.length - 1);

                        if (toIndex >= fromIndex) {
                            toIndex++;
                        }

                        bank.transfer(
                                accounts[fromIndex],
                                accounts[toIndex],
                                random.nextInt(1, 50)
                        );

                        localCompleted++;
                        completed.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    // Restore the flag before leaving the worker.
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println(
                            Thread.currentThread().getName()
                                    + " stopped after "
                                    + localCompleted
                                    + " transfer attempts"
                    );
                }
            }, "transfer-" + w);

            workers[w].start();
        }

        // Let the workers run for two seconds.
        Thread.sleep(2_000);

        System.out.println("[main] Interrupting 8 workers after 2000 ms");

        long shutdownStart = System.nanoTime();

        for (Thread worker : workers) {
            worker.interrupt();
        }

        // Wait at most one second for each worker.
        for (Thread worker : workers) {
            worker.join(1_000);
        }

        long shutdownMs =
                (System.nanoTime() - shutdownStart) / 1_000_000;

        boolean allStopped = true;

        for (Thread worker : workers) {
            boolean alive = worker.isAlive();

            System.out.println(
                    worker.getName() + " alive=" + alive
            );

            if (alive) {
                allStopped = false;
            }
        }

        System.out.println("All workers stopped: " + allStopped);
        System.out.println("Shutdown time: " + shutdownMs + " ms");

        // Read final balances only after every worker has stopped.
        if (allStopped) {
            long total = 0;

            for (TryLockBank.Account account : accounts) {
                total += account.balance();
            }

            System.out.println(
                    "Completed transfer attempts: " + completed.get()
            );
            System.out.println("Retries: " + bank.retries());
            System.out.println(
                    "Total balance: " + total + " (expected 4000)"
            );
            System.out.println("Correct balance: " + (total == 4_000));
        } else {
            System.out.println(
                    "Shutdown check failed: some workers are still running."
            );
        }
    }
}