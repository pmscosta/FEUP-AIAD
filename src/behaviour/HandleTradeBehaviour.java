package behaviour;

import agents.Village;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import utils.Trade;

import java.io.IOException;

import static utils.Printer.safePrintf;

public class HandleTradeBehaviour extends ContractNetResponder {

    public HandleTradeBehaviour(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, NotUnderstoodException {
        safePrintf(this.getAgent().getLocalName() + " : Handling received trade offer ...");

        try {
            Trade t = (Trade) cfp.getContentObject();
            safePrintf(this.getAgent().getLocalName() + " : Received trade: " + t);
            Village v = (Village) this.getAgent();

            if (v.canAcceptTrade(t) && v.wantToAcceptTrade(t)) {
                safePrintf(this.getAgent().getLocalName() + " : Can and want to accept!");
                ACLMessage reply = cfp.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContentObject(t); //TODO this is not needed since the initiator can keep count of the trade he proposed
                return reply;
            }
        } catch (UnreadableException | IOException e) {
            safePrintf("Could not cast received message's content object to a Trade!");
            e.printStackTrace();
            throw new NotUnderstoodException("Content Object not Trade");
        }

        safePrintf("Not accepting the trade");
        throw new RefuseException("Sorry, don't want to/can't accept the Trade at the moment :c");
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        safePrintf(this.getAgent().getLocalName() + " : My proposal was accepted!");
        try {
            Trade t = (Trade) cfp.getContentObject();
            ((Village) this.getAgent()).doAcceptedTrade(t);

        } catch (UnreadableException e) {
            safePrintf("Could not cast received accept proposal's content object to a Trade!");
            e.printStackTrace();
        }

        ACLMessage reply = accept.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        return reply;
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        safePrintf("My proposal was rejected... :c");
        // TODO: Free the "locked" resources
        super.handleRejectProposal(cfp, propose, reject);
    }

    private void closeNegotiation(ACLMessage cfp, boolean success){

        ACLMessage reply = cfp.createReply();

        reply.setPerformative(success ? ACLMessage.INFORM : ACLMessage.FAILURE);

        this.myAgent.send(reply);

    }
}
