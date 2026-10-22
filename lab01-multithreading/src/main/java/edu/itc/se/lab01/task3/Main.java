package edu.itc.se.lab01.task3;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        OrderQueue queue = new BoundedOrderQueue(5);
        AtomicInteger processedCount = new AtomicInteger(0);

        Thread[] producers = {
                new Thread(new Producer(queue, 1, 50), "producer-1"),
                new Thread(new Producer(queue, 51, 50), "producer-2")
        };

        Thread[] consumers = {
                new Thread(new Consumer(queue, processedCount), "consumer-1"),
                new Thread(new Consumer(queue, processedCount), "consumer-2"),
                new Thread(new Consumer(queue, processedCount), "consumer-3")
        };

        for (Thread consumer : consumers) {
            consumer.start();
        }

        for (Thread producer : producers) {
            producer.start();
        }

        // Wait until all normal orders have been added.
        for (Thread producer : producers) {
            producer.join();
        }

        // Add one stop signal for each consumer.
        for (int i = 0; i < consumers.length; i++) {
            queue.put(new Order(-1, "STOP", 0));
        }

        // Wait until all consumers finish processing and stop.
        for (Thread consumer : consumers) {
            consumer.join();
        }

        int total = processedCount.get();

        System.out.println();
        System.out.println("Expected orders: 100");
        System.out.println("Processed orders: " + total);
        System.out.println("Correct total: " + (total == 100));
    }
}