import agents.*;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import utils.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private static ContainerController mainContainer;
    private static Document doc;


    private static final String FILE_NAME = "input.xml";
    private static final String[] village_types = { "passive", "greedy", "smart" };

    public static void main(String[] args) throws StaleProxyException, IOException, ParserConfigurationException, SAXException {
        java.lang.Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

        initContainer();
        initVillages(args);
        initAttacker(Integer.parseInt(args[21]));
        initEconomy();


        TerminationScheduler.scheduleTermination(mainContainer);

        // Just getting the csv file if it is given
        if (args.length >= 23) {
            Logger.rapidminer_csv_file = args[22];
        }
    }

    private static final void initEconomy() throws StaleProxyException {
        mainContainer.acceptNewAgent("economy", new EconomyAgent()).start();
    }

    private static final void initContainer() {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        mainContainer = rt.createMainContainer(profile);
    }

    private static final void initDoc() throws ParserConfigurationException, IOException, SAXException {
        File inputFile = new File(FILE_NAME);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
    }

    private static final void initVillages(String[] args) throws StaleProxyException {
        ArrayList<Integer> passiveInfo = new ArrayList<Integer>();
        ArrayList<Integer> greedyInfo = new ArrayList<Integer>();
        ArrayList<Integer> smartInfo = new ArrayList<Integer>();

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                switch (i){
                    case 0:
                        passiveInfo.add(Integer.parseInt(args[j+i*7]));
                        break;
                    case 1:
                        greedyInfo.add(Integer.parseInt(args[j+i*7]));
                        break;
                    case 2:
                        smartInfo.add(Integer.parseInt(args[j+i*7]));
                        break;
                        default:
                            break;
                }
            }
        }

        for (String village_type : village_types) {
            switch (village_type){
                case "passive":
                    System.out.println("Initiating Passive Villages");
                    initVillage(village_type, passiveInfo);
                    break;
                case "greedy":
                    System.out.println("Initiating Greedy Villages");
                    initVillage(village_type, greedyInfo);
                    break;
                case "smart":
                    System.out.println("Initiating Smart Villages");
                    initVillage(village_type, smartInfo);
                    break;
                    default:
                        break;
            }
        }
    }

    private static final void initVillage(String village_type, ArrayList<Integer> values) throws StaleProxyException {
        int quantity = values.get(0);
        for (int i = 1; i <= quantity; ++i) {
            Village village = createVillage(values, village_type, i);
            Printer.safePrintf("Initiating Village '%s'", village.getVillageName());
            Logger.getInstance().add(
                    String.format("[Village Creation] Initiating Village %s\n", village.getVillageName())
            );
            mainContainer.acceptNewAgent(village.getVillageName(), village).start();
        }

        switch (village_type) {
            case "passive":
                IndependentVariables.getInstance().passive_num_villages = quantity;
                break;
            case "greedy":
                IndependentVariables.getInstance().greedy_num_villages = quantity;
                break;
            case "smart":
                IndependentVariables.getInstance().smart_num_villages = quantity;
                break;
        }
    }

    private static final void initAttacker(int val) throws StaleProxyException {
        // Add attacker
        Printer.safePrintf("\nInitiating Attacker\n");
        Logger.getInstance().add("[Attacker Creation] Initiating Attacker\n\n");
        mainContainer.acceptNewAgent("attacker", new Attacker(val)).start();
        IndependentVariables.getInstance().attacker_attack_percentage = val;
    }

    private static final Village createVillage(ArrayList<Integer> values, String village_type, int id) {
        int initial_resource_amounts = values.get(1);
        int resource_consumption_rate = values.get(2);

        List<Resource> production_resources = new LinkedList<>();
        production_resources.add(new Resource(Resource.ResourceType.CLAY,values.get(3)));
        production_resources.add(new Resource(Resource.ResourceType.FOOD, values.get(4)));
        production_resources.add(new Resource(Resource.ResourceType.STONE, values.get(5)));
        production_resources.add(new Resource(Resource.ResourceType.WOOD, values.get(6)));

        switch (village_type) {
            case "passive":
                IndependentVariables.getInstance().passive_init_resources = initial_resource_amounts;
                IndependentVariables.getInstance().passive_resource_consumption = resource_consumption_rate;
                IndependentVariables.getInstance().passive_clay_production = production_resources.get(0).getAmount();
                IndependentVariables.getInstance().passive_food_production = production_resources.get(1).getAmount();
                IndependentVariables.getInstance().passive_stone_production = production_resources.get(2).getAmount();
                IndependentVariables.getInstance().passive_wood_production = production_resources.get(3).getAmount();
                return new PassiveVillage(
                        String.format("%s%d", village_type, id),
                        initial_resource_amounts,
                        resource_consumption_rate,
                        production_resources);
            case "greedy":
                IndependentVariables.getInstance().greedy_init_resources = initial_resource_amounts;
                IndependentVariables.getInstance().greedy_resource_consumption = resource_consumption_rate;
                IndependentVariables.getInstance().greedy_clay_production = production_resources.get(0).getAmount();
                IndependentVariables.getInstance().greedy_food_production = production_resources.get(1).getAmount();
                IndependentVariables.getInstance().greedy_stone_production = production_resources.get(2).getAmount();
                IndependentVariables.getInstance().greedy_wood_production = production_resources.get(3).getAmount();
                return new GreedyVillage(
                        String.format("%s%d", village_type, id),
                        initial_resource_amounts,
                        resource_consumption_rate,
                        production_resources);
            case "smart":
                IndependentVariables.getInstance().smart_init_resources = initial_resource_amounts;
                IndependentVariables.getInstance().smart_resource_consumption = resource_consumption_rate;
                IndependentVariables.getInstance().smart_clay_production = production_resources.get(0).getAmount();
                IndependentVariables.getInstance().smart_food_production = production_resources.get(1).getAmount();
                IndependentVariables.getInstance().smart_stone_production = production_resources.get(2).getAmount();
                IndependentVariables.getInstance().smart_wood_production = production_resources.get(3).getAmount();
                return new SmartVillage(
                        String.format("%s%d", village_type, id),
                        initial_resource_amounts,
                        resource_consumption_rate,
                        production_resources);
            default:
                return null;
        }
    }

}
