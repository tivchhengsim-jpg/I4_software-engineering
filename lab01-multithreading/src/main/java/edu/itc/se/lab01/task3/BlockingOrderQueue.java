package edu.itc.se.lab01.task3;

import java.util.concurrent.ArrayBlockingQueue;

public class BlockingOrderQueue implements OrderQueue {
    private final ArrayBlockingQueue<Order> orders;

    public BlockingOrderQueue(int capacity) {
        orders = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void put(Order order) throws InterruptedException {
        orders.put(order);
    }

    @Override
    public Order take() throws InterruptedException {
        return orders.take();
    }
}