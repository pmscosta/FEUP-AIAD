package agents;

import behaviour.AttackerBehaviour;

public class Attacker extends BaseAgent {

    private final int attacked_resources_percentage;

    public Attacker(int attacked_resources_percentage) {
        this.attacked_resources_percentage = attacked_resources_percentage;
    }

    public int getAttackedResourcesPercentage() {
        return this.attacked_resources_percentage;
    }

    public void setup() {
        addBehaviour(new AttackerBehaviour(this));
    }
}
