package protocol;

import jade.lang.acl.ACLMessage;
import utils.Trade;

import java.io.IOException;

public class StartTradeMessage extends ACLMessage {
    public StartTradeMessage(Trade trade) throws IOException {
        super(ACLMessage.PROPOSE);
        this.setContentObject(trade);
    }
}
