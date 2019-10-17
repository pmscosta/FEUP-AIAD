package behaviour.passive;

import agents.Village;
import behaviour.TimeTickerBehaviour;
import utils.Resource;
import utils.Trade;


public class PassiveBehaviour extends TimeTickerBehaviour {

    private static final int RESOURCES_THRESHOLD = 45;

    // The village will try to trade enough resources to survive for
    // 10 ticks ,based on the village resource consumption rate
    private static final int TARGET_SURVIVAL_TIME = 10;

    public PassiveBehaviour(Village village) {
        super(village);
    }

    private int getTargetSurvivalQuantity() {
        return village.getResourceConsumption() * TARGET_SURVIVAL_TIME;
    }

    private int getTradeResourceQuantity(Resource request, Resource offer) {
        int target_survival_quantity = (RESOURCES_THRESHOLD - request.getAmount()) + getTargetSurvivalQuantity();
        int midpoint_quantity = Math.abs(RESOURCES_THRESHOLD - offer.getAmount()) / 2;

        return Math.min(target_survival_quantity, midpoint_quantity);
    }

    @Override
    protected void onTick() {
        System.out.println("\n" + village.getName());
        for (Resource r : village.getResources().values()) {
            System.out.print(r + "\t");
            if (r.getAmount() < RESOURCES_THRESHOLD) {
                Resource most_abundant_resource = village.getMostAbundantResource();
                System.out.print(most_abundant_resource + "\t");
                int quantity = getTradeResourceQuantity(r, most_abundant_resource);
                village.broadcastTrade(new Trade(
                        new Resource(r.getType(), quantity),
                        new Resource(most_abundant_resource.getType(), quantity)
                ));
            }
        }

        System.out.println("\n");
    }
}
