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
        return new AttackVector(attacker.getAttackedResourcesPercentage());
    }

    @Override
    protected void onTick() {
        AttackVector attack_vector = buildAttackVector();

        try {
            ACLObjectMessage msg = new ACLObjectMessage(ACLMessage.CFP, attack_vector);
            msg.setPerformative(ACLMessage.INFORM);
            msg.setOntology("attack");

            AMSAgentDescription[] village_descriptions = attacker.findVillages();

            if (village_descriptions.length == 0) {
                return;
            }

            msg.addReceiver(
                    village_descriptions[ThreadLocalRandom.current().nextInt(village_descriptions.length)].getName()
            );

            this.myAgent.send(msg);

            Logger.getInstance().add(String.format(
                    "[Attacker Attacking] Attacking with Attack Vector: [%d]\n",
                    attack_vector.getVector()
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}