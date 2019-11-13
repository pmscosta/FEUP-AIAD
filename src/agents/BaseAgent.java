package agents;

import behaviour.InitTradeBehaviour;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import protocol.ACLObjectMessage;
import utils.Trade;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static utils.Printer.safePrintf;

public abstract class BaseAgent extends Agent {

    /**
     * Finds all agents EXCEPT itself and the "special-kind-of-special" ones
     */
    public final AMSAgentDescription[] getOtherAgents() {
        AMSAgentDescription[] agents = null;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults((long) -1);
            agents = Arrays.stream(AMSService.search(this, new AMSAgentDescription(), c))
                    .filter(agentDescription -> {
                        return !agentDescription.getName().equals(this.getAID())
                                &&
                                !agentDescription.getName().getLocalName().equals("ams")
                                &&
                                !agentDescription.getName().getLocalName().equals("df");
                    })
                    .toArray(AMSAgentDescription[]::new);
        } catch (Exception e) {
            safePrintf("Problem searching AMS: " + e);
            e.printStackTrace();
        }

        return agents;
    }

    /**
     * Broadcasts a trade to all the other agents
     */
    public final void broadcastTrade(Trade trade) {
        safePrintf(this.getLocalName() + " : Sending trade: %s", trade);

        try {
            ACLObjectMessage msg = new ACLObjectMessage(ACLMessage.CFP, trade);
            for (AMSAgentDescription ad : this.getOtherAgents()) {
                msg.addReceiver(ad.getName());
            }

            Date now = new Date();
            now.setTime(now.getTime()+5000);

            msg.setReplyByDate(now);

            this.addBehaviour(new InitTradeBehaviour(this, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
