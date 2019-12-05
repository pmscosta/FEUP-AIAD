package agents;

import behaviour.EconomyBehaviour;
import jade.core.Agent;

public class EconomyAgent extends Agent {
    public void setup() {
        addBehaviour(new EconomyBehaviour(this));
    }
}
