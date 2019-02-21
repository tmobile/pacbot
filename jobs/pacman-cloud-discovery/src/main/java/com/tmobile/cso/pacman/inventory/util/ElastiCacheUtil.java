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
package com.tmobile.cso.pacman.inventory.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.elasticache.AmazonElastiCache;
import com.amazonaws.services.elasticache.AmazonElastiCacheClientBuilder;
import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticache.model.CacheNode;
import com.amazonaws.services.elasticache.model.CacheSubnetGroup;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersRequest;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersResult;
import com.amazonaws.services.elasticache.model.DescribeCacheSubnetGroupsRequest;
import com.amazonaws.services.elasticache.model.DescribeReplicationGroupsRequest;
import com.amazonaws.services.elasticache.model.DescribeReplicationGroupsResult;
import com.amazonaws.services.elasticache.model.Endpoint;
import com.amazonaws.services.elasticache.model.ReplicationGroup;
import com.amazonaws.services.elasticache.model.Subnet;
import com.amazonaws.services.elasticache.model.Tag;
import com.tmobile.cso.pacman.inventory.InventoryConstants;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.FileGenerator;
import com.tmobile.cso.pacman.inventory.vo.ElastiCacheNodeVH;
import com.tmobile.cso.pacman.inventory.vo.ElastiCacheVH;

/**
 * The Class ElastiCacheUtil.
 */
public class ElastiCacheUtil {
    
    /** The log. */
    private static Logger log = LoggerFactory.getLogger(ElastiCacheUtil.class);
    
    /** The delimiter. */
    private static String delimiter = FileGenerator.DELIMITER;
    
