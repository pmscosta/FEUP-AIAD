package utils;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;

public class Economy {
    private static int amount_traded_resources = 0;
    private static ArrayList<Integer> resources_record = new ArrayList<>();

    public static synchronized void tradeResources(int amount) {
        amount_traded_resources += amount;
    }

    public static Integer getLast(){
        return resources_record.get(resources_record.size()-1);
    }

    public static void saveRecord() {
        resources_record.add(amount_traded_resources);
    }

    public static int getAmountTradedResources() {
        return amount_traded_resources;
    }

    static ArrayList<Integer> getResourcesRecord() {
        return resources_record;
    }
}
