package behaviour;

import agents.Attacker;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.lang.acl.ACLMessage;
import protocol.ACLObjectMessage;
import utils.AttackVector;
import utils.Resource;
import utils.Resource.ResourceType;
import utils.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AttackerBehaviour extends TickerBehaviour {

    private static final int DEFAULT_TIME = 5000;
    private static final int MIN_STEAL_AMOUNT = 0;
    private static final int MAX_STEAL_AMOUNT = 12;

    final Attacker attacker;

    public AttackerBehaviour(Attacker attacker) {
        super(attacker, DEFAULT_TIME);
        this.attacker = attacker;
    }

    public AttackVector buildAttackVector() {
        return new AttackVector(Arrays
                .stream(ResourceType.values())
                .map(resource_type -> new Resource(resource_type, ThreadLocalRandom.current().nextInt(MIN_STEAL_AMOUNT, MAX_STEAL_AMOUNT+1)))
                .collect(Collectors.toList()));
    }

    @Override
    protected void onTick() {
        AttackVector attack_vector = buildAttackVector();

        try {
            ACLObjectMessage msg = new ACLObjectMessage(ACLMessage.CFP, attack_vector);
            msg.setPerformative(ACLMessage.INFORM);
            msg.setOntology("attack");

            AMSAgentDescription[] village_descriptions = attacker.findVillages();
            msg.addReceiver(
                    village_descriptions[ThreadLocalRandom.current().nextInt(village_descriptions.length)].getName()
            );

            this.myAgent.send(msg);

            Logger.getInstance().add(String.format(
                    "[Village Attacked] Attack Vector: [%s %s %s %s]\n",
                    attack_vector.getVector().get(0),
                    attack_vector.getVector().get(1),
                    attack_vector.getVector().get(2),
                    attack_vector.getVector().get(3)
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}