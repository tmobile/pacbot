package com.tmobile.cso.pacman.inventory.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.ListStreamsResult;
import com.amazonaws.services.kinesis.model.ListTagsForStreamRequest;
import com.amazonaws.services.kinesis.model.ListTagsForStreamResult;
import com.amazonaws.services.kinesis.model.StreamDescription;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClientBuilder;
import com.amazonaws.services.kinesisfirehose.model.DeliveryStreamDescription;
import com.amazonaws.services.kinesisfirehose.model.DescribeDeliveryStreamRequest;
import com.amazonaws.services.kinesisfirehose.model.ListDeliveryStreamsRequest;
import com.amazonaws.services.kinesisfirehose.model.ListDeliveryStreamsResult;
import com.amazonaws.services.kinesisfirehose.model.ListTagsForDeliveryStreamRequest;
import com.amazonaws.services.kinesisfirehose.model.ListTagsForDeliveryStreamResult;
import com.amazonaws.services.kinesisfirehose.model.Tag;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideo;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoClientBuilder;
import com.amazonaws.services.kinesisvideo.model.ListStreamsRequest;
import com.amazonaws.services.kinesisvideo.model.StreamInfo;
import com.tmobile.cso.pacman.inventory.InventoryConstants;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.FileGenerator;
import com.tmobile.cso.pacman.inventory.vo.Attribute;
import com.tmobile.cso.pacman.inventory.vo.DataStreamVH;
import com.tmobile.cso.pacman.inventory.vo.DeliveryStreamVH;
import com.tmobile.cso.pacman.inventory.vo.VideoStreamVH;

public class KinesisInventoryUtil {
    
    /** The log. */
    private static Logger log = LoggerFactory.getLogger(KinesisInventoryUtil.class);
    
    /** The delimiter. */
    private static String delimiter = FileGenerator.DELIMITER;

