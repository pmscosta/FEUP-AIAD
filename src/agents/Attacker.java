package agents;

import behaviour.AttackerBehaviour;
import utils.Printer;

public class Attacker extends BaseAgent {

    public void setup() {
        addBehaviour(new AttackerBehaviour(this));
    }
}
