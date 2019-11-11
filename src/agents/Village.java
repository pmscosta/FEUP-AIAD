package agents;

import exceptions.NotEnoughResources;
import utils.Resource;
import utils.Resource.ResourceType;
import utils.ResourceRandomizer;
import utils.Trade;

import java.util.*;

import static utils.Printer.safePrintf;

public abstract class Village extends BaseAgent {

    private static final int DEFAULT_RESOURCE_CONSUMPTION = 5;

    private final String name;
    private final int resource_consumption;
    private final List<Resource> production_resources;
    protected HashMap<ResourceType, Resource> resources = new HashMap<ResourceType, Resource>() {{
        put(ResourceType.CLAY, new Resource(ResourceType.CLAY));
        put(ResourceType.FOOD, new Resource(ResourceType.FOOD));
        put(ResourceType.STONE, new Resource(ResourceType.STONE));
        put(ResourceType.WOOD, new Resource(ResourceType.WOOD));
    }};

    public Village(String name) {
        this(name, DEFAULT_RESOURCE_CONSUMPTION);
    }

    public Village(String name, int resource_consumption) {
        this(name, resource_consumption, ResourceRandomizer.randomizeProduction(resource_consumption));
    }

    public Village(String name, int resource_consumption, List<Resource> production_resources) {
        this.name = name;
        this.resource_consumption = resource_consumption;
        this.production_resources = production_resources;
    }

    public int getResourceConsumption() {
        return resource_consumption;
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

    public String getVillageName() {
        return this.name;
    }

    public void applyTrade(Trade t, boolean is_proposer) {
        Resource request = is_proposer ? t.getRequest() : t.getOffer();
        Resource offer = is_proposer ? t.getOffer() : t.getRequest();

        try {
            this.resources.get(t.getRequest().getType()).produceAmount(request.getAmount());
            this.resources.get(t.getOffer().getType()).consumeAmount(offer.getAmount());
        } catch (NotEnoughResources e) {
            // Never happens unless there are concurrency problems since canAcceptTrade has returned true before
            e.printStackTrace();
        }

        safePrintf("%s: As a %s, just did this trade:", getVillageName(), is_proposer ? "proposer" : "responder");
        safePrintf(t.toString());
    }

    /**
     * Sets up the Villages behaviours
     */
    public abstract void setup();

    /**
     * Verifies if trade can be accepted, according to the village's standards
     * @param t
     * @return true if trade can be accepted, false otherwise
     */
    public abstract boolean canAcceptTrade(Trade t);

    /**
     * Decides whether the given trade should be accepted or not. Override in subclasses to change the passive behaviour
     *
     * @param t The trade to decide acceptance of
     * @return true if the trade should be accepted, false otherwise
     */
    public abstract boolean wantToAcceptTrade(Trade t);

    /**
     * Verifies if a trade should be performed base on the village's current status
     * @param r
     * @return true if should be performed, false otherwise
     */
    public abstract boolean shouldProposeTrade(Resource r);

    /**
     * Perform a trade on the given resource
     * @param r
     */
    public abstract void performTrade(Resource r);

}
