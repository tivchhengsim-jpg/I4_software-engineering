package edu.itc.se.lab01.task5;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.StringJoiner;

public class DeadlockWatchdog {

    public static Thread start() {
        Thread watchdog = new Thread(() -> {
            ThreadMXBean bean =
                    ManagementFactory.getThreadMXBean();

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1_000);

                    long[] ids = bean.findDeadlockedThreads();

                    if (ids != null) {
                        StringJoiner names = new StringJoiner(", ");

                        for (ThreadInfo info : bean.getThreadInfo(ids)) {
                            if (info != null) {
                                names.add(info.getThreadName());
                            }
                        }

                        System.out.println(
                                "[watchdog] DEADLOCK detected between: "
                                        + names
                        );
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "deadlock-watchdog");

        watchdog.setDaemon(true);
        watchdog.start();

        return watchdog;
    }
}