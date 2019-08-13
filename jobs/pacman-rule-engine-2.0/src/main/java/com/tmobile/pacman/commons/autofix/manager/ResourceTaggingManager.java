/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package com.tmobile.pacman.commons.autofix.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClient;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudtrail.model.OperationNotPermittedException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;
import com.amazonaws.services.ec2.model.DescribeTagsRequest;
import com.amazonaws.services.ec2.model.DescribeTagsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagDescription;
import com.amazonaws.services.elasticfilesystem.AmazonElasticFileSystem;
import com.amazonaws.services.elasticsearch.AWSElasticsearch;
import com.amazonaws.services.elasticsearch.model.AddTagsRequest;
import com.amazonaws.services.elasticsearch.model.AddTagsResult;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.AddTagsToResourceRequest;
import com.amazonaws.services.rds.model.AddTagsToResourceResult;
import com.amazonaws.services.redshift.AmazonRedshift;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.TagSet;
import com.google.common.collect.Maps;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.util.CommonUtils;
/**
 * The Class ResourceTaggingManager.
 *
 * @author kkumar
 */
public class ResourceTaggingManager {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ResourceTaggingManager.class);

    /**
     * Tag resource.
     *
     * @param resourceId
     *            the resource id
     * @param clientMap
     *            the client map
     * @param serviceType
     *            the service type
     * @param pacTag
     *            the pac tag
     * @return the boolean
     * @throws Exception
     *             the exception
     */
    public Boolean tagResource(final String resourceId, final Map<String, Object> clientMap, AWSService serviceType,
            Map<String, String> pacTag) throws Exception {

        switch (serviceType) {
        case S3: {
            try {

                AmazonS3 s3Client = (AmazonS3) clientMap.get("client");
                BucketTaggingConfiguration bucketTaggingConfiguration = s3Client
                        .getBucketTaggingConfiguration(resourceId);
                if (bucketTaggingConfiguration == null) {
                    saveTags(resourceId, new BucketTaggingConfiguration(Collections.singletonList(new TagSet(pacTag))),
                            s3Client);
                } else {
                    List<TagSet> existingTargets = bucketTaggingConfiguration.getAllTagSets();
                    Map<String, String> existingTags = existingTargets.get(0).getAllTags();
                    Map<String, String> allTags = Maps.newHashMap();

                    if (bucketAlreadyTaggedAndTagValueNotAltered(existingTags, pacTag))
                        return Boolean.TRUE;

                    allTags.putAll(existingTags);
                    allTags.putAll(pacTag);
                    bucketTaggingConfiguration.setTagSets(existingTargets);
                    saveTags(resourceId,
                            new BucketTaggingConfiguration(Collections.singletonList(new TagSet(allTags))), s3Client);
                }
                return Boolean.TRUE;
            } catch (Exception exception) {
                logger.error("error tagging bucekt - > " + resourceId, exception);
                throw exception;
            }


        }
        case EC2: {
            return setEC2VolumeTag(resourceId, clientMap, pacTag);
        }
        case SNAPSHOT: {
            return setEC2VolumeTag(resourceId, clientMap, pacTag);

        }
        case VOLUME: {
            return setEC2VolumeTag(resourceId, clientMap, pacTag);

        }
        case RDSDB: {
            return setRDSDBTag(resourceId,clientMap,pacTag);

        }
        case ELASTICSEARCH:{
            return setElasticSearchTag(resourceId,clientMap,pacTag);
        }
        case EFS:{
            return setEFSTag(resourceId,clientMap,pacTag);
        }
        case REDSHIFT:{
            return setRedshiftTag(resourceId,clientMap,pacTag);
        }
        default:
            throw new OperationNotPermittedException("this resource tagging is not imlemented yet");
        }
    }
    /**
     *
     * @param resourceId
     * @param clientMap
     * @param pacTag
     * @return
     */
    private Boolean setEFSTag(final String resourceId,
            final Map<String, Object> clientMap, Map<String, String> pacTag) {
        com.amazonaws.services.elasticfilesystem.model.Tag tag = new com.amazonaws.services.elasticfilesystem.model.Tag();
        for (Map.Entry<String, String> tags : pacTag.entrySet()) {
            tag.setKey(tags.getKey());
            tag.setValue(tags.getValue());
        }
        AmazonElasticFileSystem fileSystem = (AmazonElasticFileSystem) clientMap
                .get("client");
        com.amazonaws.services.elasticfilesystem.model.CreateTagsRequest createTagsRequest = new com.amazonaws.services.elasticfilesystem.model.CreateTagsRequest();
        createTagsRequest.setFileSystemId(resourceId);
        createTagsRequest.setTags(Arrays.asList(tag));
        try {
            fileSystem.createTags(createTagsRequest);
            return Boolean.TRUE;
        } catch (AmazonServiceException ase) {
            logger.error("error tagging efs - > " + resourceId, ase);
            throw ase;
        }
    }

    /**
     *
     * @param resourceId
     * @param clientMap
     * @param pacTag
     * @return
     */
        private Boolean setElasticSearchTag(final String resourceId, final Map<String, Object> clientMap,
                Map<String, String> pacTag) {
            AWSElasticsearch elasticsearch = (AWSElasticsearch) clientMap.get("client");

           com.amazonaws.services.elasticsearch.model.Tag tag = new com.amazonaws.services.elasticsearch.model.Tag();
            for(Map.Entry<String, String> tags : pacTag.entrySet()){
            tag.setKey(tags.getKey());
            tag.setValue(tags.getValue());
            }

            AddTagsRequest request = new AddTagsRequest().withARN(resourceId);
            request.setTagList(Arrays.asList(tag));
            try {
                AddTagsResult response = elasticsearch.addTags(request);
                return Boolean.TRUE;
            } catch (AmazonServiceException ase) {
                logger.error("error tagging Elastic Search - > " + resourceId, ase);
                throw ase;
            }
        }

