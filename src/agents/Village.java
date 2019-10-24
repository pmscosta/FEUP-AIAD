package agents;

import behaviour.*;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import protocol.ACLObjectMessage;
import utils.Resource;
import utils.Resource.ResourceType;
import utils.Trade;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Village extends Agent {

    private static final int DEFAULT_RESOURCE_CONSUMPTION = 5;

    private final String name;
    private final int resource_consumption;
    private final List<Resource> production_resources;
    private HashMap<ResourceType, Resource> resources = new HashMap<ResourceType, Resource>() {{
        put(ResourceType.CLAY, new Resource(ResourceType.CLAY));
        put(ResourceType.FOOD, new Resource(ResourceType.FOOD));
        put(ResourceType.STONE, new Resource(ResourceType.STONE));
        put(ResourceType.WOOD, new Resource(ResourceType.WOOD));
    }};

    public Village(String name) {
        this(name, DEFAULT_RESOURCE_CONSUMPTION);
    }

    public Village(String name, int resource_consumption) {
        this(name, resource_consumption, randomizeProduction(resource_consumption));
    }

    public Village(String name, int resource_consumption, List<Resource> production_resources) {
        this.name = name;
        this.resource_consumption = resource_consumption;
        this.production_resources = production_resources;
    }

    public int getResourceConsumption() {
        return resource_consumption;
    }

    public void setup() {
        addBehaviour(new PassiveBehaviour(this));
        addBehaviour(new ProducingBehaviour(this));
        addBehaviour(new ConsumingBehaviour(this));

        // TODO: Not match all :upside_down_smile:
        addBehaviour(new HandleTradeBehaviour(this, MessageTemplate.MatchAll()));
    }

    public Resource getMostDepletedResource() {
        return this.getResources().values().stream().min(Comparator.comparing(Resource::getAmount)).orElse(null);
    }

    public Resource getMostAbundantResource() {
        return this.getResources().values().stream().max(Comparator.comparing(Resource::getAmount)).orElse(null);
    }

    public HashMap<ResourceType, Resource> getResources() {
        return this.resources;
    }

    public List<Resource> getProductionResources() {
        return production_resources;
    }

    public AMSAgentDescription[] findVillages() {

        AMSAgentDescription[] agents = null;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e) {
            System.out.println("Problem searching AMS: " + e);
            e.printStackTrace();
        }

        return agents;
    }

    public void broadcastTrade(Trade trade) {
        System.out.println("Sending trade ...");

        try {
            ACLObjectMessage msg = new ACLObjectMessage(ACLMessage.CFP, trade);
            this.addBehaviour(new InitTradeBehaviour(this, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final int getRandomResourceProductionRate(int resource_consumption) {
        return ThreadLocalRandom.current().nextInt(resource_consumption + 5, resource_consumption + 20);
    }

    private static final List<Resource> randomizeProduction(int resource_consumption) {
        List<ResourceType> resource_types = Arrays.asList(ResourceType.values());
        int num_resources = ThreadLocalRandom.current().nextInt(1, resource_types.size());
        Collections.shuffle(resource_types);
        return resource_types
                .subList(0, num_resources)
                .stream()
                .map(type -> new Resource(type, getRandomResourceProductionRate(resource_consumption)))
                .collect(Collectors.toList());
    }

    public String getVillageName() {
        return this.name;
    }
}
