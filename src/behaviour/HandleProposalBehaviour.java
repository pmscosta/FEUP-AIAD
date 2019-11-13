package behaviour;

import agents.Village;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import utils.Printer;
import utils.Trade;

import java.io.IOException;

/**
 * Waits for other initiators to start trades
 */
public class HandleProposalBehaviour extends CyclicBehaviour {

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        ACLMessage msg = this.myAgent.receive(mt);

        if (msg != null) {
            try {
                // Printer.safePrintf("\t%s [RECEIVER]: Received Proposal", this.getAgent().getLocalName());
                Trade trade = (Trade) msg.getContentObject();
                Village village = (Village) this.getAgent();
                ACLMessage reply = msg.createReply();

                if (village.canAcceptTrade(trade) && village.wantToAcceptTrade(trade)) {
                    /*  start accounting for the promised quantity
                     *  since we are the receiver, that resource is the request
                     */
                    village.accountForNewTrade(trade.getRequest());

                    Trade counter_propose = village.decideCounterPropose(trade);

                    reply.setContentObject(counter_propose);
                    // Printer.safePrintf("\t%s [RECEIVER]: Im Proposing for id [%s]", this.getAgent().getLocalName(), reply.getConversationId());
                    reply.setPerformative(ACLMessage.PROPOSE);
                } else {
                    // Printer.safePrintf("\t%s [RECEIVER]: Im Refusing for id [%s]", this.getAgent().getLocalName(), reply.getConversationId());
                    reply.setPerformative(ACLMessage.REFUSE);
                }
                this.myAgent.send(reply);
            } catch (UnreadableException | IOException e) {
                e.printStackTrace();
            }
        }
        else {
            block();
        }
    }
}
