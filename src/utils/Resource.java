package utils;

import exceptions.NotEnoughResources;

import java.io.Serializable;

public class Resource implements Serializable, Comparable {

    public static final int DEFAULT_AMOUNT = 50;
    private ResourceType type;

    private int amount;
    public Resource(ResourceType type) {
        this(type, DEFAULT_AMOUNT);
    }

    public Resource(ResourceType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    public ResourceType getType() {
        return type;
    }

    public void consumeAmount(int amount) throws NotEnoughResources {
        if (this.amount - amount < 0) {
            throw new NotEnoughResources("Tried to consume " + amount + " of " + this.type + " but only has " + this.amount);
        } else {
            this.amount -= amount;
        }
    }

    public void produceAmount(int amount) {
        this.amount += amount;
    }

    public String toString() {
        return String.format("%s: %s", this.type.name(), this.amount);
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.getAmount(), ((Resource) o).getAmount());
    }

    public enum ResourceType {
        CLAY,
        FOOD,
        STONE,
        WOOD,
    }
}
