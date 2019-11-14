package utils;

import java.io.Serializable;
import java.util.List;

public class AttackVector implements Serializable {
    private final List<Resource> vector;

    public AttackVector(List<Resource> vector) {
        this.vector = vector;
    }

    public List<Resource> getVector() {
        return vector;
    }
}
