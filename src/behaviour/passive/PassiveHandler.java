package behaviour.passive;

import agents.Village;
import behaviour.Handler;
import behaviour.HandlerInterface;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import protocol.AcceptTradeMessage;
import utils.Trade;

import java.io.IOException;

public class PassiveHandler extends Handler{

    private Village village;

    public PassiveHandler(Village village){
        this.village = village;
    }

    @Override
    public void handleReceivedTrade(ACLMessage msg) throws UnreadableException, IOException {
        Trade trade = (Trade) msg.getContentObject();

        System.out.println(this.village.getVillageName() + ": Village " + msg.getSender().getName() + " sent trade " + trade);

        //TODO CHECK IF WE CAN ACCEPT THE TRADE

        AcceptTradeMessage response = new AcceptTradeMessage(msg, trade);

        System.out.println(response.getInReplyTo());

        this.village.send(new AcceptTradeMessage(msg, trade));
    }

    @Override
    public void handleConfirmedTrade(ACLMessage msg) throws UnreadableException {
        Trade trade = (Trade) msg.getContentObject();

        System.out.println(this.village.getVillageName() + ": Village " + msg.getSender().getName() + " accepted trade " + trade);

    }

    @Override
    public void action() {
        ACLMessage msg = this.village.receive();
        if (msg != null) {

            try {
                dispatcher(msg);
            } catch (IOException | UnreadableException e) {
                e.printStackTrace();
            }


        } else {
            block();
        }
    }
}