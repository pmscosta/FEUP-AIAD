package agents;

import behaviour.HandleCounterProposalAnswerBehaviour;
import behaviour.HandleProposalBehaviour;
import behaviour.LifeCycleBehaviour;
import exceptions.NotEnoughResources;
import utils.Resource;
import utils.Resource.ResourceType;
import utils.ResourceLogger;
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
    ConcurrentHashMap<ResourceType, Resource> resources = new ConcurrentHashMap<ResourceType, Resource>() {{ // Cannot have anonymous inner class in order to be compliant with Java 8
        put(ResourceType.CLAY, new Resource(ResourceType.CLAY));
        put(ResourceType.FOOD, new Resource(ResourceType.FOOD));
        put(ResourceType.STONE, new Resource(ResourceType.STONE));
        put(ResourceType.WOOD, new Resource(ResourceType.WOOD));
    }};

    ConcurrentHashMap<ResourceType, Integer> openTrades = new ConcurrentHashMap<>();
    public int tick_num = 0;

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

    /**
     * Verifies if trade can be accepted, according to the village's standards
     * @param t
     * @return true if trade can be accepted, false otherwise
     */
    public boolean canAcceptTrade(Trade t){
        return canPromiseTrade(t.getRequest());
    }

    public void applyTrade(Trade t, boolean is_proposer) {
        Resource request = is_proposer ? t.getRequest() : t.getOffer();
        Resource offer = is_proposer ? t.getOffer() : t.getRequest();

        /*
         * free the locked resource in the open trades
         */
        this.closeOpenTrade(is_proposer ? t.getOffer() : t.getRequest());

        int a1 = this.getResources().get(Resource.ResourceType.STONE).getAmount();
        int a2 = this.getResources().get(Resource.ResourceType.WOOD).getAmount();
        int a3 = this.getResources().get(Resource.ResourceType.FOOD).getAmount();
        int a4 = this.getResources().get(Resource.ResourceType.CLAY).getAmount();

        try {
            this.resources.get(request.getType()).produceAmount(request.getAmount());
            this.resources.get(offer.getType()).consumeAmount(offer.getAmount());
        } catch (NotEnoughResources e) {
            // Never happens unless there are concurrency problems since canAcceptTrade has returned true before
            e.printStackTrace();
        }

        int b1 = this.getResources().get(Resource.ResourceType.STONE).getAmount();
        int b2 = this.getResources().get(Resource.ResourceType.WOOD).getAmount();
        int b3 = this.getResources().get(Resource.ResourceType.FOOD).getAmount();
        int b4 = this.getResources().get(Resource.ResourceType.CLAY).getAmount();

        ResourceLogger.getInstance().add(String.format(
                "%d %s (%d) (%d) (%d) (%d)\n",
                this.tick_num,
                    this.getVillageName(),
                    b1-a1,
                    b2-a2,
                    b3-a3,
                    b4-a4
                ));

        safePrintf("%s: As a %s, just did this trade:", getVillageName(), is_proposer ? "proposer" : "responder");
        safePrintf(t.toString());
    }

    public void proposeTrade(Resource r) {
        Trade t = createProposingTrade(r);

        if(!this.canPromiseTrade(t.getOffer())){
            return;
        }

        broadcastTrade(t);
    }

    public void setup() {
        addBehaviour(new LifeCycleBehaviour(this));
        addBehaviour(new HandleProposalBehaviour());
        addBehaviour(new HandleCounterProposalAnswerBehaviour());
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

    /**
     * Selects the best trade based of the received counter proposals
     * @param trades
     * @return Best trade
     */
    public abstract int selectBestTrade(List<Trade> trades);

    /**
     * Create a trade to broadcast to other villages
     * @param r Resource to request
     * @return Trade to broadcast
     */
    public abstract Trade createProposingTrade(Resource r);

    /**
     * Decide a counter propose for a given received trade proposal
     * @param t Received trade proposal
     * @return Trade counter proposal
     */
    public abstract Trade decideCounterPropose(Trade t);
}
