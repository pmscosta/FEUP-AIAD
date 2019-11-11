package utils;

import java.io.Serializable;

public class Trade implements Serializable {
    private final Resource request;
    private final Resource offer;

    public Trade(Resource request, Resource offer) {
        this.request = request;
        this.offer = offer;
    }

    public Resource getOffer() {
        return offer;
    }

    public Resource getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return String.format("Offering '%s' - Want '%s'", this.offer, this.request);
    }
}

