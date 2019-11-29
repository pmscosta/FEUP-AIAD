import agents.Attacker;
import agents.GreedyVillage;
import agents.PassiveVillage;
import agents.SmartVillage;
import agents.Village;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
        initDoc();
        initVillages();
        initAttacker();

        TerminationScheduler.scheduleTermination(mainContainer);
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

    private static final void initVillages() throws StaleProxyException {
        for (String village_type : village_types) {
            initVillage(village_type);
        }
    }

    private static final void initVillage(String village_type) throws StaleProxyException {
        Element village_element = (Element) doc.getElementsByTagName(String.format("%s_village", village_type)).item(0);
        int quantity = getElementValue(village_element, "quantity");
        for (int i = 1; i <= quantity; ++i) {
            Village village = createVillage(village_element, village_type, i);
            Printer.safePrintf("Initiating Village '%s'", village.getVillageName());
            Logger.getInstance().add(
                    String.format("[Village Creation] Initiating Village %s\n", village.getVillageName())
            );
            mainContainer.acceptNewAgent(village.getVillageName(), village).start();
        }
    }

    private static final void initAttacker() throws StaleProxyException {
        Element attacker_element = (Element) doc.getElementsByTagName("attacker").item(0);
        int attacked_resources_percentage = getElementValue(attacker_element, "attacked_resources_percentage");

        // Add attacker
        Printer.safePrintf("\nInitiating Attacker\n");
        Logger.getInstance().add("[Attacker Creation] Initiating Attacker\n\n");
        mainContainer.acceptNewAgent("attacker", new Attacker(attacked_resources_percentage)).start();
    }

    private static final Village createVillage(Element village_xml_element, String village_type, int id) {
        int initial_resource_amounts = getElementValue(village_xml_element, "initial_resource_amounts");
        int resource_consumption_rate = getElementValue(village_xml_element, "resource_consumption_rate");

        List<Resource> production_resources = new LinkedList<>();
        production_resources.add(new Resource(Resource.ResourceType.CLAY, Integer.parseInt(village_xml_element.getElementsByTagName("clay_production_rate").item(0).getTextContent())));
        production_resources.add(new Resource(Resource.ResourceType.FOOD, Integer.parseInt(village_xml_element.getElementsByTagName("food_production_rate").item(0).getTextContent())));
        production_resources.add(new Resource(Resource.ResourceType.STONE, Integer.parseInt(village_xml_element.getElementsByTagName("stone_production_rate").item(0).getTextContent())));
        production_resources.add(new Resource(Resource.ResourceType.WOOD, Integer.parseInt(village_xml_element.getElementsByTagName("wood_production_rate").item(0).getTextContent())));

        switch (village_type) {
            case "passive":
                return new PassiveVillage(
                        String.format("%s%d", village_type, id),
                        initial_resource_amounts,
                        resource_consumption_rate,
                        production_resources);
            case "greedy":
                return new GreedyVillage(
                        String.format("%s%d", village_type, id),
                        initial_resource_amounts,
                        resource_consumption_rate,
                        production_resources);
            case "smart":
                return new SmartVillage(
                        String.format("%s%d", village_type, id),
                        initial_resource_amounts,
                        resource_consumption_rate,
                        production_resources);
            default:
                return null;
        }
    }

    private static final int getElementValue(Element element, String name) {
        return Integer.parseInt(element.getElementsByTagName(name).item(0).getTextContent());
    }
}

