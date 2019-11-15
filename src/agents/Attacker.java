package agents;

import behaviour.AttackerBehaviour;

public class Attacker extends BaseAgent {

    public void setup() {
        addBehaviour(new AttackerBehaviour(this));
    }
}
