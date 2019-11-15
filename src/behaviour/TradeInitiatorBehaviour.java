package behaviour;

import agents.Village;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import protocol.ACLObjectMessage;
import utils.Trade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * All steps done by the trade initiator
 */
public class TradeInitiatorBehaviour extends Behaviour {

    enum TradeStep {
        BROADCAST, AWAIT_COUNTER_OFFERS, DECIDE_BEST_OFFER, AWAIT_FINAL_CONFIRMATION, DONE
    }

    private TradeStep trade_step = TradeStep.BROADCAST;
    private final Trade trade;
    private final String trade_id;
    private int num_contacted_villages = 0;
    private int num_counter_offer_replies = 0;

    private static final long MESSAGE_WAITING_TIMEOUT = 200L;

    List<ACLMessage> propose_messages = new ArrayList<>();

    private final MessageTemplate await_counter_offer_mt;

    private final MessageTemplate await_final_confirmation_mt;

    public TradeInitiatorBehaviour(Agent agent, Trade trade) {
        super(agent);
        this.trade = trade;
        this.trade_id = String.format("%s-%s-%s-%d",
                ((Village) getAgent()).getVillageName(),
                trade.getRequest().getType(),
                trade.getOffer().getType(),
                System.currentTimeMillis()
        );

        await_counter_offer_mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId(this.trade_id),
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)
        );

        await_final_confirmation_mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId(this.trade_id),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );
    }

    @Override
    public void action() {
        switch (trade_step) {
            case BROADCAST:
                broadcastTrade();
                break;
            case AWAIT_COUNTER_OFFERS:
                awaitCounterOffers();
                break;
            case DECIDE_BEST_OFFER:
                decideBestOffer();
                break;
            case AWAIT_FINAL_CONFIRMATION:
                awaitFinalConfirmation();
                break;
            default:
                break;
        }
    }

    private void broadcastTrade() {
        Village village = ((Village) getAgent());

        try {
            ACLObjectMessage msg = new ACLObjectMessage(ACLMessage.CFP, trade);
            msg.setConversationId(trade_id);

            AMSAgentDescription[] other_villages = village.findVillages();
            num_contacted_villages = other_villages.length;

            for (AMSAgentDescription ad : other_villages) {
                msg.addReceiver(ad.getName());
            }

            if (num_contacted_villages == 0) {
                this.trade_step = TradeStep.DONE;
            }

            this.myAgent.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.trade_step = TradeStep.AWAIT_COUNTER_OFFERS;
    }

    private void awaitCounterOffers() {
        while(num_counter_offer_replies < num_contacted_villages) {
            ACLMessage msg = this.myAgent.blockingReceive(await_counter_offer_mt, MESSAGE_WAITING_TIMEOUT);

            if (msg == null) {
                break;
            }

            propose_messages.add(msg);
            num_counter_offer_replies++;
        }

        if (num_counter_offer_replies == 0)   {
            this.trade_step = TradeStep.DONE;
        } else {
            this.trade_step = TradeStep.DECIDE_BEST_OFFER;
        }
    }

    private void decideBestOffer() {
        Village village = ((Village) this.getAgent());

        List<Trade> propose_trades = new ArrayList<>();
        for (ACLMessage message : propose_messages) {
            try {
                propose_trades.add((Trade) message.getContentObject());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }
        int accepted_index = village.selectBestTrade(propose_trades);

        for(int i = 0; i < propose_messages.size(); i++){
            ACLMessage msg = propose_messages.get(i);
            ACLMessage reply = msg.createReply();

            try {
                if (i == accepted_index) {
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    village.accountForNewTrade(propose_trades.get(accepted_index).getOffer());
                } else {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }

                reply.setContentObject(msg.getContentObject());
                this.myAgent.send(reply);
            } catch (IOException | UnreadableException e) {
                e.printStackTrace();
            }
        }

        this.trade_step = TradeStep.AWAIT_FINAL_CONFIRMATION;
    }

    private void awaitFinalConfirmation() {
        ACLMessage msg = this.myAgent.receive(await_final_confirmation_mt);
        Village village = ((Village) this.getAgent());
        if (msg != null) {
            try {
                village.applyTrade((Trade) msg.getContentObject(), true);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            this.trade_step = TradeStep.DONE;
        }
        else {
            block();
        }
    }


    @Override
    public boolean done() {
        return this.trade_step == TradeStep.DONE;
    }
}
