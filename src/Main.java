import agents.ListeningAgent;
import agents.Village;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {

    public static void main(String[] args) throws StaleProxyException {

        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(p1);

        AgentController ac1 = mainContainer.acceptNewAgent("name1", new Village());
        ac1.start();

        AgentController ac2 = mainContainer.acceptNewAgent("name2", new Village());
        ac2.start();

    }

}

