package behaviour;

import agents.Village;
import exceptions.NotEnoughResources;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import utils.Resource;
import java.util.List;

import static utils.Printer.safePrintf;

public class VulnerableBehaviour extends CyclicBehaviour {

    private static final MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.MatchOntology("attack"),
            MessageTemplate.MatchPerformative(ACLMessage.INFORM)
    );

    @Override
    public void action() {
        ACLMessage msg = this.myAgent.receive(mt);
        Village village = (Village) this.getAgent();

        if (msg != null) {
            try {
                // Printer.safePrintf("\t%s [RECEIVER]: Received Proposal", this.getAgent().getLocalName());
                List<Resource> attack_vector = (List<Resource>) msg.getContentObject();

                for (Resource attack : attack_vector) {
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
