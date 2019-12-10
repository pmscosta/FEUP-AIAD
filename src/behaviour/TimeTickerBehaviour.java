package behaviour;

import agents.Village;
import jade.core.behaviours.TickerBehaviour;

public abstract class TimeTickerBehaviour extends TickerBehaviour {

    public static final int DEFAULT_TIME = 500;
    final Village village;

    TimeTickerBehaviour(Village village) {
        super(village, DEFAULT_TIME);
        this.village = village;
    }

}
