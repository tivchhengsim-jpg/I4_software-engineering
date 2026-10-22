package edu.itc.se.lab01.task2;

import java.beans.beancontext.BeanContextChild;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        
        Bench.run("UnsafeCounter run " , new UnsafeCounter());
        Bench.run("SynchronizedCounter", new SynchronizedCounter());
        Bench.run("LockCounter", new LockCounter());
        Bench.run("AtomicCounter", new AtomicCounter());
        Bench.run("LongAdderCounter", new LongAdderCounter());
       
    }
}
//wrong result get from running UnsafeCounter 3 time : 
// - value=2,297,214  correct=false  35 ms  
// - value=1,872,588  correct=false  54 ms 
// - value=2,356,406  correct=false  42 ms

