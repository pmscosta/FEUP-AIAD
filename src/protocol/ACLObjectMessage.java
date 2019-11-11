package protocol;

import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

public class ACLObjectMessage extends ACLMessage {
    public ACLObjectMessage(int performative, Serializable s) throws IOException {
        super(performative);
        this.setContentObject(s);
    }
}
