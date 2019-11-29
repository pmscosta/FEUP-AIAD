package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IndependentVariables {
    private static IndependentVariables instance;

    public int passive_num_villages;
    public int greedy_num_villages;
    public int smart_num_villages;

    public int passive_init_resources;
    public int greedy_init_resources;
    public int smart_init_resources;

    public int passive_resource_consumption;
    public int greedy_resource_consumption;
    public int smart_resource_consumption;

    public int passive_stone_production;
    public int passive_wood_production;
    public int passive_clay_production;
    public int passive_food_production;
    public int greedy_stone_production;
    public int greedy_wood_production;
    public int greedy_clay_production;
    public int greedy_food_production;
    public int smart_stone_production;
    public int smart_wood_production;
    public int smart_clay_production;
    public int smart_food_production;

    public int attacker_attack_percentage;

    private IndependentVariables() {}

    public static IndependentVariables getInstance() {
        if (instance == null) {
            instance = new IndependentVariables();
        }
        return instance;
    }

    public void export() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("independent_variables.csv"));

        writer.write(
                String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                        passive_num_villages,
                        greedy_num_villages,
                        smart_num_villages,

                        passive_init_resources,
                        greedy_init_resources,
                        smart_init_resources,

                        passive_resource_consumption,
                        greedy_resource_consumption,
                        smart_resource_consumption,

                        passive_clay_production,
                        passive_food_production,
                        passive_stone_production,
                        passive_wood_production,
                        greedy_clay_production,
                        greedy_food_production,
                        greedy_stone_production,
                        greedy_wood_production,
                        smart_clay_production,
                        smart_food_production,
                        smart_stone_production,
                        smart_wood_production,

                        attacker_attack_percentage
                )
        );



        writer.close();
    }

}
