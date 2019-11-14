package behaviour;

import agents.Village;
import exceptions.NotEnoughResources;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import utils.AttackVector;
import utils.Resource;
import utils.Logger;

import static utils.Printer.safePrintf;

public class VulnerableBehaviour extends CyclicBehaviour {

    private static final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchOntology("attack"),
            MessageTemplate.MatchPerformative(ACLMessage.INFORM)
    );

    @Override
    public void action() {
        ACLMessage msg = this.myAgent.receive(mt);
        Village village = (Village) this.getAgent();

        if (msg != null) {
            try {
                AttackVector attack_vector = ((AttackVector) msg.getContentObject());

                Logger.getInstance().add(String.format(
                        "[Attack] Attack Vector: [%s %s %s %s]\n",
                        village.getVillageName(),
                        attack_vector.getVector().get(0),
                        attack_vector.getVector().get(1),
                        attack_vector.getVector().get(2),
                        attack_vector.getVector().get(3)
                ));

                for (Resource attack : attack_vector.getVector()) {
                    village.getResources().get(attack.getType()).consumeAmount(attack.getAmount());
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            } catch (NotEnoughResources notEnoughResources) {
                safePrintf("\t\t*** %s TERMINATED ***", village.getVillageName());
                village.doDelete();
            }
        }
        else {
            block();
        }
    }
}
