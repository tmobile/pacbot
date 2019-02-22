package com.tmobile.cso.pacman.inventory.vo;

import java.util.List;

public class SQSVH {

    private String queueUrl;
    
    private SQS sqs;

    private List<Attribute> tags;

    public SQSVH(String queueUrl, SQS sqs, List<Attribute> tags) {
        super();
        this.queueUrl = queueUrl;
        this.sqs = sqs;
        this.tags = tags;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public SQS getSqsAttributes() {
        return sqs;
    }

    public void setSqsAttributes(SQS sqsAttributes) {
        this.sqs = sqsAttributes;
    }

    public List<Attribute> getTags() {
        return tags;
    }

    public void setTags(List<Attribute> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "SQSVH [queueUrl=" + queueUrl + ", sqs=" + sqs + ", tags=" + tags + "]";
    }
}