    public static Map<String,List<DataStreamVH>> fetchDataStreamInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName) {
        
        Map<String,List<DataStreamVH>> dataStream = new LinkedHashMap<>();
        AmazonKinesis amazonKinesis;
        String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource\" ,\"type\": \"datastream\"" ;
        for(Region region : RegionUtils.getRegions()) { 
            try{
                if(!skipRegions.contains(region.getName())){
                    amazonKinesis = AmazonKinesisClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
                    ListStreamsResult listStreamsResult = amazonKinesis.listStreams();
                    List<String> streamNamesTemp = listStreamsResult.getStreamNames();
                    List<String> streamNames = new ArrayList<>(streamNamesTemp);
                    while (listStreamsResult.isHasMoreStreams() && !streamNamesTemp.isEmpty()) {
                        listStreamsResult = amazonKinesis.listStreams(streamNamesTemp.get(streamNamesTemp.size() - 1));
                        streamNamesTemp = listStreamsResult.getStreamNames();
                        streamNames.addAll(streamNamesTemp);
                    }
                    
                    List<DataStreamVH> dataStreamList = new ArrayList<>();
                    for(String streamName : streamNames) {
                        StreamDescription streamDescription = amazonKinesis.describeStream(new DescribeStreamRequest().withStreamName(streamName)).getStreamDescription();
                        ListTagsForStreamResult listTagsForStreamResult = amazonKinesis.listTagsForStream(new ListTagsForStreamRequest().withStreamName(streamName));
                        List<com.amazonaws.services.kinesis.model.Tag> tagsTemp = listTagsForStreamResult.getTags();
                        List<com.amazonaws.services.kinesis.model.Tag> tags = new ArrayList<>(tagsTemp);
                        while (listTagsForStreamResult.isHasMoreTags() && !tagsTemp.isEmpty()) {
                            listTagsForStreamResult = amazonKinesis.listTagsForStream(new ListTagsForStreamRequest().withExclusiveStartTagKey(tagsTemp.get(tagsTemp.size() - 1).getKey()));
                            tagsTemp = listTagsForStreamResult.getTags();
                            tags.addAll(tagsTemp);
                        }
                        dataStreamList.add(new DataStreamVH(streamDescription, tags));
                    }
                    if( !dataStreamList.isEmpty() ) {
                        log.debug(InventoryConstants.ACCOUNT + accountId +" Type : datastream "+region.getName() + " >> "+dataStreamList.size());
                        dataStream.put(accountId+delimiter+accountName+delimiter+region.getName(),dataStreamList);
                    }
                }
            } catch(Exception e){
                log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
                ErrorManageUtil.uploadError(accountId, region.getName(),"datastream",e.getMessage());
            }
        }
        return dataStream;
    }
    
    public static Map<String,List<DeliveryStreamVH>> fetchDeliveryStreamInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName) {
        
        Map<String,List<DeliveryStreamVH>> deliveryStream = new LinkedHashMap<>();
        AmazonKinesisFirehose amazonKinesisFirehose;
        String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource\" ,\"type\": \"deliverystream\"" ;
        for(Region region : RegionUtils.getRegions()) { 
            try{
                if(!skipRegions.contains(region.getName())){
                    amazonKinesisFirehose = AmazonKinesisFirehoseClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
                    ListDeliveryStreamsResult listDeliveryStreamsResult = amazonKinesisFirehose.listDeliveryStreams(new ListDeliveryStreamsRequest().withLimit(100));
                    List<String> deliveryStreamNamesTemp = listDeliveryStreamsResult.getDeliveryStreamNames();
                    List<String> deliveryStreamNames = new ArrayList<>(deliveryStreamNamesTemp);
                    while (listDeliveryStreamsResult.isHasMoreDeliveryStreams() && !deliveryStreamNamesTemp.isEmpty()) {
                        listDeliveryStreamsResult = amazonKinesisFirehose.listDeliveryStreams(new ListDeliveryStreamsRequest().withExclusiveStartDeliveryStreamName(deliveryStreamNamesTemp.get(deliveryStreamNamesTemp.size() - 1)).withLimit(100));
                        deliveryStreamNamesTemp = listDeliveryStreamsResult.getDeliveryStreamNames();
                        deliveryStreamNames.addAll(deliveryStreamNamesTemp);
                    }
                    
                    List<DeliveryStreamVH> deliveryStreamList = new ArrayList<>();
                    for(String deliveryStreamName : deliveryStreamNames) {
                        DeliveryStreamDescription deliveryStreamDescription = amazonKinesisFirehose.describeDeliveryStream(new DescribeDeliveryStreamRequest().withDeliveryStreamName(deliveryStreamName).withLimit(100)).getDeliveryStreamDescription();
                        ListTagsForDeliveryStreamResult listTagsForDeliveryStreamResult = amazonKinesisFirehose.listTagsForDeliveryStream(new ListTagsForDeliveryStreamRequest().withDeliveryStreamName(deliveryStreamName));
                        List<Tag> tagsTemp = listTagsForDeliveryStreamResult.getTags();
                        List<Tag> tags = new ArrayList<>(tagsTemp);
                        while (listTagsForDeliveryStreamResult.isHasMoreTags() && !tagsTemp.isEmpty()) {
                            listTagsForDeliveryStreamResult = amazonKinesisFirehose.listTagsForDeliveryStream(new ListTagsForDeliveryStreamRequest().withExclusiveStartTagKey(tagsTemp.get(tagsTemp.size() - 1).getKey()));
                            tagsTemp = listTagsForDeliveryStreamResult.getTags();
                            tags.addAll(tagsTemp);
                        }
                        if(deliveryStreamDescription.getDestinations().isEmpty()) {
                            deliveryStreamList.add(new DeliveryStreamVH(deliveryStreamDescription,null, tags));
                        } else {
                            deliveryStreamList.add(new DeliveryStreamVH(deliveryStreamDescription,deliveryStreamDescription.getDestinations().get(0), tags));
                        }
                        
                    }
                    if( !deliveryStreamList.isEmpty() ) {
                        log.debug(InventoryConstants.ACCOUNT + accountId +" Type : deliverystream "+region.getName() + " >> "+deliveryStreamList.size());
                        deliveryStream.put(accountId+delimiter+accountName+delimiter+region.getName(),deliveryStreamList);
                    }
                }
            } catch(Exception e){
                log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
                ErrorManageUtil.uploadError(accountId, region.getName(),"deliverystream",e.getMessage());
            }
        }
        return deliveryStream;
    }
    
    public static Map<String,List<VideoStreamVH>> fetchVideoStreamInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName) {
        
        Map<String,List<VideoStreamVH>> videoStream = new LinkedHashMap<>();
        AmazonKinesisVideo amazonKinesisVideo;
        String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource\" ,\"type\": \"videoStream\" " ;
        for(Region region : RegionUtils.getRegions()) { 
            try{
                if(!skipRegions.contains(region.getName()) && region.isServiceSupported(AmazonKinesisVideo.ENDPOINT_PREFIX)){
                    amazonKinesisVideo = AmazonKinesisVideoClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
                    List<StreamInfo> videoStreamListTemp = new ArrayList<>();
                    com.amazonaws.services.kinesisvideo.model.ListStreamsResult listStreamsResult;
                    String nextToken = null;
                    do{
                        listStreamsResult = amazonKinesisVideo.listStreams(new ListStreamsRequest().withNextToken(nextToken));
                        videoStreamListTemp.addAll(listStreamsResult.getStreamInfoList());
                        nextToken = listStreamsResult.getNextToken();
                    }while(nextToken!=null);
                    
                    List<VideoStreamVH> videoStreamList = new ArrayList<>();
                    for(StreamInfo streamInfo : videoStreamListTemp) {
                        List<Attribute> tags = new ArrayList<>();
                        for(Entry<String, String> entry: amazonKinesisVideo.listTagsForStream(new com.amazonaws.services.kinesisvideo.model.ListTagsForStreamRequest()
                                .withStreamARN(streamInfo.getStreamARN())).getTags().entrySet()) {
                            tags.add(new Attribute(entry.getKey(), entry.getValue()));
                        }
                        videoStreamList.add(new VideoStreamVH(streamInfo,tags));
                    }
                    
                    if( !videoStreamList.isEmpty() ) {
                        log.debug(InventoryConstants.ACCOUNT + accountId +" Type : VideoStream "+region.getName() + " >> "+videoStreamList.size());
                        videoStream.put(accountId+delimiter+accountName+delimiter+region.getName(),videoStreamList);
                    }
                }
            } catch(Exception e){
                log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
                ErrorManageUtil.uploadError(accountId, region.getName(),"videoStream",e.getMessage());
               
            }
        }
        return videoStream;
    }
}
