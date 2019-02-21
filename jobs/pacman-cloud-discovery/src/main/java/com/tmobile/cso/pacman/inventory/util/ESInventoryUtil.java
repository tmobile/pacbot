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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.elasticsearch.AWSElasticsearch;
import com.amazonaws.services.elasticsearch.AWSElasticsearchClientBuilder;
import com.amazonaws.services.elasticsearch.model.DescribeElasticsearchDomainsRequest;
import com.amazonaws.services.elasticsearch.model.DescribeElasticsearchDomainsResult;
import com.amazonaws.services.elasticsearch.model.DomainInfo;
import com.amazonaws.services.elasticsearch.model.ElasticsearchDomainStatus;
import com.amazonaws.services.elasticsearch.model.ListDomainNamesRequest;
import com.amazonaws.services.elasticsearch.model.ListDomainNamesResult;
import com.amazonaws.services.elasticsearch.model.ListTagsRequest;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.FileGenerator;
import com.tmobile.cso.pacman.inventory.vo.ElasticsearchDomainVH;

/**
 * The Class ESInventoryUtil.
 */
public class ESInventoryUtil {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(ESInventoryUtil.class);
	
	/** The delimiter. */
	private static String delimiter = FileGenerator.DELIMITER;
	
	/**
	 * Instantiates a new ES inventory util.
	 */
	private ESInventoryUtil(){
	}
	
	/**
	 * Fetch ES info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param accountId the accountId
	 * @return the map
	 */
	public static Map<String,List<ElasticsearchDomainVH>> fetchESInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String accountId,String accountName){
		
		Map<String,List<ElasticsearchDomainVH>> esDomainMap = new LinkedHashMap<>();
		AWSElasticsearch awsEsClient ;
		String expPrefix = "{\"errcode\": \"NO_RES_REG\" ,\"accountId\": \""+accountId + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"elasticsearch\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){ 
			try{
				if(!skipRegions.contains(region.getName())){ //!skipRegions
					awsEsClient = AWSElasticsearchClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<ElasticsearchDomainVH> elasticSearchDomains = new ArrayList<>();
					DescribeElasticsearchDomainsResult  describeResult ;
					ListDomainNamesResult listReuslt =  awsEsClient.listDomainNames(new ListDomainNamesRequest());
					List<String> domains = new ArrayList<>();
					for(DomainInfo domain: listReuslt.getDomainNames()){
						domains.add(domain.getDomainName());
					}
					
					DescribeElasticsearchDomainsRequest describeElasticsearchDomainsRequest ;
					int i =0;
					List<String> domainsTemp = new ArrayList<>();
					for(String domain : domains){
						domainsTemp.add(domain);
						i++;
						if(i%5 == 0 || i==domains.size()){
							describeElasticsearchDomainsRequest = new DescribeElasticsearchDomainsRequest();
							describeElasticsearchDomainsRequest.setDomainNames(domainsTemp);
							describeResult = awsEsClient.describeElasticsearchDomains(describeElasticsearchDomainsRequest);
							for( ElasticsearchDomainStatus domaininfo  : describeResult.getDomainStatusList()){
								ElasticsearchDomainVH elasticsearchDomainVH = new ElasticsearchDomainVH();
								elasticsearchDomainVH.setElasticsearchDomainStatus(domaininfo);
								elasticsearchDomainVH.setTags(awsEsClient.listTags(new ListTagsRequest().withARN(domaininfo.getARN())).getTagList());
								elasticSearchDomains.add(elasticsearchDomainVH);
							}
							domainsTemp =  new ArrayList<>();
						}
					}
					
					if(!elasticSearchDomains.isEmpty() ) {
						log.debug("Account : " + accountId + " Type : ES Domain "+ region.getName()+" >> " + elasticSearchDomains.size());
						esDomainMap.put(accountId+delimiter+accountName+delimiter+region.getName(), elasticSearchDomains);
					}
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+"\", \"cause\":\"" +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(accountId,region.getName(),"elasticsearch",e.getMessage());
			}
		}
		return esDomainMap;
	}
}
