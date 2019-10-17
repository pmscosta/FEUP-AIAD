package behaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public interface HandlerInterface {

    public void handleReceivedTrade(ACLMessage msg) throws UnreadableException, IOException;

    public void handleConfirmedTrade(ACLMessage msg) throws UnreadableException;

    default void dispatcher(ACLMessage msg)throws IOException, UnreadableException {

        switch (msg.getPerformative()) {
            case ACLMessage.PROPOSE:
                handleReceivedTrade(msg);
                break;
            case ACLMessage.ACCEPT_PROPOSAL:
                handleConfirmedTrade(msg);
                break;
            default:
                System.out.println("UNRECOGNIZED MESSAGE");
                break;
        }

    }
}
