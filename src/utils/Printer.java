package utils;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;

import java.util.concurrent.ConcurrentHashMap;

public class Printer {

    public void printAgents(AMSAgentDescription[] agents) {
        for (int i = 0; i < agents.length; i++) {
            AID agentID = agents[i].getName();
            safePrintf(i + ": " + agentID.getName());
        }
    }

    public static void safePrintf(String fmt, Object... args) {
        synchronized (System.out) {
            System.out.printf(fmt, args);
            System.out.println();
        }
    }

}
