package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;

import utils.Resource;

import java.util.concurrent.TimeUnit;

public class Village extends Agent {

    private Resource WOOD = new Resource("WOOD", 1000);
    private Resource STONE = new Resource("STONE", 1000);
    private Resource FOOD = new Resource("FOOD", 1000);

    public void setup() {
        addBehaviour(new AwaitTrades());
        addBehaviour(new RequestTrades());
    }

    public void printAgents(AMSAgentDescription[] agents) {
        AID myID = getAID();
        for (int i = 0; i < agents.length; i++) {
            AID agentID = agents[i].getName();
            System.out.println(
                    (agentID.equals(myID) ? "*** " : "    ")
                            + i + ": " + agentID.getName()
            );
        }
    }

    public AMSAgentDescription[] getAgents() {
        AMSAgentDescription[] agents = null;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e) {
            System.out.println("Problem searching AMS: " + e);
            e.printStackTrace();
        }

        return agents;


    }

    class RequestTrades extends CyclicBehaviour {

        public void action() {

            System.out.println("Asking for a trade");

            ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);

            msg.setContent("Holla wan't some wood?");

            for (AMSAgentDescription a : getAgents()) {
                msg.addReceiver(a.getName());
            }

            send(msg);

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    class AwaitTrades extends CyclicBehaviour {

        public void action() {

            System.out.println("Waiting for trades");

            ACLMessage msg = receive();

            if (msg != null) {
                System.out.println("Received" + msg);
            } else {
                block();
            }

        }

    }
}
