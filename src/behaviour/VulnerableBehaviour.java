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
                        "[Village Attacked] %s attacked with Attack Vector: [%d]\n",
                        village.getVillageName(),
                        attack_vector.getVector()
                ));

                for (Resource resource : village.getResources().values()) {
                    int attacked_amount = (int) (resource.getAmount() * ((double) attack_vector.getVector() / 100.0));
                    resource.consumeAmount(attacked_amount);
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