/**
 *
 * @param resourceId
 * @param clientMap
 * @param pacTag
 * @return
 */
    private Boolean setRDSDBTag(final String resourceId, final Map<String, Object> clientMap,
        Map<String, String> pacTag
) {
    AmazonRDS rdsClient = (AmazonRDS) clientMap.get("client");
    com.amazonaws.services.rds.model.Tag tag = new com.amazonaws.services.rds.model.Tag();
    for(Map.Entry<String, String> tags : pacTag.entrySet()){
    tag.setKey(tags.getKey());
    tag.setValue(tags.getValue());
    }

    AddTagsToResourceRequest request = new AddTagsToResourceRequest().withResourceName(resourceId);
    request.setTags(Arrays.asList(tag));
    try {
        AddTagsToResourceResult response = rdsClient.addTagsToResource(request);
        return Boolean.TRUE;
    } catch (AmazonServiceException ase) {
        logger.error("error tagging rds - > " + resourceId, ase);
        throw ase;
    }

    }
/**
 *
 * @param resourceId
 * @param clientMap
 * @param pacTag
 * @return
 */
    private Boolean setEC2VolumeTag(final String resourceId, final Map<String, Object> clientMap,
            Map<String, String> pacTag) {
        AmazonEC2 ec2Client = (AmazonEC2) clientMap.get("client");
        CreateTagsRequest createTagsRequest = new CreateTagsRequest(Arrays.asList(resourceId), new ArrayList<>());
        createTagsRequest.setTags(pacTag.entrySet().stream().map(t -> new Tag(t.getKey(), t.getValue()))
                .collect(Collectors.toList()));
        try {
            ec2Client.createTags(createTagsRequest);
            return Boolean.TRUE;
        } catch (AmazonServiceException ase) {
            logger.error("error tagging ec2 - > " + resourceId, ase);
            throw ase;
        }
    }

    /**
     * Save tags.
     *
     * @param resourceId
     *            the resource id
     * @param bucketTaggingConfiguration
     *            the bucket tagging configuration
     * @param s3Client
     *            the s 3 client
     */
    private void saveTags(final String resourceId, final BucketTaggingConfiguration bucketTaggingConfiguration,
            AmazonS3 s3Client) {
        s3Client.setBucketTaggingConfiguration(resourceId, bucketTaggingConfiguration);
    }

    /**
     * Bucket already tagged and tag value not altered.
     *
     * @param existingTags
     *            the existing tags
     * @param newTags
     *            the new tags
     * @return true, if successful
     */
    private boolean bucketAlreadyTaggedAndTagValueNotAltered(Map<String, String> existingTags,
            Map<String, String> newTags) {

        try {
            return existingTags.containsKey(CommonUtils.getPropValue(PacmanSdkConstants.PACMAN_AUTO_FIX_TAG_NAME))
                    && newTags.get(CommonUtils.getPropValue(PacmanSdkConstants.PACMAN_AUTO_FIX_TAG_NAME)).equals(
                            existingTags.get(CommonUtils.getPropValue(PacmanSdkConstants.PACMAN_AUTO_FIX_TAG_NAME)));
        } catch (Exception e) {
            logger.error("error matching pacman tag", e);
            return Boolean.FALSE;
        }
    }

    /**
     * Gets the pacman tag value.
     *
     * @param resourceId
     *            the resource id
     * @param clientMap
     *            the client map
     * @param serviceType
     *            the service type
     * @return the pacman tag value
     */
    public String getPacmanTagValue(String resourceId, Map<String, Object> clientMap, AWSService serviceType) {
        switch (serviceType) {
        case S3: {
            try {
                AmazonS3 s3Client = (AmazonS3) clientMap.get("client");
                BucketTaggingConfiguration bucketTaggingConfiguration = s3Client
                        .getBucketTaggingConfiguration(resourceId);
                if (null == bucketTaggingConfiguration) {// this is the case
                                                         // when bucket does not
                                                         // exists , this is the
                                                         // case when inventory
                                                         // sync is delayed
                    return null;
                }
                List<TagSet> existingTargets = bucketTaggingConfiguration.getAllTagSets();
                Map<String, String> existingTags = existingTargets.get(0).getAllTags();
                return existingTags.get(CommonUtils.getPropValue(PacmanSdkConstants.PACMAN_AUTO_FIX_TAG_NAME));

            } catch (Exception exception) {
                logger.error("error tagging bucekt - > " + resourceId, exception);
                throw exception;
            }

        }
        case EC2: {

            return getEC2PacManTagValue(resourceId, clientMap);
        }
        case VOLUME: {
            return getEC2PacManTagValue(resourceId, clientMap);
        }
        case SNAPSHOT: {
            return getEC2PacManTagValue(resourceId, clientMap);
        }
        case IAM: {
            return "";
        }
        case ELB_APP: {
            return  getAppElbPacManTagValue(resourceId, clientMap);
        }
        
        case ELB_CLASSIC: {
            return "";
        }
        case REDSHIFT: {
            return "";
        }
        
        case RDS: {
            return "";
        }
        case ELASTICSEARCH: {
            return "";
        }

        default:
            throw new OperationNotPermittedException("this resource tagging is not imlemented yet");
        }

    }

    /**
     *
     * @param resourceId
     * @param clientMap
     * @return
     */
    private String getEC2PacManTagValue(String resourceId, Map<String, Object> clientMap) {
        AmazonEC2 ec2Client = (AmazonEC2) clientMap.get("client");
        DescribeTagsRequest describeTagsRequest = new DescribeTagsRequest();
        Filter filter = new Filter("resource-id");
        filter.setValues(Arrays.asList(resourceId));
        describeTagsRequest.setFilters(Arrays.asList(filter));
        DescribeTagsResult describeTagsResult = ec2Client.describeTags(describeTagsRequest);
        List<TagDescription> descriptions = describeTagsResult.getTags();
        TagDescription tagDescription = null;
        Optional<TagDescription> optional = descriptions.stream()
                .filter(obj -> obj.getKey().equals(PacmanSdkConstants.PACMAN_AUTO_FIX_TAG_NAME)).findAny();
        if (optional.isPresent()) {
            tagDescription = optional.get();
        } else {
            return null;
        }
        return tagDescription.getValue();
    }
    /**
     *
     * @param resourceId
     * @param clientMap
     * @param pacTag
     * @return
     */
    private Boolean setRedshiftTag(final String resourceId,
            final Map<String, Object> clientMap, Map<String, String> pacTag) {
       com.amazonaws.services.redshift.model.Tag tag = new com.amazonaws.services.redshift.model.Tag();
        for (Map.Entry<String, String> tags : pacTag.entrySet()) {
            tag.setKey(tags.getKey());
            tag.setValue(tags.getValue());
        }
        AmazonRedshift amazonRedshift = (AmazonRedshift) clientMap
                .get("client");
        com.amazonaws.services.redshift.model.CreateTagsRequest createTagsRequest = new com.amazonaws.services.redshift.model.CreateTagsRequest();
        createTagsRequest.setResourceName(resourceId);
        createTagsRequest.setTags(Arrays.asList(tag));
        try {
            amazonRedshift.createTags(createTagsRequest);
            return Boolean.TRUE;
        } catch (AmazonServiceException ase) {
            logger.error("error tagging redshift - > " + resourceId, ase);
            throw ase;
        }
    }
    
    /**
     * get the value of pacman tag from app elb
     * @param resourceId
     * @param clientMap
     * @return
     */
    private String getAppElbPacManTagValue(String resourceId, Map<String, Object> clientMap) {

        try{
            AmazonElasticLoadBalancingClient client = (AmazonElasticLoadBalancingClient) clientMap.get(PacmanSdkConstants.CLIENT);
            com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsRequest describeTagsRequest =   new com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsRequest();
            describeTagsRequest.withResourceArns(resourceId);
            com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsResult describeTagsResult = client.describeTags(describeTagsRequest);
            List<com.amazonaws.services.elasticloadbalancingv2.model.TagDescription> descriptions = describeTagsResult.getTagDescriptions();
            com.amazonaws.services.elasticloadbalancingv2.model.Tag tag=null;
            Optional<com.amazonaws.services.elasticloadbalancingv2.model.Tag> optional=null;;
            if(descriptions!=null && descriptions.size()>0){
                 optional = descriptions.get(0).getTags().stream()
                .filter(obj -> obj.getKey().equals(CommonUtils.getPropValue(PacmanSdkConstants.PACMAN_AUTO_FIX_TAG_NAME))).findAny();
            }
            if (optional.isPresent()) {
                tag = optional.get();
            } else {
                return null;
            }
            return tag.getValue();
        }catch (Exception e) {
            logger.error("error whiel getting pacman tag valye for " + resourceId,e);
            return null;
        }
    }

}
