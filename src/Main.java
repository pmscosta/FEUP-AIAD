import agents.Village;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Resource;
import utils.Resource.ResourceType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws StaleProxyException, IOException {
        Runtime rt = Runtime.instance();

        Profile profile = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(profile);

        for (Village village : parseVillagesFile("villages.txt")) {
            mainContainer.acceptNewAgent(village.getVillageName(), village).start();
        }
    }

    private static final LinkedList<Village> parseVillagesFile(String file_path) throws IOException {
        LinkedList<Village> villages = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String name = line;
                String type = br.readLine();
                int consumption_rate = Integer.parseInt(br.readLine());

                List<Resource> production_resources = new LinkedList<>();
                production_resources.add(new Resource(ResourceType.CLAY, Integer.parseInt(br.readLine())));
                production_resources.add(new Resource(ResourceType.FOOD, Integer.parseInt(br.readLine())));
                production_resources.add(new Resource(ResourceType.STONE, Integer.parseInt(br.readLine())));
                production_resources.add(new Resource(ResourceType.WOOD, Integer.parseInt(br.readLine())));

                villages.add(new Village(name, consumption_rate, production_resources));
                br.readLine();
            }
        }

        return villages;
    }
}

