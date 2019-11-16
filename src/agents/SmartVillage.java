package agents;

import utils.Resource;
import utils.Trade;

import java.util.ArrayList;
import java.util.List;

public class SmartVillage extends Village {

    private static int MIN_THRESHOLD;
    private static final int THRESHOLD = (int) (Resource.DEFAULT_AMOUNT * 0.9);
    private static final double MIN_SMART_RATIO_VALUE = 0.8;
    private static final double MAX_SMART_RATIO_VALUE = 1.5;
    private static int OK_THRESHOLD;
    private static final double OPTIMAL_SMART_RATIO_VALUE = 2.0;
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
        // the minimum is threshold is the amount needed to survive two ticks
        // any quantity below that, we are in danger
        OK_THRESHOLD = getResourceConsumption() * 5;
        villagesInfo.put(getVillageName(), "Smart");
    }

    private boolean passiveVillagesExist() {
        return villagesInfo.containsValue("Passive");
    }

    private boolean isInCriticalSituation() {
        return getMostDepletedResource().getAmount() < MIN_THRESHOLD;
    }

    private boolean isInOkSituation() {
        for (Resource r : getSortedResources()) {
            if (r.getAmount() < OK_THRESHOLD)
                return false;
        }

        return true;
    }

    // y = ((1.5-0.2)/60)*x+0.2
    private double calculateDesiredRatio(Resource r) {
        if (r.getAmount() > THRESHOLD) {
            return MAX_SMART_RATIO_VALUE;
        }

        return MIN_SMART_RATIO_VALUE + ((MAX_SMART_RATIO_VALUE - MIN_SMART_RATIO_VALUE) / THRESHOLD) * r.getAmount();
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
        int best_trade_index = -1;
        double best_ratio = Integer.MIN_VALUE;

        for (int i = 0; i < trades.size(); i++) {

            //if a greedy village tried to propose us a "bad" offer and we are kinda stable, deny it
            if (villagesInfo.get(trades.get(i).getSource()).equals("Greedy")
                    && trades.get(i).getRatio(true) < 1
                    && isInOkSituation()) {
                continue;
            }

            if (trades.get(i).getRatio(false) > best_ratio) {
                best_trade_index = i;
                best_ratio = trades.get(i).getRatio(false);
            }
        }
        return best_trade_index;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Trade decideCounterPropose(Trade t) {

        //if we are in a critical situation, don't even try to counter propose
        if (isInCriticalSituation()) {
            return t;
        }

        List<Resource> my_sorted_resources = getSortedResources();

        if (t.getRequest().getType() == my_sorted_resources.get(2).getType()) {
            return new Trade(t.getSource(),
                    new Resource(t.getRequest().getType(), (int) (0.9 * t.getRequest().getAmount())),
                    t.getOffer()
            );
        }

        return t;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<Trade> generateDesiredTrades() {
        List<Trade> trades = new ArrayList<>();

        Resource most_depleted_resource = this.getMostDepletedResource();
        Resource most_abundant_resource = this.getMostAbundantResource();

        double ratio = calculateDesiredRatio(most_depleted_resource);
        int amount = getTargetSurvivalQuantity(TARGET_SURVIVAL_TIME);

        Resource request = new Resource(most_depleted_resource.getType(), amount);

        Trade trade = new Trade(getVillageName(),
                request,
                new Resource(most_abundant_resource.getType(), (int) (amount / ratio))
        );

        trades.add(trade);

        //if there are any passive villages in the system, just send a really good trade since they will accept it
        if (passiveVillagesExist()) {
            Trade bonusTrade = new Trade(getVillageName(),
                    request,
                    new Resource(most_abundant_resource.getType(),
                            (int) (amount / OPTIMAL_SMART_RATIO_VALUE)));
            trades.add(bonusTrade);
        }

        return trades;
    }
}
