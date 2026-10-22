package edu.itc.se.lab01.task1;

public class Countdown extends Thread{

    public Countdown(String name){
        super(name);
    }

    public void run(){

        try{
            
            for(int i=3; i>=1; i--){
                System.err.println(getName()+ ": " + i);
                Thread.sleep(200);
            }
            System.out.println(getName()+ ": Liftoff");
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println(getName()+" -> Countdown interrupted");
        }
    }
    
}
