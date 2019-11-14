package utils;

import java.io.Serializable;

public final class Trade implements Serializable {
    private final Resource request;
    private final Resource offer;
    private final double ratio;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Trade(Resource request, Resource offer) {
        this.request = request;
        this.offer = offer;
        this.ratio = request.getAmount() / offer.getAmount();
    }

    public Resource getOffer() {
        return offer;
    }

    public Resource getRequest() {
        return request;
    }

    public double getRatio(boolean is_proposer) {
        return is_proposer ? ratio : 1/ratio;
    }

    @Override
    public String toString() {
        return String.format("Offering '%s' - Want '%s'", this.offer, this.request);
    }
}

