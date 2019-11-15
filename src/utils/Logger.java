package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    public synchronized void logVillageStatus(int tick_num, String village_name, ConcurrentHashMap<Resource.ResourceType, Resource> resources) {
        log.add(String.format(
                "[Village Status] %d %s %d %d %d %d [Resources Sum = %d]\n",
                tick_num,
                village_name,
                resources.get(Resource.ResourceType.STONE).getAmount(),
                resources.get(Resource.ResourceType.WOOD).getAmount(),
                resources.get(Resource.ResourceType.FOOD).getAmount(),
                resources.get(Resource.ResourceType.CLAY).getAmount(),
                resources.get(Resource.ResourceType.STONE).getAmount()+
                        resources.get(Resource.ResourceType.WOOD).getAmount()+
                        resources.get(Resource.ResourceType.FOOD).getAmount()+
                        resources.get(Resource.ResourceType.CLAY).getAmount()
        ));
    }

    public void writeLogToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"));

        for (String entry : log) {
            writer.write(entry);
        }

        writer.close();
    }
}
