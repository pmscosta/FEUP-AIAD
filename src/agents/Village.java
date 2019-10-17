package agents;

import behaviour.ConsumingBehaviour;
import behaviour.Handler;
import behaviour.passive.PassiveBehaviour;
import behaviour.ProducingBehaviour;
import behaviour.passive.PassiveHandler;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import protocol.AcceptTradeMessage;
import protocol.StartTradeMessage;
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
    private final VillageType type;
    private HashMap<ResourceType, Resource> resources = new HashMap<ResourceType, Resource>() {{
        put(ResourceType.CLAY, new Resource(ResourceType.CLAY));
        put(ResourceType.FOOD, new Resource(ResourceType.FOOD));
        put(ResourceType.STONE, new Resource(ResourceType.STONE));
        put(ResourceType.WOOD, new Resource(ResourceType.WOOD));
    }};

    private Handler tradeHandler;

    public Village(String name) {
        this(name, DEFAULT_RESOURCE_CONSUMPTION);
    }

    public Village(String name, int resource_consumption) {
        this(name, VillageType.PASSIVE, resource_consumption, randomizeProduction(resource_consumption));
    }

    public Village(String name, VillageType type, int resource_consumption, List<Resource> production_resources) {
        this.name = name;
        this.resource_consumption = resource_consumption;
        this.production_resources = production_resources;
        this.type = type;
        this.createHandler();
    }

    public int getResourceConsumption() {
        return resource_consumption;
    }

    public void setup() {
        addBehaviour(new PassiveBehaviour(this));
        addBehaviour(new ProducingBehaviour(this));
        addBehaviour(new ConsumingBehaviour(this));
        addBehaviour(new TradeListenerBehaviour());
    }

    private void createHandler() {

        switch (this.type) {
            case PASSIVE:
            default:
                this.tradeHandler = new PassiveHandler(this);
                break;
            case AGGRESSIVE:
                break;
        }

    }

    class TradeListenerBehaviour extends CyclicBehaviour {

        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {

                try {
                    dispatcher(msg);
                } catch (IOException | UnreadableException e) {
                    e.printStackTrace();
                }


            } else {
                block();
            }
        }
    }

    private void dispatcher(ACLMessage msg) throws IOException, UnreadableException {

        switch (msg.getPerformative()) {
            case ACLMessage.PROPOSE:
                this.tradeHandler.handleReceivedTrade(msg);
                break;
            case ACLMessage.ACCEPT_PROPOSAL:
                this.tradeHandler.handleConfirmedTrade(msg);
                break;
            default:
                System.out.println("UNRECOGNIZED MESSAGE");
                break;
        }

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

        try {
            StartTradeMessage msg = new StartTradeMessage(trade);
            for (AMSAgentDescription e : this.findVillages()) {
                if (!e.getName().equals(this.getAID())) {
                    msg.addReceiver(e.getName());
                }
            }
            send(msg);
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
