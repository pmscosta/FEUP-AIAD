package behaviour;

import agents.Village;
import exceptions.NotEnoughResources;
import utils.Resource;

public class ConsumingBehaviour extends TimeTickerBehaviour {

    public ConsumingBehaviour(Village village) {
        super(village);
    }

    @Override
    protected void onTick() {
        for (Resource r : this.village.getResources().values()) {
            try {
                r.consumeAmount(village.getResourceConsumption());
            } catch (NotEnoughResources e) {
                System.out.println(e.toString());
                this.village.doDelete();
            }
        }
    }
}
