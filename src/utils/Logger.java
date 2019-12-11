package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Logger {

    private static Logger instance;
    List<String> log = new LinkedList<>();
    HashMap<String, ArrayList<Integer>> resourcesEvolution = new HashMap<>();
    private int total_ticks;

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

        total_ticks = tick_num;

        int sum = resources.get(Resource.ResourceType.STONE).getAmount() +
                resources.get(Resource.ResourceType.WOOD).getAmount() +
                resources.get(Resource.ResourceType.FOOD).getAmount() +
                resources.get(Resource.ResourceType.CLAY).getAmount();

        log.add(String.format(
                "[Village Status] %d %s %d %d %d %d [Resources Sum = %d]\n",
                tick_num,
                village_name,
                resources.get(Resource.ResourceType.STONE).getAmount(),
                resources.get(Resource.ResourceType.WOOD).getAmount(),
                resources.get(Resource.ResourceType.FOOD).getAmount(),
                resources.get(Resource.ResourceType.CLAY).getAmount(),
                sum
        ));


        if (resourcesEvolution.containsKey(village_name)) {
            resourcesEvolution.get(village_name).add(sum);
        } else {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(sum);
            resourcesEvolution.put(village_name, list);
        }

    }

    public void writeLogToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"));

        for (String entry : log) {
            writer.write(entry);
        }

        // BufferedWriter csvWriter = new BufferedWriter(new FileWriter("resources_evo.csv"));

        BufferedWriter rapidMinerCsv = new BufferedWriter(new FileWriter("rapidminer_output_data.csv", true));

        // csvWriter.write("ticks, ");
        // for (int i = 0; i <= total_ticks; i++) {
        //     csvWriter.write(i + ", ");
        // }

        // csvWriter.write("\n");
        // for (String village : resourcesEvolution.keySet()) {
        //     csvWriter.write(village + ", ");
        //     for (Integer i : resourcesEvolution.get(village)) {
        //         csvWriter.write(i + ", ");
        //     }
        // }
        // csvWriter.write("\n");

        String rapid_miner_string = String.format("%s,%d\n", IndependentVariables.getInstance().exportString(), Economy.getLast());

        rapidMinerCsv.write(rapid_miner_string);
        writer.close();
        // csvWriter.close();
        rapidMinerCsv.close();
    }
}
