package utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Trade implements Serializable {
    private final List<Resource> request;
    private final List<Resource> offer;

    public Trade(Resource request, Resource offer) {
        this(Arrays.asList(request), Arrays.asList(offer));
    }

    public Trade(Resource request, List<Resource> offer) {
        this(Arrays.asList(request), offer);
    }

    public Trade(List<Resource> request, Resource offer) {
        this(request, Arrays.asList(offer));
    }

    public Trade(List<Resource> request, List<Resource> offer) {
        this.request = request;
        this.offer = offer;
    }

    public List<Resource> getOffer() {
        return offer;
    }

    public List<Resource> getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return "Request: " + request.toString() + "\tOffer: " + offer.toString();
    }
}

