package behaviour;

import agents.Village;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static utils.Printer.safePrintf;

public class InitTradeBehaviour extends ContractNetInitiator {

    private final ACLMessage message;

    public InitTradeBehaviour(Agent agent, ACLMessage message)  {
        super(agent, message);
        this.message = message;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        safePrintf(this.myAgent.getLocalName() + " : Handling all responses:");
        safePrintf(this.myAgent.getLocalName() + " : Received %d responses.\n", responses.size());

        List<ACLMessage> proposed_messages = new ArrayList<>();
        List<Trade>  proposed_trades = new ArrayList<>();

        for (Object response_obj : responses) {
            ACLMessage response = (ACLMessage) response_obj;
            ACLMessage reply = response.createReply();
            if (response.getPerformative() == ACLMessage.PROPOSE) {

                Trade t = null;
                try {
                    t = (Trade) message.getContentObject();

                    proposed_messages.add(response);
                    proposed_trades.add(t);

                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

            } else {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                acceptances.add(reply);
            }
        }

        int accepted_index = ((Village) this.myAgent).selectBestTrade(proposed_trades);

        for(int i = 0; i < proposed_messages.size(); i++){

            ACLMessage acl_message = proposed_messages.get(i);

            if(i == accepted_index){
                acl_message.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                ((Village) this.myAgent).accountForNewTrade(proposed_trades.get(accepted_index).getOffer());
            }else{
                acl_message.setPerformative(ACLMessage.REJECT_PROPOSAL);
            }

            acceptances.add(acl_message);
        }

        safePrintf("here");
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {

        for (Object o : resultNotifications) {
            ACLMessage notif = (ACLMessage) o;
            try {
                ((Village) this.myAgent).applyTrade((Trade) message.getContentObject(), true);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

    }
}
