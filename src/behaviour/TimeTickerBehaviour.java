package behaviour;

import agents.Village;
import jade.core.behaviours.TickerBehaviour;
import utils.Resource;

public abstract class TimeTickerBehaviour extends TickerBehaviour {

    private static final int DEFAULT_TIME = 1000;

    final Village village;

    TimeTickerBehaviour(Village village) {
        super(village, DEFAULT_TIME);
        this.village = village;
    }


    @Override
    protected void onTick() {
        for (Resource r : this.village.getResources()) {
            if (r.getAmount() <= 0)
                this.village.doDelete();
        }

    }
}
