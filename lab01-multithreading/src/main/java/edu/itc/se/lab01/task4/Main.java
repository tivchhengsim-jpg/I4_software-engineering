package edu.itc.se.lab01.task4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
        try {
            runPrimeComparison();

            runDownloads("Fixed pool (20)", false);
            runDownloads("Virtual threads", true);

            System.out.println("All executors closed. Program finished.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main interrupted.");

        } catch (ExecutionException e) {
            System.out.println("Prime task failed: " + e.getCause());
        }
    }

    private static void runPrimeComparison()
            throws InterruptedException, ExecutionException {

        final long limit = 4_000_000;
        final int chunks = 16;
        int cores = Runtime.getRuntime().availableProcessors();

        long start = System.nanoTime();

        long single = new PrimeCounter(0, limit).call();

        long singleMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf(
                "Single-threaded: %,d primes in %d ms%n",
                single, singleMs
        );

        start = System.nanoTime();
        long total = 0;

        try (ExecutorService pool = Executors.newFixedThreadPool(cores)) {
            List<Future<Long>> futures = new ArrayList<>();

            // Submit all 16 tasks before collecting results.
            for (int i = 0; i < chunks; i++) {
                long from = i * limit / chunks;
                long to = (i + 1) * limit / chunks;

                futures.add(pool.submit(new PrimeCounter(from, to)));
            }

            for (Future<Long> future : futures) {
                total += future.get();
            }
        }

        long poolMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf(
                "Pool (%d threads): %,d primes in %d ms%n",
                cores, total, poolMs
        );

        System.out.println("Same result: " + (single == total));
        System.out.println();
    }

    private static void runDownloads(String label, boolean virtual)
            throws InterruptedException {

        long start = System.nanoTime();

        int ok = 0;
        int failed = 0;
        int timedOut = 0;

        ExecutorService executor = virtual
                ? Executors.newVirtualThreadPerTaskExecutor()
                : Executors.newFixedThreadPool(20);

        try (executor) {
            List<Future<String>> futures = new ArrayList<>();

            for (int number = 1; number <= 500; number++) {
                String url = "https://example.com/file-" + number;

                futures.add(
                        executor.submit(new FakeDownload(url, number))
                );
            }

            for (int i = 0; i < futures.size(); i++) {
                Future<String> future = futures.get(i);

                try {
                    if (i == 0) {
                        // Check the slow download first.
                        future.get(1, TimeUnit.SECONDS);
                    } else {
                        future.get();
                    }

                    ok++;

                } catch (TimeoutException e) {
                    future.cancel(true);
                    timedOut++;

                    System.out.println(
                            label + ": download 1 timed out; cancellation requested."
                    );

                } catch (ExecutionException e) {
                    failed++;

                    System.out.println(
                            label + ": download " + (i + 1)
                                    + " failed: " + e.getCause()
                    );
                }
            }
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf(
                "%s: ok=%d failed=%d timedOut=%d in %d ms%n%n",
                label, ok, failed, timedOut, elapsedMs
        );
    }
}