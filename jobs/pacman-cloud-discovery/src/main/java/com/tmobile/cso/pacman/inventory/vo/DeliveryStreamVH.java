package com.tmobile.cso.pacman.inventory.vo;

import java.util.List;

import com.amazonaws.services.kinesisfirehose.model.DeliveryStreamDescription;
import com.amazonaws.services.kinesisfirehose.model.DestinationDescription;
import com.amazonaws.services.kinesisfirehose.model.Tag;

public class DeliveryStreamVH {

    private DeliveryStreamDescription deliveryStreamDescription;
    
    private DestinationDescription destinationDescription;
    
    private List<Tag> tags;

    public DeliveryStreamVH(DeliveryStreamDescription deliveryStreamDescription,
            DestinationDescription destinationDescription,List<Tag> tags) {
        super();
        this.deliveryStreamDescription = deliveryStreamDescription;
        this.destinationDescription = destinationDescription;
        this.tags = tags;
    }

    public DeliveryStreamDescription getDeliveryStreamDescription() {
        return deliveryStreamDescription;
    }

    public void setDeliveryStreamDescription(DeliveryStreamDescription deliveryStreamDescription) {
        this.deliveryStreamDescription = deliveryStreamDescription;
    }

    public DestinationDescription getDestinationDescription() {
        return destinationDescription;
    }

    public void setDestinationDescription(DestinationDescription destinationDescription) {
        this.destinationDescription = destinationDescription;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
