package behaviour;

import agents.Village;
import exceptions.NotEnoughResources;
import utils.Resource;
import utils.ResourceLogger;

import static utils.Printer.safePrintf;


public class LifeCycleBehaviour extends TimeTickerBehaviour {

    public LifeCycleBehaviour(Village village) {
        super(village);
    }

    public void consumeResources() {
        for (Resource r : this.village.getResources().values()) {
            try {
                r.consumeAmount(village.getResourceConsumption());
            } catch (NotEnoughResources e) {
                safePrintf(e.toString());
                this.village.doDelete();
            }
        }
    }

    public void produceResources() {
        for (Resource produced_resource : this.village.getProductionResources()) {
            village.getResources().get(produced_resource.getType()).produceAmount(produced_resource.getAmount());
        }
    }

    public void proposeTrades() {
        for (Resource r : village.getResources().values()) {
            if (village.shouldProposeTrade(r)) {
                village.proposeTrade(r);
            }
        }
    }

    public void logStuff() {
        ResourceLogger.getInstance().add(String.format(
                "%d %s %d %d %d %d (%d)\n",
                this.village.tick_num++,
                this.village.getVillageName(),
                this.village.getResources().get(Resource.ResourceType.STONE).getAmount(),
                this.village.getResources().get(Resource.ResourceType.WOOD).getAmount(),
                this.village.getResources().get(Resource.ResourceType.FOOD).getAmount(),
                this.village.getResources().get(Resource.ResourceType.CLAY).getAmount(),
                this.village.getResources().get(Resource.ResourceType.STONE).getAmount()+
                        this.village.getResources().get(Resource.ResourceType.WOOD).getAmount()+
                        this.village.getResources().get(Resource.ResourceType.FOOD).getAmount()+
                        this.village.getResources().get(Resource.ResourceType.CLAY).getAmount()
        ));

        safePrintf("%s: %s-(%d) %s-(%d) %s-(%d) %s-(%d)", this.village.getVillageName(),
                Resource.ResourceType.STONE,
                this.village.getResources().get(Resource.ResourceType.STONE).getAmount(),
                Resource.ResourceType.WOOD,
                this.village.getResources().get(Resource.ResourceType.WOOD).getAmount(),
                Resource.ResourceType.FOOD,
                this.village.getResources().get(Resource.ResourceType.FOOD).getAmount(),
                Resource.ResourceType.CLAY,
                this.village.getResources().get(Resource.ResourceType.CLAY).getAmount()
        );
    }

    @Override
    protected void onTick() {
        consumeResources();
        produceResources();

        logStuff();

        proposeTrades();
    }
}
