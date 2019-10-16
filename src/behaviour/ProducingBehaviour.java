package behaviour;

import agents.Village;
import utils.Resource;

public class ProducingBehaviour extends TimeTickerBehaviour {

    public ProducingBehaviour(Village village) {
        super(village);
    }

    @Override
    protected void onTick() {
        for (Resource produced_resource : this.village.getProductionResources()) {
            village.getResources().get(produced_resource.getType()).produceAmount(produced_resource.getAmount());
        }
    }
}
