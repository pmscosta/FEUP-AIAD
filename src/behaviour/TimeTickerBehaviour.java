package behaviour;

import agents.Village;
import jade.core.behaviours.TickerBehaviour;

public abstract class TimeTickerBehaviour extends TickerBehaviour {

    private static final int DEFAULT_TIME = 4000;
    protected final Village village;

    protected TimeTickerBehaviour(Village village) {
        super(village, DEFAULT_TIME);
        this.village = village;
    }
}
