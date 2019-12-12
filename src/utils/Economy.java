package utils;

import java.util.ArrayList;

public class Economy {
    private static int amount_traded_resources = 0;
    private static ArrayList<Integer> resources_record = new ArrayList<>();
    private static int num_terminated_villages = 0;

    public static synchronized void terminateVillage() {
        num_terminated_villages++;
    }

    public static int getPercentageOfLivingVillages() {
        int total_num_villages = IndependentVariables.getInstance().passive_num_villages
                + IndependentVariables.getInstance().greedy_num_villages
                + IndependentVariables.getInstance().smart_num_villages;

        int num_living_villages = total_num_villages - num_terminated_villages;

        return (int) Math.round(((double) num_living_villages) / ((double)total_num_villages) * 100);
    }

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
