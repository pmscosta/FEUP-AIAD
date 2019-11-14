package agents;

import utils.Resource;
import utils.Trade;

import java.util.Arrays;
import java.util.List;

public class SmartVillage extends Village {

    private static int MIN_THRESHOLD;
    private static final int THRESHOLD = (int) (Resource.DEFAULT_AMOUNT * 0.9);
    private static final double MIN_SMART_RATIO_VALUE = 0.8;
    private static final double MID_SMART_RATIO_VALUE = 1.1;
    private static final double MAX_SMART_RATIO_VALUE = 1.5;
    private static final int TARGET_SURVIVAL_TIME = 5;

    SmartVillage(String name) {
        super(name);
    }

    SmartVillage(String name, int resource_consumption) {
        super(name, resource_consumption);
    }

    public SmartVillage(String name, int resource_consumption, List<Resource> production_resources) {
        super(name, resource_consumption, production_resources);
        MIN_THRESHOLD = getResourceConsumption() * 2;
    }

    private boolean isInCriticalSituation(){
        return getMostDepletedResource().getAmount() < MIN_THRESHOLD;
    }

    // y = ((1.5-0.2)/60)*x+0.2
    private double calculateDesiredRatio(Resource r) {
        if (r.getAmount() > THRESHOLD) {
            return MAX_SMART_RATIO_VALUE;
        }


        return MIN_SMART_RATIO_VALUE + ((MAX_SMART_RATIO_VALUE- MIN_SMART_RATIO_VALUE)/THRESHOLD)*r.getAmount();
    }

    @Override
    public boolean wantToAcceptTrade(Trade t) {
        Resource request = t.getRequest();

        List<Resource> my_sorted_resources = getSortedResources();

        Resource most_depleted = my_sorted_resources.get(0);

        //if we are really low on the amount offered, just accept it
        if (resources.get(t.getOffer().getType()).getAmount() < MIN_THRESHOLD) {
            return true;
        }

        //if he's offering what we need the most and the ratio is "good enough", accept it
        if (t.getOffer().getType() == most_depleted.getType() && t.getRatio(false) > MIN_SMART_RATIO_VALUE) {
            return true;
        }

        //if the ratio is better for us, check if we can just accept the trade without going into "debt"
        if (t.getRatio(false) > 1) {
            return ((resources.get(t.getRequest().getType()).getAmount() - t.getRequest().getAmount()) > THRESHOLD);
        }

        return (request.getType() == my_sorted_resources.get(2).getType() || request.getType() == my_sorted_resources.get(3).getType());
    }

    @Override
    public int selectBestTrade(List<Trade> trades) {
        int best_trade_index = 0;

        for (int i = 1; i < trades.size(); i++) {
            if (trades.get(i).getRatio(false) > trades.get(best_trade_index).getRatio(false)) {
                best_trade_index = i;
            }
        }

        return best_trade_index;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Trade decideCounterPropose(Trade t) {

        //if we are in a critical situation, don't even try to counter propose
        if(isInCriticalSituation()){
            return t;
        }

        List<Resource> my_sorted_resources = getSortedResources();

        if (t.getRequest().getType() == my_sorted_resources.get(2).getType()) {
            return new Trade(
                    new Resource(t.getRequest().getType(), (int) (0.9 * t.getRequest().getAmount())),
                    t.getOffer()
            );
        }

        return t;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<Trade> generateDesiredTrades() {
        Resource most_depleted_resource = this.getMostDepletedResource();
        Resource most_abundant_resource = this.getMostAbundantResource();


        double ratio = calculateDesiredRatio(most_depleted_resource);
        int amount = getTargetSurvivalQuantity(TARGET_SURVIVAL_TIME);

        Trade trade = new Trade(
                new Resource(most_depleted_resource.getType(), amount),
                new Resource(most_abundant_resource.getType(), (int) (amount / ratio))
        );

        return Arrays.asList(trade);
    }
}
