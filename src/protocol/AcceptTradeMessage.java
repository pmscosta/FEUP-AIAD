package protocol;

import jade.lang.acl.ACLMessage;
import utils.Trade;

import java.io.IOException;

public class AcceptTradeMessage extends ACLMessage {
    public AcceptTradeMessage(ACLMessage proposal, Trade trade) throws IOException {
        super(ACLMessage.ACCEPT_PROPOSAL);
        this.addReceiver(proposal.getSender());
        this.setContentObject(trade);
    }
}