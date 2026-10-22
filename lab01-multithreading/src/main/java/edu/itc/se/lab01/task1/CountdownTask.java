package edu.itc.se.lab01.task1;

public class CountdownTask implements Runnable{
    
    public void run(){
        String name = Thread.currentThread().getName();

        try{
             
            for(int i=3; i>=1; i--){
                System.err.println(name+ ": " + i);
                Thread.sleep(200);
            }
            System.out.println(name+" : Liftoff");
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println(name + " -> Countdown interrupted");
        }
    }
}
