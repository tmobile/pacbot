package com.tmobile.cso.pacman.inventory.vo;

import java.util.List;

import com.amazonaws.services.kinesisvideo.model.StreamInfo;

public class VideoStreamVH {

    private StreamInfo streamInfo;
    
    private List<Attribute> tags;

    public VideoStreamVH(StreamInfo streamInfo, List<Attribute> tags) {
        super();
        this.streamInfo = streamInfo;
        this.tags = tags;
    }

    public StreamInfo getStreamInfo() {
        return streamInfo;
    }

    public void setStreamInfo(StreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    public List<Attribute> getTags() {
        return tags;
    }

    public void setTags(List<Attribute> tags) {
        this.tags = tags;
    }
}
