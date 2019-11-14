package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Logger {

    private static Logger instance;
    List<String> log = new LinkedList<>();

    private Logger() {
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public synchronized void add(String entry) {
        log.add(entry);
    }

    public void writeLogToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"));

        for (String entry : log) {
            writer.write(entry);
        }

        writer.close();
    }
}
