package edu.itc.se.lab01.task1;


public class Main {

    public static void main(String[] args) throws InterruptedException {

        boolean sequential = args.length > 0 && args[0].equals("--sequential");

        Thread apollo  = new Countdown("apollo");
        Thread gemini  = new Thread(new CountdownTask(), "gemini");
        Thread mercury = new Thread(() -> new CountdownTask().run(), "mercury");   
        Thread artemis = Thread.ofVirtual().name("artemis").unstarted(new CountdownTask());

        Thread[] threads = {apollo, gemini, mercury, artemis};

        for (Thread thread : threads) {
            if (sequential) {
                thread.run();
            } else {
                thread.start();
            }
        }

        System.out.println("Mission control: all launched");

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Mission control: all landed");

        for (Thread thread : threads) {
            System.out.println(
                    thread.getName()
                    + " virtual=" + thread.isVirtual()
                    + " state=" + thread.getState()
            );
        }
    }
}