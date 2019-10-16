package agents;

import behaviour.ConsumingBehaviour;
import behaviour.PassiveBehaviour;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

import utils.Resource;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Village extends Agent {

    private Resource wood = new Resource(Resource.ResourceType.WOOD);
    private Resource stone = new Resource(Resource.ResourceType.STONE);
    private Resource food = new Resource(Resource.ResourceType.FOOD);
    private Resource clay = new Resource(Resource.ResourceType.CLAY);

    public Village() {
        super();
    }

    public void setup() {
        addBehaviour(new PassiveBehaviour(this));
        addBehaviour(new ConsumingBehaviour(this));
        //addBehaviour(new AwaitTrades());
        //addBehaviour(new RequestTrades());
    }

    public Resource getWood() {
        return wood;
    }

    public Resource getStone() {
        return stone;
    }

    public Resource getFood() {
        return food;
    }

    public Resource getClay() {
        return clay;
    }

    public LinkedList<Resource> getResources() {
        return new LinkedList<>(Arrays.asList(this.wood, this.stone, this.food, this.clay));
    }

    public Resource getMostDepletedResource() {
        return this.getResources().stream().min(Comparator.comparing(Resource::getAmount)).orElse(null);
    }

    public AMSAgentDescription[] findVillages() {

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

            for (AMSAgentDescription a : findVillages()) {
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
