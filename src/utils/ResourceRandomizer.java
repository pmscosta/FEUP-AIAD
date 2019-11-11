package utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ResourceRandomizer {
    public static final int getRandomResourceProductionRate(int resource_consumption) {
        return ThreadLocalRandom.current().nextInt(resource_consumption + 5, resource_consumption + 20);
    }

    public static final List<Resource> randomizeProduction(int resource_consumption) {
        List<Resource.ResourceType> resource_types = Arrays.asList(Resource.ResourceType.values());
        int num_resources = ThreadLocalRandom.current().nextInt(1, resource_types.size());
        Collections.shuffle(resource_types);
        return resource_types
                .subList(0, num_resources)
                .stream()
                .map(type -> new Resource(type, getRandomResourceProductionRate(resource_consumption)))
                .collect(Collectors.toList());
    }
}
