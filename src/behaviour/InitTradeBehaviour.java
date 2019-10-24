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
        super.handleAllResponses(responses, acceptances);
        System.out.println("Handling all responses:");
        System.out.printf("Received %d responses.\n", responses.size());
        System.out.printf("Received %d acceptances.\n", acceptances.size());
    }
}
