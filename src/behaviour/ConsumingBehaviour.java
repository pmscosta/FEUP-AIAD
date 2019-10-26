package behaviour;

import agents.Village;
import exceptions.NotEnoughResources;
import utils.Resource;

import static utils.Printer.safePrintf;
import static utils.Printer.safePrintf;

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
                safePrintf(e.toString());
                this.village.doDelete();
            }
        }
    }
}
