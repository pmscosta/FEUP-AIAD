import agents.GreedyVillage;
import agents.PassiveVillage;
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
import utils.Resource;
import utils.ShutdownHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws StaleProxyException, IOException, ParserConfigurationException, SAXException {
        Runtime rt = Runtime.instance();
        java.lang.Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

        Profile profile = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(profile);

        for (Village village : parseVillagesFile("villages.xml")) {
            mainContainer.acceptNewAgent(village.getVillageName(), village).start();
        }
    }

    private static final LinkedList<Village> parseVillagesFile(String file_path) throws IOException, ParserConfigurationException, SAXException {
        LinkedList<Village> villages = new LinkedList<>();

        File inputFile = new File(file_path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        NodeList village_nodes = doc.getElementsByTagName("village");

        for (int i = 0; i < village_nodes.getLength(); i++) {
            Node village_node = village_nodes.item(i);

            if (village_node.getNodeType() == Node.ELEMENT_NODE) {
                Element village_element = (Element) village_node;

                String name = village_element.getElementsByTagName("name").item(0).getTextContent();
                String type = village_element.getElementsByTagName("type").item(0).getTextContent();
                int consumption_rate = Integer.parseInt(
                        village_element.getElementsByTagName("consumption_rate").item(0).getTextContent()
                );

                List<Resource> production_resources = new LinkedList<>();
                production_resources.add(new Resource(Resource.ResourceType.CLAY, Integer.parseInt(village_element.getElementsByTagName("clay_production_rate").item(0).getTextContent())));
                production_resources.add(new Resource(Resource.ResourceType.FOOD,Integer.parseInt(village_element.getElementsByTagName("food_production_rate").item(0).getTextContent())));
                production_resources.add(new Resource(Resource.ResourceType.STONE, Integer.parseInt(village_element.getElementsByTagName("stone_production_rate").item(0).getTextContent())));
                production_resources.add(new Resource(Resource.ResourceType.WOOD, Integer.parseInt(village_element.getElementsByTagName("wood_production_rate").item(0).getTextContent())));

                if (type.equals("Passive")) {
                    villages.add(new PassiveVillage(name, consumption_rate, production_resources));
                } else if (type.equals("Greedy")) {
                    villages.add(new GreedyVillage(name, consumption_rate, production_resources));
                }
            }
        }

        return villages;
    }
}

