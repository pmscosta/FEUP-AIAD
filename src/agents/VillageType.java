package agents;

import java.util.Arrays;

public enum VillageType {
    PASSIVE,
    AGGRESSIVE;

    public static VillageType getVillageType(String type) {
        return Arrays.stream(VillageType.values()).filter(v_type -> v_type.name().equals(type)).findFirst().orElse(null);
    }
}
