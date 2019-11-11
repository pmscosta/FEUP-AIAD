package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ResourceLogger {

    private static ResourceLogger instance;
    List<String> log = new LinkedList<>();

    private ResourceLogger() {
    }

    public static ResourceLogger getInstance() {
        if (instance == null) {
            instance = new ResourceLogger();
        }
        return instance;
    }

    public synchronized void add(String entry) {
        log.add(entry);
    }

    public void writeLogToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("resource_log.txt"));

        for (String entry : log) {
            writer.write(entry);
        }

        writer.close();
    }
}
