package agents;

import behaviour.ConsumingBehaviour;
import behaviour.PassiveBehaviour;
import behaviour.ProducingBehaviour;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import utils.Resource.ResourceType;

import utils.Resource;
import utils.Trade;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Village extends Agent {

    private static final int DEFAULT_RESOURCE_CONSUMPTION = 5;

    private final int resource_consumption;
    private List<Resource> production_resources;
    private HashMap<ResourceType, Resource> resources = new HashMap<ResourceType, Resource>() {{
        put(ResourceType.WOOD, new Resource(ResourceType.WOOD));
        put(ResourceType.STONE, new Resource(ResourceType.STONE));
        put(ResourceType.FOOD, new Resource(ResourceType.FOOD));
        put(ResourceType.CLAY, new Resource(ResourceType.CLAY));
    }};

    public Village() {
        this(DEFAULT_RESOURCE_CONSUMPTION);
    }

    public Village(int resource_consumption) {
        this.resource_consumption = resource_consumption;
        this.randomizeProduction();
    }

    public Village(int resource_consumption, List<Resource> production_resources) {
        this(resource_consumption);
        this.production_resources = production_resources;
    }

    public int getResourceConsumption() {
        return resource_consumption;
    }

    public void setup() {
        addBehaviour(new PassiveBehaviour(this));
        addBehaviour(new ProducingBehaviour(this));
        addBehaviour(new ConsumingBehaviour(this));
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

    }

    private int getRandomResourceProductionRate() {
        return ThreadLocalRandom.current().nextInt(this.resource_consumption + 5, this.resource_consumption + 20);
    }

    private void randomizeProduction() {
        List<ResourceType> resource_types = Arrays.asList(ResourceType.values());
        int num_resources = ThreadLocalRandom.current().nextInt(1, resource_types.size());
        Collections.shuffle(resource_types);
        this.production_resources = resource_types
                .subList(0, num_resources)
                .stream()
                .map(type -> new Resource(type, this.getRandomResourceProductionRate()))
                .collect(Collectors.toList());
    }
}
