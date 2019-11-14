package utils;

import java.io.IOException;

public class ShutdownHandler extends Thread {
    @Override
    public void run() {
        try {
            Logger.getInstance().writeLogToFile();
        } catch (IOException e) {
            System.out.println("Failed to write log to file.");
        }
        super.run();
    }
}