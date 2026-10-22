package edu.itc.se.lab01.task2;

import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounter implements Counter{
    private final LongAdder count = new LongAdder();

    public void increment(){
        count.increment();
    }

    public long get(){
        return count.sum();
    }
}
