package edu.itc.se.lab01.task3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements  Runnable{

    private  final OrderQueue queue;
    
    private final AtomicInteger processedCount;

    public Consumer(OrderQueue queue, AtomicInteger processedCount) {
        this.queue = queue;
        this.processedCount = processedCount;
    }

    @Override 

    public void run(){
        try{
            while ((true)) {
                Order order = queue.take();

                if(order.id() == -1){
                    System.out.println( Thread.currentThread().getName() + "stopped");
                    break;  
                }

                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 51));

                processedCount.incrementAndGet();

                System.out.println(Thread.currentThread().getName() + " Proccessed" + order);
                
            }
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " interrupted");
        }
    }

    
}
