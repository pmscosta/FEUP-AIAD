package behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public interface Handler {

    public void handleReceivedTrade(ACLMessage msg) throws UnreadableException, IOException;

    public void handleConfirmedTrade(ACLMessage msg) throws UnreadableException;
}
