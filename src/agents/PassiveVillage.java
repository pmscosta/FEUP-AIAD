package agents;

import behaviour.ConsumingBehaviour;
import behaviour.HandleTradeBehaviour;
import behaviour.TradeProposalBehaviour;
import behaviour.ProducingBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Resource;
import utils.Trade;

import java.util.List;

public class PassiveVillage extends Village {

    private static final int RESOURCES_THRESHOLD = 980;

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
    }

    private int getTargetSurvivalQuantity() {
        return getResourceConsumption() * TARGET_SURVIVAL_TIME;
    }

    private int getTradeResourceQuantity(Resource request, Resource offer) {
        int target_survival_quantity = (RESOURCES_THRESHOLD - request.getAmount()) + getTargetSurvivalQuantity();
        int midpoint_quantity = Math.abs(RESOURCES_THRESHOLD - offer.getAmount()) / 2;

        return Math.min(target_survival_quantity, midpoint_quantity);
    }

    @Override
    public void setup() {
        addBehaviour(new TradeProposalBehaviour(this));
        addBehaviour(new ProducingBehaviour(this));
        addBehaviour(new ConsumingBehaviour(this));

        // TODO: Not match all :upside_down_smile:
        MessageTemplate mt =
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.CFP),
                        MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL)
                );

        addBehaviour(new HandleTradeBehaviour(this, mt));
    }

    @Override
    public boolean shouldProposeTrade(Resource r) {
        return r.getAmount() < RESOURCES_THRESHOLD;
    }

    @Override
    public Trade createProposingTrade(Resource r){

        Resource most_abundant_resource = getMostAbundantResource();
        int quantity = getTradeResourceQuantity(r, most_abundant_resource);

        return new Trade(
                new Resource(r.getType(), quantity),
                new Resource(most_abundant_resource.getType(), quantity));
    }

    @Override
    public boolean wantToAcceptTrade(Trade t) {

        int have = this.resources.get(t.getRequest().getType()).getAmount();
        int requested = t.getRequest().getAmount();

        return (have - requested) > RESOURCES_THRESHOLD;
    }

}
