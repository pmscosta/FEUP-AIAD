package behaviour;

import agents.Village;
import utils.Resource;

import static utils.Printer.safePrintf;


public class TradeProposalBehaviour extends TimeTickerBehaviour {

    public TradeProposalBehaviour(Village village) {
        super(village);
    }

    @Override
    protected void onTick() {

        //TODO THIS IS STUPID BUT DEBUG PURPOSES
        // 'TIS NOT STUPID, 'TIS BEAUTIFUL PEDRO-SAN
        safePrintf("%s: %s-(%d) %s-(%d) %s-(%d) %s-(%d)", this.village.getVillageName(),
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
            if (village.shouldProposeTrade(r)) {
                village.proposeTrade(r);
            }
        }
    }
}
