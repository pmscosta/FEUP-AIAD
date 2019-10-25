package behaviour;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Vector;

public class InitTradeBehaviour extends ContractNetInitiator {

    private final ACLMessage message;

    public InitTradeBehaviour(Agent agent, ACLMessage message) {
        super(agent, message);
        this.message = message;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        System.out.println("Handling all responses:");
        System.out.printf("Received %d responses.\n", responses.size());

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
}
