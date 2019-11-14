package behaviour;

import agents.Village;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import utils.Trade;

import java.io.IOException;

/**
 * Waits for responses to sent counter proposals (my counter proposal may be Accepted or Rejected by initiators)
 */
public class HandleCounterProposalAnswerBehaviour extends CyclicBehaviour {

    private static final MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
            MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)
    );

    @Override
    public void action() {
        ACLMessage msg = this.myAgent.receive(mt);
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                handleAcceptProposal(msg);
            } else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                handleRejectProposal(msg);
            }
        }
        else {
            block();
        }
    }

    private void handleAcceptProposal(ACLMessage msg) {
        try {
            // Printer.safePrintf("\t%s [RECEIVER]: In finallizing", this.getAgent().getLocalName());
            Trade trade = (Trade) msg.getContentObject();
            Village village = ((Village) this.getAgent());

            village.applyTrade(trade, false);

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContentObject(trade);
            this.myAgent.send(reply);
        } catch (UnreadableException | IOException e) {
            // safePrintf("Could not cast received accept proposal's content object to a Trade!");
            e.printStackTrace();
        }
    }

    private void handleRejectProposal(ACLMessage msg) {
        try {
            Trade trade = (Trade) msg.getContentObject();
            Village village = ((Village) this.getAgent());

            village.closeOpenTrade(trade.getRequest());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
