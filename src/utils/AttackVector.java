package utils;

import java.io.Serializable;
import java.util.List;

public class AttackVector implements Serializable {
    private final int attacked_resources_percentage;

    public AttackVector(int attacked_resources_percentage) {
        this.attacked_resources_percentage = attacked_resources_percentage;
    }

    public int getVector() {
        return attacked_resources_percentage;
    }
}
