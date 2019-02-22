package com.tmobile.cso.pacman.inventory.vo;

import java.util.List;

import com.amazonaws.services.kinesis.model.StreamDescription;
import com.amazonaws.services.kinesis.model.Tag;

public class DataStreamVH {

    private StreamDescription streamDescription;
    
    private List<Tag> tags;

    public DataStreamVH(StreamDescription streamDescription, List<Tag> tags) {
        super();
        this.streamDescription = streamDescription;
        this.tags = tags;
    }

    public StreamDescription getStreamDescription() {
        return streamDescription;
    }

    public void setStreamDescription(StreamDescription streamDescription) {
        this.streamDescription = streamDescription;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
