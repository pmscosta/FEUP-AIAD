package behaviour;

import agents.Village;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.Trade;

import java.util.Vector;

import static utils.Printer.safePrintf;
import static utils.Printer.safePrintf;

public class InitTradeBehaviour extends ContractNetInitiator {

    private final ACLMessage message;

    public InitTradeBehaviour(Agent agent, ACLMessage message) {
        super(agent, message);
        this.message = message;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        safePrintf(this.myAgent.getLocalName() + " : Handling all responses:");
        safePrintf(this.myAgent.getLocalName() + " : Received %d responses.\n", responses.size());

        boolean already_accepted = false;

        // TODO: Change proposal acceptance heuristic. Currently accepting the first one and rejecting all the others
        for (Object response_obj : responses) {
            ACLMessage response = (ACLMessage) response_obj;
            ACLMessage reply = response.createReply();
            if (response.getPerformative() == ACLMessage.PROPOSE && !already_accepted) {
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                already_accepted = true;
            } else {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            }
            acceptances.add(reply);
        }
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