    /**
     * Fetch elasti cache info.
     *
     * @param temporaryCredentials the temporary credentials
     * @param skipRegions the skip regions
     * @param accountId the accountId
     * @return the map
     */
    public static Map<String,List<ElastiCacheVH>> fetchElastiCacheInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName) {
        
        Map<String,List<ElastiCacheVH>> elastiCache = new LinkedHashMap<>();
        
        String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+accountId + "\",\"Message\": \"Exception in fetching info for resource \" ,\"type\": \"ElastiCache\"" ;
        String arnTemplate = "arn:aws:elasticache:%s:%s:cluster:%s";
        for(Region region : RegionUtils.getRegions()) { 
            try{
                if(!skipRegions.contains(region.getName())){ 
                    
                    List<CacheCluster> cacheClusterList = new ArrayList<>();
                    AmazonElastiCache amazonElastiCache = AmazonElastiCacheClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
                   
                    String marker = null;
                    DescribeCacheClustersResult  describeResult ;
                    DescribeCacheClustersRequest rqst;
                  
                    do{        
                        rqst =new DescribeCacheClustersRequest().withMarker(marker);
                        rqst.setShowCacheNodeInfo(true);
                        describeResult = amazonElastiCache.describeCacheClusters(rqst);
                        cacheClusterList.addAll(describeResult.getCacheClusters());
                        marker = describeResult.getMarker();
                      
                    }while(marker!=null);
                    
                    
                    List<ReplicationGroup> replicationGroupList = new ArrayList<>();
                    marker = null;
                    DescribeReplicationGroupsResult  describeRGResult ;
                    DescribeReplicationGroupsRequest rgRqst;
                  
                    do{        
                        rgRqst = new DescribeReplicationGroupsRequest().withMarker(marker);
                        describeRGResult = amazonElastiCache.describeReplicationGroups(rgRqst);
                        replicationGroupList.addAll(describeRGResult.getReplicationGroups());
                        marker = describeResult.getMarker();
                      
                    }while(marker!=null);
                    
                    Map<String,List<CacheCluster>> cacheMap = cacheClusterList.stream().collect(Collectors.groupingBy(cluster-> cluster.getReplicationGroupId()!=null?cluster.getReplicationGroupId():cluster.getCacheClusterId()));
                    Map<String,ReplicationGroup> replGrpMap = replicationGroupList.stream().collect(Collectors.toMap(rplGrp -> rplGrp.getReplicationGroupId(),rplGrp->rplGrp));
                  
                    
                    List<ElastiCacheVH> elasticacheList = populateVH(cacheMap,replGrpMap);
                 
                    String engine;
                    String arn ;
                    for(ElastiCacheVH cacheVH :elasticacheList){
                    	engine = cacheVH.getCluster().getEngine();
                    	arn = String.format(arnTemplate, region.getName(),accountId,cacheVH.getCluster().getCacheClusterId()); 	
                        cacheVH.setArn(String.format(arnTemplate, region.getName(),accountId,cacheVH.getClusterName()));
     
                        if("memcached".equalsIgnoreCase(engine)){
                        	cacheVH.setTags(amazonElastiCache.listTagsForResource(new com.amazonaws.services.elasticache.model.ListTagsForResourceRequest().
                                    withResourceName(arn)).getTagList());   
                        }
                        List<CacheSubnetGroup> subnetGroups = amazonElastiCache.describeCacheSubnetGroups( new DescribeCacheSubnetGroupsRequest().withCacheSubnetGroupName(cacheVH.getCluster().getCacheSubnetGroupName())).getCacheSubnetGroups();
                        subnetGroups.forEach(cacheGroup-> {
                            cacheVH.setVpc(cacheGroup.getVpcId());
                            cacheVH.setSubnets(cacheGroup.getSubnets().stream().map(Subnet::getSubnetIdentifier).collect(Collectors.toList()));
                        });
                           
                        List<ElastiCacheNodeVH> nodeDetails = getNodeDetails(cacheMap,replGrpMap,accountId, arnTemplate, region,
								amazonElastiCache, cacheVH);
                        cacheVH.setNodes(nodeDetails);
                    }
          
                    if(!elasticacheList.isEmpty()) {
                        log.debug(InventoryConstants.ACCOUNT + accountId +" Type : ElastiCache "+region.getName() + " >> "+elasticacheList.size());
                        elastiCache.put(accountId+delimiter+accountName+delimiter+region.getName(), elasticacheList);
                    }
                }
            }catch(Exception e){
                log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
                ErrorManageUtil.uploadError(accountId,"","elastiCache",e.getMessage());
            }
        }
        return elastiCache;
    }


	private static List<ElastiCacheNodeVH> getNodeDetails(Map<String,List<CacheCluster>> cacheClusterMap,Map<String,ReplicationGroup> replGroupMap ,String accountId, String arnTemplate, Region region,
			AmazonElastiCache amazonElastiCache, ElastiCacheVH cacheVH) {
		
		List<ElastiCacheNodeVH> nodeDetails = new ArrayList<>();
		String clusterName = cacheVH.getClusterName();
    	String engine = cacheVH.getCluster().getEngine();
		List<CacheCluster> cacheClusters = cacheClusterMap.get(clusterName);
		cacheClusters.forEach(cacheCluster -> {
			String clusterId = cacheCluster.getCacheClusterId();
			List<CacheNode> nodes = cacheCluster.getCacheNodes();
			if(!nodes.isEmpty()){
	    		for(CacheNode node : nodes){
	    			ElastiCacheNodeVH nodeVH = new ElastiCacheNodeVH();
	    			nodeVH.setNode(node);
	    			if("memcached".equalsIgnoreCase(engine)){
	    				nodeVH.setNodeName(node.getCacheNodeId());
	    			}else{
	    				nodeVH.setNodeName(cacheCluster.getCacheClusterId());
	    				try {
	    					List<Tag> tags = amazonElastiCache.listTagsForResource(new com.amazonaws.services.elasticache.model.ListTagsForResourceRequest().
	    		                withResourceName(String.format(arnTemplate, region.getName(),accountId,clusterId))).getTagList();
	    					nodeVH.setTags(tags.stream().map(tag->tag.getKey()+":"+tag.getValue()).collect(Collectors.joining(",")));
	    				} catch (Exception e) {
	    					
	    				}
	    			}
	    			nodeDetails.add(nodeVH);
	    		}
			}
		});
		
    	return nodeDetails;
	}
    

    /**
     * Populate VH.
     *
     * @param cacheClusterList the cache cluster list
     * @param replicationGroupList the replication group list
     * @return the list
     */
    private static List<ElastiCacheVH> populateVH(Map<String,List<CacheCluster>> cacheMap,Map<String,ReplicationGroup> replGrpMap  ){
        
        List<ElastiCacheVH> elasticacheList = new ArrayList<>();
        
        cacheMap.forEach((k,v)->{
            String clusterName = k;
            ElastiCacheVH elastiCacheVH = new ElastiCacheVH();
            elastiCacheVH.setClusterName(clusterName);
            elastiCacheVH.setAvailabilityZones(v.stream().map(CacheCluster::getPreferredAvailabilityZone).collect(Collectors.toSet()).stream().collect(Collectors.joining(",")));
            
            CacheCluster cluster = v.get(0);
            elastiCacheVH.setSecurityGroups(cluster.getSecurityGroups().stream().map(sg -> sg.getSecurityGroupId()+"("+sg.getStatus()+")").collect(Collectors.joining(",")));
            elastiCacheVH.setParameterGroup(cluster.getCacheParameterGroup().getCacheParameterGroupName()+"("+cluster.getCacheParameterGroup().getParameterApplyStatus()+")");
            elastiCacheVH.setCluster(cluster);
            String engine = cluster.getEngine();
              	
            if("memcached".equalsIgnoreCase(engine)){
                elastiCacheVH.setNoOfNodes(cluster.getNumCacheNodes());
                elastiCacheVH.setPrimaryOrConfigEndpoint(cluster.getConfigurationEndpoint().getAddress()+":"+cluster.getConfigurationEndpoint().getPort());
            }else{
                ReplicationGroup rplGrp = replGrpMap.get(clusterName);
                Endpoint endPoint ;
                if(rplGrp!=null){
                    elastiCacheVH.setDescription(rplGrp.getDescription());
                    elastiCacheVH.setNoOfNodes(rplGrp.getMemberClusters().size());
                    endPoint = rplGrp.getConfigurationEndpoint();
                    if(endPoint==null){
                        endPoint = rplGrp.getNodeGroups().stream().filter(obj->obj.getPrimaryEndpoint()!=null).map(obj-> obj.getPrimaryEndpoint()).findAny().get();
                    }
                }else{
                    elastiCacheVH.setNoOfNodes(cluster.getNumCacheNodes());
                    endPoint = cluster.getCacheNodes().stream().map(CacheNode::getEndpoint).findAny().get();
                    
                }
                elastiCacheVH.setPrimaryOrConfigEndpoint(endPoint.getAddress().replaceAll(cluster.getCacheClusterId(), clusterName)+":"+endPoint.getPort());
            }
            
            elasticacheList.add(elastiCacheVH);
            
        });
        return elasticacheList;
    }

}
