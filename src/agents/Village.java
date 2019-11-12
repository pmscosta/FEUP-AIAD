package agents;

import exceptions.NotEnoughResources;
import utils.Resource;
import utils.Resource.ResourceType;
import utils.ResourceRandomizer;
import utils.Trade;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Printer.safePrintf;

public abstract class Village extends BaseAgent {

    private static final int DEFAULT_RESOURCE_CONSUMPTION = 5;

    private final String name;
    private final int resource_consumption;
    private final List<Resource> production_resources;
    ConcurrentHashMap<ResourceType, Resource> resources = new ConcurrentHashMap<>() {{
        put(ResourceType.CLAY, new Resource(ResourceType.CLAY));
        put(ResourceType.FOOD, new Resource(ResourceType.FOOD));
        put(ResourceType.STONE, new Resource(ResourceType.STONE));
        put(ResourceType.WOOD, new Resource(ResourceType.WOOD));
    }};

    ConcurrentHashMap<ResourceType, Integer> openTrades = new ConcurrentHashMap<>();

    public void printOpenTrades(){
        openTrades.entrySet().forEach(entry->{
            safePrintf("%s : %d", entry.getKey() ,entry.getValue());
        });
    }

    Village(String name) {
        this(name, DEFAULT_RESOURCE_CONSUMPTION);
    }

    Village(String name, int resource_consumption) {
        this(name, resource_consumption, ResourceRandomizer.randomizeProduction(resource_consumption));
    }

    Village(String name, int resource_consumption, List<Resource> production_resources) {
        this.name = name;
        this.resource_consumption = resource_consumption;
        this.production_resources = production_resources;
    }

     public void accountForNewTrade(Resource r){

        int new_quantity = r.getAmount();
        ResourceType type = r.getType();
        int curr_quantity = 0;

        if(this.openTrades.containsKey(r.getType())){
            curr_quantity += this.openTrades.get(type);
        }

        this.openTrades.put(type, curr_quantity + new_quantity);
    }

    public int getResourceQuantityInOpenTrades(ResourceType type) {
        return this.openTrades.get(type);
    }

    public void closeOpenTrade(Resource r){
        int curr_quantity = this.openTrades.get(r.getType());
        this.openTrades.put(r.getType(), curr_quantity - r.getAmount());
    }


    public boolean canPromiseTrade(Resource r){
        int lockedQuantity = 0;

        if(this.openTrades.containsKey(r.getType())) {
            lockedQuantity += getResourceQuantityInOpenTrades(r.getType());
        }

        int totalLockedQuantity = lockedQuantity + r.getAmount();

        return this.resources.get(r.getType()).getAmount() - totalLockedQuantity > 0;
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

    public ConcurrentHashMap<ResourceType, Resource> getResources() {
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

        /*
         * free the locked resource in the open trades
         */
        this.closeOpenTrade(is_proposer ? t.getOffer() : t.getRequest());

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
    public boolean canAcceptTrade(Trade t){
        return canPromiseTrade(t.getRequest());
    }

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

    public abstract int selectBestTrade(List<Trade> trades);

    public abstract Trade createProposingTrade(Resource r);


    public void proposeTrade(Resource r) {
        Trade t = createProposingTrade(r);

        if(!this.canPromiseTrade(t.getOffer())){
            return;
        }

        broadcastTrade(t);
    }
}
