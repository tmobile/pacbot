package com.tmobile.cso.pacman.inventory.vo;

public class SQS {

    private String QueueArn;
    private String Policy;
    private String ApproximateNumberOfMessagesDelayed;
    private String ReceiveMessageWaitTimeSeconds;
    private String CreatedTimestamp;
    private String DelaySeconds;
    private String MessageRetentionPeriod;
    private String MaximumMessageSize;
    private String VisibilityTimeout;
    private String ApproximateNumberOfMessages;
    private String ApproximateNumberOfMessagesNotVisible;
    private String LastModifiedTimestamp;
    private String KmsMasterKeyId;
    private String KmsDataKeyReusePeriodSeconds;
    private String FifoQueue ;
    private String ContentBasedDeduplication ;
    private String RedrivePolicy   ;

    public String getQueueArn() {
        return QueueArn;
    }

    public void setQueueArn(String queueArn) {
        QueueArn = queueArn;
    }

    public String getPolicy() {
        return Policy;
    }

    public void setPolicy(String policy) {
        Policy = policy;
    }

    public String getApproximateNumberOfMessagesDelayed() {
        return ApproximateNumberOfMessagesDelayed;
    }

    public void setApproximateNumberOfMessagesDelayed(String approximateNumberOfMessagesDelayed) {
        ApproximateNumberOfMessagesDelayed = approximateNumberOfMessagesDelayed;
    }

    public String getReceiveMessageWaitTimeSeconds() {
        return ReceiveMessageWaitTimeSeconds;
    }

    public void setReceiveMessageWaitTimeSeconds(String receiveMessageWaitTimeSeconds) {
        ReceiveMessageWaitTimeSeconds = receiveMessageWaitTimeSeconds;
    }

    public String getCreatedTimestamp() {
        return CreatedTimestamp;
    }

    public void setCreatedTimestamp(String createdTimestamp) {
        CreatedTimestamp = createdTimestamp;
    }

    public String getDelaySeconds() {
        return DelaySeconds;
    }

    public void setDelaySeconds(String delaySeconds) {
        DelaySeconds = delaySeconds;
    }

    public String getMessageRetentionPeriod() {
        return MessageRetentionPeriod;
    }

    public void setMessageRetentionPeriod(String messageRetentionPeriod) {
        MessageRetentionPeriod = messageRetentionPeriod;
    }

    public String getMaximumMessageSize() {
        return MaximumMessageSize;
    }

    public void setMaximumMessageSize(String maximumMessageSize) {
        MaximumMessageSize = maximumMessageSize;
    }

    public String getVisibilityTimeout() {
        return VisibilityTimeout;
    }

    public void setVisibilityTimeout(String visibilityTimeout) {
        VisibilityTimeout = visibilityTimeout;
    }

    public String getApproximateNumberOfMessages() {
        return ApproximateNumberOfMessages;
    }

    public void setApproximateNumberOfMessages(String approximateNumberOfMessages) {
        ApproximateNumberOfMessages = approximateNumberOfMessages;
    }

    public String getApproximateNumberOfMessagesNotVisible() {
        return ApproximateNumberOfMessagesNotVisible;
    }

    public void setApproximateNumberOfMessagesNotVisible(String approximateNumberOfMessagesNotVisible) {
        ApproximateNumberOfMessagesNotVisible = approximateNumberOfMessagesNotVisible;
    }

    public String getLastModifiedTimestamp() {
        return LastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(String lastModifiedTimestamp) {
        LastModifiedTimestamp = lastModifiedTimestamp;
    }
    
    public String getKmsMasterKeyId() {
        return KmsMasterKeyId;
    }

    public void setKmsMasterKeyId(String kmsMasterKeyId) {
        KmsMasterKeyId = kmsMasterKeyId;
    }

    public String getKmsDataKeyReusePeriodSeconds() {
        return KmsDataKeyReusePeriodSeconds;
    }

    public void setKmsDataKeyReusePeriodSeconds(String kmsDataKeyReusePeriodSeconds) {
        KmsDataKeyReusePeriodSeconds = kmsDataKeyReusePeriodSeconds;
    }

    public String getFifoQueue() {
        return FifoQueue;
    }

    public void setFifoQueue(String fifoQueue) {
        FifoQueue = fifoQueue;
    }

    public String getContentBasedDeduplication() {
        return ContentBasedDeduplication;
    }

    public void setContentBasedDeduplication(String contentBasedDeduplication) {
        ContentBasedDeduplication = contentBasedDeduplication;
    }

    public String getRedrivePolicy() {
        return RedrivePolicy;
    }

    public void setRedrivePolicy(String redrivePolicy) {
        RedrivePolicy = redrivePolicy;
    }

    @Override
    public String toString() {
        return "SQS [QueueArn=" + QueueArn + ", Policy=" + Policy + ", ApproximateNumberOfMessagesDelayed="
                + ApproximateNumberOfMessagesDelayed + ", ReceiveMessageWaitTimeSeconds="
                + ReceiveMessageWaitTimeSeconds + ", CreatedTimestamp=" + CreatedTimestamp + ", DelaySeconds="
                + DelaySeconds + ", MessageRetentionPeriod=" + MessageRetentionPeriod + ", MaximumMessageSize="
                + MaximumMessageSize + ", VisibilityTimeout=" + VisibilityTimeout + ", ApproximateNumberOfMessages="
                + ApproximateNumberOfMessages + ", ApproximateNumberOfMessagesNotVisible="
                + ApproximateNumberOfMessagesNotVisible + ", LastModifiedTimestamp=" + LastModifiedTimestamp
                + ", KmsMasterKeyId=" + KmsMasterKeyId + ", KmsDataKeyReusePeriodSeconds="
                + KmsDataKeyReusePeriodSeconds + ", FifoQueue=" + FifoQueue + ", ContentBasedDeduplication="
                + ContentBasedDeduplication + ", RedrivePolicy=" + RedrivePolicy + "]";
    }
}
