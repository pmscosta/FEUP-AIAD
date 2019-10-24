package behaviour;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.sql.SQLOutput;
import java.util.concurrent.ThreadLocalRandom;

public class HandleTradeBehaviour extends ContractNetResponder {

    public HandleTradeBehaviour(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        System.out.println("Handling received trade offer ...");

        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new RefuseException("NO");
        }

        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new NotUnderstoodException("Wat?");
        }

        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.setContent("Holla");
        return msg;
    }
}
