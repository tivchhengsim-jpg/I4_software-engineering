package edu.itc.se.lab01.task4;

import java.io.IOException;
import java.util.concurrent.Callable;

public class FakeDownload implements Callable<String> {
    private final String url;
    private final int downloadNumber;

    public FakeDownload(String url, int downloadNumber) {
        this.url = url;
        this.downloadNumber = downloadNumber;
    }

    @Override
    public String call() throws IOException, InterruptedException {
       
        if (downloadNumber == 1) {
            Thread.sleep(5_000);
        } else {
            Thread.sleep(200);
        }


        if (downloadNumber % 50 == 0) {
            throw new IOException("boom");
        }

        // Simulate downloaded content.
        String content = "Downloaded content from " + url;

        return url + " -> " + content.length();
    }
}