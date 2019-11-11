package agents;

import exceptions.NotEnoughResources;
import utils.Resource;
import utils.Resource.ResourceType;
import utils.Trade;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static utils.Printer.safePrintf;

public abstract class Village extends BaseAgent {

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
    public abstract boolean shouldPerformTrade(Resource r);

    /**
     * Perform a trade on the given resource
     * @param r
     */
    public abstract void performTrade(Resource r);

}
