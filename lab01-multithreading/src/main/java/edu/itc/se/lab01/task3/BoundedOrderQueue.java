package edu.itc.se.lab01.task3;

import java.util.ArrayDeque;
import java.util.Queue;

public class BoundedOrderQueue implements OrderQueue{
    private final Queue<Order> orders = new ArrayDeque<>();
    private final int capacity;

    public  BoundedOrderQueue(int capacity) { 
        while(capacity<=0){
            throw new IllegalArgumentException("Capacity must be positive");

        }
        this.capacity = capacity;
    }
    
    @Override
    public synchronized void put(Order order) throws InterruptedException {
        while (orders.size() >= capacity) {
            wait();
        }

        orders.add(order);
        notifyAll();  
    }

    public  synchronized Order take() throws InterruptedException{
        while (orders.isEmpty()){
            wait();
        }

        Order order = orders.remove();

        notifyAll();
        return  order;
    }
}
