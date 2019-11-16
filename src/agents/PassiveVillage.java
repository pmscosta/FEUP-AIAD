package agents;

import utils.Resource;
import utils.Trade;

import java.util.LinkedList;
import java.util.List;

public class PassiveVillage extends Village {

    private static final int RESOURCES_THRESHOLD = 40;

    // The village will try to trade enough resources to survive for
    // 10 ticks ,based on the village resource consumption rate
    private static final int TARGET_SURVIVAL_TIME = 10;

    public PassiveVillage(String name) {
        super(name);
    }

    public PassiveVillage(String name, int resource_consumption) {
        super(name, resource_consumption);
    }

    public PassiveVillage(String name, int resource_consumption, List<Resource> production_resources) {
        super(name, resource_consumption, production_resources);
        villagesInfo.put(getVillageName(), "Passive");
    }

    private int getTradeResourceQuantity(Resource request, Resource offer) {
        int target_survival_quantity = (RESOURCES_THRESHOLD - request.getAmount()) + getTargetSurvivalQuantity(TARGET_SURVIVAL_TIME);
        int midpoint_quantity = Math.abs(RESOURCES_THRESHOLD - offer.getAmount()) / 2;

        return Math.min(target_survival_quantity, midpoint_quantity);
    }


    @Override
    public boolean wantToAcceptTrade(Trade t) {

        int have = this.resources.get(t.getRequest().getType()).getAmount();
        int requested = t.getRequest().getAmount();

        return (have - requested) > RESOURCES_THRESHOLD;
    }

    @Override
    public int selectBestTrade(List<Trade> trades) {
        return 0;
    }

    @Override
    public Trade decideCounterPropose(Trade t) {
        // Passive Village offer a counter_propose equal to the original propose
        return t;
    }

    @Override
    public List<Trade> generateDesiredTrades() {
        List<Trade> trades = new LinkedList<>();
        Resource most_abundant_resource = getMostAbundantResource();

        for (Resource r : resources.values()) {
            if (r.getAmount() < RESOURCES_THRESHOLD) {
                int quantity = getTradeResourceQuantity(r, most_abundant_resource);

                trades.add(new Trade(getVillageName(),
                        new Resource(r.getType(), quantity),
                        new Resource(most_abundant_resource.getType(), quantity))
                );
            }
        }

        return trades;
    }
}
