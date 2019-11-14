package agents;

import behaviour.TradeInitiatorBehaviour;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import utils.Trade;

import java.util.Arrays;

import static utils.Printer.safePrintf;

public abstract class BaseAgent extends Agent {

    /**
     * Finds all agents EXCEPT itself and the "special-kind-of-special" ones
     */
    public final AMSAgentDescription[] findVillages() {
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
                                !agentDescription.getName().getLocalName().equals("df")
                                &&
                                !agentDescription.getName().getLocalName().equals("attacker");
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
        this.addBehaviour(new TradeInitiatorBehaviour(this, trade));
    }
}
