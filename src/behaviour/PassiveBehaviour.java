package behaviour;

import agents.Village;
import utils.Resource;


public class PassiveBehaviour extends TimeTickerBehaviour {

    protected static final int RESOURCES_THRESHOLD = 100;

    public PassiveBehaviour(Village village) {
        super(village);
    }

    @Override
    protected void onTick() {

        System.out.println(village.getName());
        for(Resource r : village.getResources()){
            System.out.println(r);
        }

    }
}
