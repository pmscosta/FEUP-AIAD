package agents;

import utils.Resource;
import utils.Trade;

import java.util.Arrays;
import java.util.List;

public class GreedyVillage extends Village {

    private static final int THRESHOLD = (int) (Resource.DEFAULT_AMOUNT * 1.2);
    private static final double MIN_GREEDY_RATIO_VALUE = 1.0;
    private static final double MAX_GREEDY_RATIO_VALUE = 2.0;
    private static final int TARGET_SURVIVAL_TIME = 5;

    public GreedyVillage(String name) {
        super(name);
    }

    public GreedyVillage(String name, int resource_consumption) {
        super(name, resource_consumption);
    }

    public GreedyVillage(String name, int resource_consumption, List<Resource> production_resources) {
        super(name, resource_consumption, production_resources);
        villagesInfo.put(getVillageName(), "Greedy");
    }

    private double calculateDesiredRatio(Resource r) {
        if (r.getAmount() > THRESHOLD) {
            return MAX_GREEDY_RATIO_VALUE;
        }

        return MIN_GREEDY_RATIO_VALUE + (MAX_GREEDY_RATIO_VALUE/THRESHOLD)*r.getAmount();
    }

    @Override
    public boolean wantToAcceptTrade(Trade t) {
        Resource request = t.getRequest();

        List<Resource> my_sorted_resources = getSortedResources();

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

    @Override
    public Trade decideCounterPropose(Trade t) {
        List<Resource> my_sorted_resources = getSortedResources();

        if (t.getRequest().getType() == my_sorted_resources.get(2).getType()) {
            return new Trade(t.getSource(),
                    new Resource(t.getRequest().getType(), (int) (0.9 * t.getRequest().getAmount())),
                    t.getOffer()
            );
        }

        return t;
    }

    @Override
    public List<Trade> generateDesiredTrades() {
        Resource most_depleted_resource = this.getMostDepletedResource();
        Resource most_abundant_resource = this.getMostAbundantResource();


        double ratio = calculateDesiredRatio(most_depleted_resource);
        int amount = getTargetSurvivalQuantity(TARGET_SURVIVAL_TIME);

        Trade trade = new Trade(getVillageName(),
                new Resource(most_depleted_resource.getType(), amount),
                new Resource(most_abundant_resource.getType(), (int) (amount/ratio))
        );

        return Arrays.asList(trade);
    }
}
