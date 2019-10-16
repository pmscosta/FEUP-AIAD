package behaviour;

import agents.Village;
import exceptions.NotEnoughResources;
import utils.Resource;

public class ConsumingBehaviour extends TimeTickerBehaviour {

    private static final int DEFAULT_RESOURCE_CONSUMPTION = 500;

    public ConsumingBehaviour(Village village) {
        super(village);
    }

    @Override
    protected void onTick() {
        for (Resource r : this.village.getResources()) {

            try {
                r.consumeAmount(DEFAULT_RESOURCE_CONSUMPTION);
            } catch (NotEnoughResources e) {
                System.out.println(e.toString());
                this.village.doDelete();
            }
        }
    }
}
