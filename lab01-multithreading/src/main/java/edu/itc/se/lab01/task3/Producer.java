package edu.itc.se.lab01.task3;

import java.util.concurrent.ThreadLocalRandom;

public class Producer implements Runnable{

    private final OrderQueue queue;
    private  final int firstID ;
    private int orderCount;

    public Producer(OrderQueue queue, int firstID, int orderCount) {
        this.queue = queue;
        this.firstID = firstID;
        this.orderCount = orderCount;
    }

    public void run(){
        try{
            for(int i=0; i<orderCount; i++){
                Order order = new Order( firstID+i, "Ice latte", ThreadLocalRandom.current().nextInt(1,4));
                queue.put(order);

                System.out.println(Thread.currentThread().getName() + " produced" + order);
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 51));
            }
            
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println( Thread.currentThread().getName() + " interrupted");
        }
    }
    
    
}
