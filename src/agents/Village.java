package agents;

import behaviour.HandleCounterProposalAnswerBehaviour;
import behaviour.HandleProposalBehaviour;
import behaviour.LifeCycleBehaviour;
import behaviour.VulnerableBehaviour;
import exceptions.NotEnoughResources;
import utils.Resource;
import utils.Resource.ResourceType;
import utils.Logger;
import utils.ResourceRandomizer;
import utils.Trade;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static utils.Printer.safePrintf;

public abstract class Village extends BaseAgent {

    public static HashMap<String, String> villagesInfo = new HashMap<>();

    private static final int DEFAULT_RESOURCE_CONSUMPTION = 5;

    private final String name;
    private final int resource_consumption;
    private final List<Resource> production_resources;
    protected final ConcurrentHashMap<ResourceType, Resource> resources;

    ConcurrentHashMap<ResourceType, Integer> openTrades = new ConcurrentHashMap<>();
    public int tick_num = 0;

    public void printOpenTrades(){
        openTrades.entrySet().forEach(entry->{
            safePrintf("%s : %d", entry.getKey() ,entry.getValue());
        });
    }

    Village(String name, int initial_resources_amount, int resource_consumption, List<Resource> production_resources) {
        this.name = name;
        this.resource_consumption = resource_consumption;
        this.production_resources = production_resources;

        resources = new ConcurrentHashMap<ResourceType, Resource>() {{ // Cannot have anonymous inner class in order to be compliant with Java 8
            put(ResourceType.CLAY, new Resource(ResourceType.CLAY, initial_resources_amount));
            put(ResourceType.FOOD, new Resource(ResourceType.FOOD, initial_resources_amount));
            put(ResourceType.STONE, new Resource(ResourceType.STONE, initial_resources_amount));
            put(ResourceType.WOOD, new Resource(ResourceType.WOOD, initial_resources_amount));
        }};
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

    public List<Resource> getSortedResources() {
        return this.getResources().values().stream().sorted().collect(Collectors.toList());
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

        int initial_stone_amount = this.getResources().get(Resource.ResourceType.STONE).getAmount();
        int initial_wood_amount = this.getResources().get(Resource.ResourceType.WOOD).getAmount();
        int initial_food_amount = this.getResources().get(Resource.ResourceType.FOOD).getAmount();
        int initial_clay_amount = this.getResources().get(Resource.ResourceType.CLAY).getAmount();

        try {
            this.resources.get(request.getType()).produceAmount(request.getAmount());
            this.resources.get(offer.getType()).consumeAmount(offer.getAmount());
        } catch (NotEnoughResources e) {
            // Never happens unless there are concurrency problems since canAcceptTrade has returned true before
            e.printStackTrace();
        }

        int final_stone_amount = this.getResources().get(Resource.ResourceType.STONE).getAmount();
        int final_wood_amount = this.getResources().get(Resource.ResourceType.WOOD).getAmount();
        int final_food_amount = this.getResources().get(Resource.ResourceType.FOOD).getAmount();
        int final_clay_amount = this.getResources().get(Resource.ResourceType.CLAY).getAmount();

        Logger.getInstance().add(String.format(
                "[Trade Applying] %d %s %d %d %d %d\n",
                this.tick_num,
                    this.getVillageName(),
                    final_stone_amount-initial_stone_amount,
                    final_wood_amount-initial_wood_amount,
                    final_food_amount-initial_food_amount,
                    final_clay_amount-initial_clay_amount
                ));

        safePrintf("%s: As a %s, just did this trade:", getVillageName(), is_proposer ? "proposer" : "responder");
        safePrintf(t.toString());
    }

    public void proposeTrades(List<Trade> trades) {
        for (Trade trade : trades) {
            if (this.canPromiseTrade(trade.getOffer())) {
                broadcastTrade(trade);
            }
        }
    }

    public void setup() {
        addBehaviour(new LifeCycleBehaviour(this));
        addBehaviour(new HandleProposalBehaviour());
        addBehaviour(new HandleCounterProposalAnswerBehaviour());
        addBehaviour(new VulnerableBehaviour());
    }

    protected int getTargetSurvivalQuantity(int num_ticks) {
        return getResourceConsumption() * num_ticks;
    }

    /**
     * Decides whether the given trade should be accepted or not. Override in subclasses to change the passive behaviour
     *
     * @param t The trade to decide acceptance of
     * @return true if the trade should be accepted, false otherwise
     */
    public abstract boolean wantToAcceptTrade(Trade t);

    /**
     * Selects the best trade based of the received counter proposals
     * @param trades
     * @return Best trade
     */
    public abstract int selectBestTrade(List<Trade> trades);

    /**
     * Decide a counter propose for a given received trade proposal
     * @param t Received trade proposal
     * @return Trade counter proposal
     */
    public abstract Trade decideCounterPropose(Trade t);

    /**
     * Returns all the trades that a village wants to make at a certain point in time
     * @return List of desired trades
     */
    public abstract List<Trade> generateDesiredTrades();
}
