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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.client.methods.HttpPost;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.TagSet;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.commons.AWSService;
import com.tmobile.pacman.config.ConfigManager;
import com.tmobile.pacman.util.CommonUtils;
import com.tmobile.pacman.util.ESUtils;
import com.tmobile.pacman.util.ProgramExitUtils;
import com.tmobile.pacman.util.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceTaggingManager.
 *
 * @author kkumar
 */
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ReflectionUtils.class,ESUtils.class, CommonUtils.class, Strings.class,ConfigManager.class})
public class ResourceTaggingManagerTest {

	/** The s 3 mock. */
	@Mock
	private AmazonS3 s3Mock;

	/** The ec 2 mock. */
	@Mock
	private AmazonEC2 ec2Mock;

	/** The describe tags result. */
	@Mock
	private DescribeTagsResult describeTagsResult;

	/** The bucket tagging configuration. */
	@Mock
	private BucketTaggingConfiguration bucketTaggingConfiguration;
	
	 /**
     * Setup.
     */
    @Before
    public void setup(){
        
        mockStatic(ConfigManager.class);
        ConfigManager ConfigManager = PowerMockito.mock(ConfigManager.class);
		PowerMockito.when(ConfigManager.getConfigurationsMap()).thenReturn(new Hashtable<String, Object>());
    }

	/**
	 * Tag resource.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void tagResource() throws Exception{
		Map<String, Object> clientMap = Maps.newHashMap();
		clientMap.put("client", s3Mock);
		clientMap.put("field234", "field234");
		clientMap.put("field345", "field345");

		Map<String, String> pacTag = Maps.newHashMap();
		clientMap.put("field123", "field123");
		clientMap.put("field234", "field234");
		clientMap.put("field345", "field345");

		final ResourceTaggingManager classUnderTest = PowerMockito.spy(new ResourceTaggingManager());
		assertTrue(classUnderTest.tagResource("resourceId", clientMap, AWSService.S3, pacTag));
	}

	/**
	 * Tag resource 2.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void tagResource2() throws Exception{
		PowerMockito.mockStatic(CommonUtils.class);
		Map<String, Object> clientMap = Maps.newHashMap();
		clientMap.put("client", s3Mock);
		clientMap.put("field234", "field234");
		clientMap.put("field345", "field345");
		Map<String, String> pacTag = Maps.newHashMap();
		clientMap.put("field123", "field123");
		clientMap.put("field234", "field234");
		clientMap.put("field345", "field345");
		List<TagSet> existingTargets = Lists.newArrayList();
		TagSet tagSet = new TagSet();
		tagSet.setTag("test", "value2");
		existingTargets.add(tagSet);

		existingTargets.add(tagSet);
		PowerMockito.when(bucketTaggingConfiguration.getAllTagSets()).thenReturn(existingTargets);
		PowerMockito.when(s3Mock.getBucketTaggingConfiguration(anyString())).thenReturn(bucketTaggingConfiguration);
		PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("test");


		final ResourceTaggingManager classUnderTest = PowerMockito.spy(new ResourceTaggingManager());
		assertTrue(classUnderTest.tagResource("resourceId", clientMap, AWSService.S3, pacTag));
	}

	/**
	 * Tag resource 3.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void tagResource3() throws Exception{
		PowerMockito.mockStatic(CommonUtils.class);
		Map<String, Object> clientMap = Maps.newHashMap();
		clientMap.put("client", ec2Mock);
		clientMap.put("field234", "field234");
		clientMap.put("field345", "field345");
		Map<String, String> pacTag = Maps.newHashMap();
		clientMap.put("field123", "field123");
		clientMap.put("field234", "field234");
		clientMap.put("field345", "field345");
		List<TagSet> existingTargets = Lists.newArrayList();
		TagSet tagSet = new TagSet();
		tagSet.setTag("test", "value2");
		existingTargets.add(tagSet);

		existingTargets.add(tagSet);
		//PowerMockito.when(bucketTaggingConfiguration.getAllTagSets()).thenReturn(existingTargets);
		//PowerMockito.when(s3Mock.getBucketTaggingConfiguration(anyString())).thenReturn(bucketTaggingConfiguration);
		PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("test");


		final ResourceTaggingManager classUnderTest = PowerMockito.spy(new ResourceTaggingManager());
		assertTrue(classUnderTest.tagResource("resourceId", clientMap, AWSService.EC2, pacTag));
	}

	/**
	 * Tag resource 4.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void tagResource4() throws Exception{
		Map<String, Object> clientMap = Maps.newHashMap();
		Map<String, String> pacTag = Maps.newHashMap();
		final ResourceTaggingManager classUnderTest = PowerMockito.spy(new ResourceTaggingManager());
		assertThatThrownBy(() -> classUnderTest.tagResource("resourceId", clientMap, AWSService.EC2, pacTag)).isInstanceOf(Exception.class);
	}


	/**
	 * Gets the pacman tag value.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void getPacmanTagValue() throws Exception{
		PowerMockito.mockStatic(CommonUtils.class);
		Map<String, Object> clientMap = Maps.newHashMap();
		clientMap.put("client", s3Mock);
		final ResourceTaggingManager classUnderTest = PowerMockito.spy(new ResourceTaggingManager());
		assertNull(classUnderTest.getPacmanTagValue("resourceId", clientMap, AWSService.S3));
	}

	/**
	 * Gets the pacman tag value 2.
	 *
	 * @throws Exception the exception
	 */
	@Test
    public void getPacmanTagValue2() throws Exception{
		PowerMockito.mockStatic(CommonUtils.class);
		Map<String, Object> clientMap = Maps.newHashMap();
		clientMap.put("client", ec2Mock);
		Map<String, String> pacTag = Maps.newHashMap();
		List<TagSet> existingTargets = Lists.newArrayList();
		TagSet tagSet = new TagSet();
		tagSet.setTag("test", "value2");
		existingTargets.add(tagSet);
		DescribeTagsResult descriptions = getDescribeTagsResult();
		PowerMockito.when(ec2Mock.describeTags(any(DescribeTagsRequest.class))).thenReturn(descriptions);
		PowerMockito.when(bucketTaggingConfiguration.getAllTagSets()).thenReturn(existingTargets);
		PowerMockito.when(s3Mock.getBucketTaggingConfiguration(anyString())).thenReturn(bucketTaggingConfiguration);
		PowerMockito.when(CommonUtils.getPropValue(anyString())).thenReturn("test");


		final ResourceTaggingManager classUnderTest = PowerMockito.spy(new ResourceTaggingManager());
		assertNull(classUnderTest.getPacmanTagValue("resourceId", clientMap, AWSService.EC2));
	}

	/**
	 * Gets the describe tags result.
	 *
	 */
	private DescribeTagsResult getDescribeTagsResult() {
		DescribeTagsResult describeTagsResult = new DescribeTagsResult();
		describeTagsResult.setTags(getTagDescriptions());
		return describeTagsResult;
	}

	/**
	 * Gets the tag descriptions.
	 *
	 */
	private List<TagDescription> getTagDescriptions() {
		List<TagDescription> dists = Lists.newArrayList();
		TagDescription dist = new TagDescription();
		dist.setKey("key");
		dist.setResourceId("resourceId");
		dist.setResourceType("resourceType");
		dist.setResourceType("resourceType");
		dist.setValue("value");
		dists.add(dist);
		return dists;
	}
}
