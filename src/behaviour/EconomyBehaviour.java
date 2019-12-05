package behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import utils.Economy;

public class EconomyBehaviour extends TickerBehaviour {
    private static final int DEFAULT_TIME = 2000;

    public EconomyBehaviour(Agent agent) {
        super(agent, DEFAULT_TIME);
    }

    @Override
    protected void onTick() {
        Economy.saveRecord();
    }
}
