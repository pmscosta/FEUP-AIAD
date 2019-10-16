package utils;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;

public class Printer {

    public void printAgents(AMSAgentDescription[] agents) {
        for (int i = 0; i < agents.length; i++) {
            AID agentID = agents[i].getName();
            System.out.println(i + ": " + agentID.getName());
        }
    }
}
