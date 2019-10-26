package behaviour;

import agents.Village;
import utils.Resource;
import utils.Trade;

import static utils.Printer.safePrintf;


public class PassiveBehaviour extends TimeTickerBehaviour {

    private static final int RESOURCES_THRESHOLD = 995;

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

        //TODO THIS IS STUPID BUT DEBUG PURPOSES
        safePrintf("%s: %s-%d %s-%d %s-%d %s-%d", this.village.getVillageName(),
                Resource.ResourceType.STONE,
                this.village.getResources().get(Resource.ResourceType.STONE).getAmount(),
                Resource.ResourceType.WOOD,
                this.village.getResources().get(Resource.ResourceType.WOOD).getAmount(),
                Resource.ResourceType.FOOD,
                this.village.getResources().get(Resource.ResourceType.FOOD).getAmount(),
                Resource.ResourceType.CLAY,
                this.village.getResources().get(Resource.ResourceType.CLAY).getAmount()
        );

        for (Resource r : village.getResources().values()) {
            if (r.getAmount() < RESOURCES_THRESHOLD) {
                Resource most_abundant_resource = village.getMostAbundantResource();
                int quantity = getTradeResourceQuantity(most_abundant_resource, r);
                village.broadcastTrade(new Trade(
                        new Resource(r.getType(), quantity),
                        new Resource(most_abundant_resource.getType(), quantity)
                ));
            }
        }
    }
}
