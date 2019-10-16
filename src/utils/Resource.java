package utils;

import exceptions.NotEnoughResources;

public class Resource {

    private static final int DEFAULT_AMOUNT = 1000;

    public enum ResourceType {
        WOOD,
        FOOD,
        STONE,
        CLAY
    };

    private ResourceType type;

    private int amount;

    public Resource(ResourceType type){
        this(type, DEFAULT_AMOUNT);
    }

    public Resource(ResourceType type, int amount){
        this.type = type;
        this.amount = amount;
    }

    public int getAmount(){
        return this.amount;
    }

    public void consumeAmount(Integer sub) throws NotEnoughResources {

        if(this.amount - sub < 0)
            throw new NotEnoughResources("Tried to consume " + sub + " of " + this.type + " but only has " + this.amount);
        else
            this.amount -= sub;

    }

    public String toString(){
        return "Resource: " + this.type.name() + " : " +  this.amount;
    }
}
