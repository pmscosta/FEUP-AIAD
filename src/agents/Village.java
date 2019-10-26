package agents;

import behaviour.*;
import exceptions.NotEnoughResources;
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

import static utils.Printer.safePrintf;

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
        MessageTemplate mt =
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.CFP),
                        MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL)
                );

        addBehaviour(new HandleTradeBehaviour(this, mt));
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

    public AMSAgentDescription[] findOtherVillages() {

        AMSAgentDescription[] agents = null;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults((long) -1);
            agents = Arrays.stream(AMSService.search(this, new AMSAgentDescription(), c))
                    .filter(agentDescription -> {
                        return !agentDescription.getName().equals(this.getAID())
                                &&
                                !agentDescription.getName().getLocalName().equals("ams")
                                &&
                                !agentDescription.getName().getLocalName().equals("df");
                    })
                    .toArray(AMSAgentDescription[]::new);
        } catch (Exception e) {
            safePrintf("Problem searching AMS: " + e);
            e.printStackTrace();
        }

        return agents;
    }

    public void broadcastTrade(Trade trade) {
        safePrintf(this.getLocalName() + " : Sending trade: %s", trade);

        try {
            ACLObjectMessage msg = new ACLObjectMessage(ACLMessage.CFP, trade);
            for (AMSAgentDescription ad : this.findOtherVillages()) {
                msg.addReceiver(ad.getName());
            }

            this.addBehaviour(new InitTradeBehaviour(this, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decides whether the given trade should be accepted or not. Override in subclasses to change the passive behaviour
     *
     * @param t The trade to decide acceptance of
     * @return true if the trade should be accepted, false otherwise
     */
    public boolean wantToAcceptTrade(Trade t) {
        return true;
    }

    public final boolean canAcceptTrade(Trade t) {
        int have = this.resources.get(t.getRequest().getType()).getAmount();
        int requested = t.getRequest().getAmount();
        // TODO: Use threshold (did not use yet since it is necessary to migrate things via refactoring)
        return have - requested > 0;
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

    // These two probably can be refactored but for the sake of finishing the MVP left them like this (basically they
    // only flip the interpretation of offer and request)

    public void doProposedTrade(Trade t) {
        // I received what I requested
        this.resources.get(t.getRequest().getType()).produceAmount(t.getRequest().getAmount());
        try {
            // And sent what I offered
            this.resources.get(t.getOffer().getType()).consumeAmount(t.getOffer().getAmount());
        } catch (NotEnoughResources e) {
            // Never happens unless there are concurrency problems since canAcceptTrade has returned true before
            e.printStackTrace();
        }

        safePrintf(this.getLocalName() + " : As a proposer, just did this trade: " + t);
    }

    public void doAcceptedTrade(Trade t) {
        // I received what I was offered
        this.resources.get(t.getOffer().getType()).produceAmount(t.getOffer().getAmount());
        try {
            // And sent what I was requested
            this.resources.get(t.getRequest().getType()).consumeAmount(t.getRequest().getAmount());
        } catch (NotEnoughResources e) {
            // Never happens unless there are concurrency problems since canAcceptTrade has returned true before
            e.printStackTrace();
        }

        safePrintf(this.getLocalName() + " : As a responder, just did this trade: " + t);
    }
}
