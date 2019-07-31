package com.tmobile.pacman.api.asset.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class CostRepositoryTest {

	@Mock
    ElasticSearchRepository elasticSearchRepository;
	
	@InjectMocks
	CostRepository costRepository;
	
	@Before
	public void init() {
		costRepository.init();
	}
	
	@Test
	public void getCostAggsTest() throws Exception {
		
	 	String yearMonth = "{\"aggregations\":{\"year-month\":{\"value\":201903.0}}}";
	 	String costAggs = "{\"aggregations\":{\"YEAR\":{\"buckets\":[{\"key\":2019,\"doc_count\":5,\"MONTH\":{\"buckets\":[{\"key\":1,\"doc_count\":1,"
	 			+ "\"COST\":{\"value\":57220.640625}},{\"key\":2,\"doc_count\":1,\"COST\":{\"value\":50138.328125}},{\"key\":3,\"doc_count\":1,"
	 			+ "\"COST\":{\"value\":44942.09765625}},{\"key\":4,\"doc_count\":1,\"COST\":{\"value\":37133.69921875}},{\"key\":5,\"doc_count\":1,"
	 			+ "\"COST\":{\"value\":6517.2265625}}]}}]}}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(yearMonth,costAggs);
        assertTrue(costRepository.getCostAggs(Arrays.asList("app")).size() == 5);
	}
	
	@Test
	public void getCostAggsTest_Exception() throws Exception {
		mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new Exception());
        assertThatThrownBy(() -> costRepository.getCostAggs(Arrays.asList("app")))
        .isInstanceOf(Exception.class);
	}
	
	@Test
	public void getCostAggsWithTTTest() throws Exception {
		
	 	String yearMonth = "{\"aggregations\":{\"year-month\":{\"value\":201903.0}}}";
	 	String costAggs = "{\"aggregations\":{\"YEAR\":{\"buckets\":[{\"key\":2019,\"doc_count\":5,\"MONTH\":{\"buckets\":[{\"key\":1,\"doc_count\":1,"
	 			+ "\"COST\":{\"value\":57220.640625}},{\"key\":2,\"doc_count\":1,\"COST\":{\"value\":50138.328125}},{\"key\":3,\"doc_count\":1,"
	 			+ "\"COST\":{\"value\":44942.09765625}},{\"key\":4,\"doc_count\":1,\"COST\":{\"value\":37133.69921875}},{\"key\":5,\"doc_count\":1,"
	 			+ "\"COST\":{\"value\":6517.2265625}}]}}]}}}";
        mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(yearMonth,costAggs);
        assertTrue(costRepository.getCostAggsWithTT(Arrays.asList("app"),Arrays.asList("type")).size() == 5);
	}
	
	@Test
	public void getCostAggsWithTT_Exception() throws Exception {
		mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new Exception());
        assertThatThrownBy(() -> costRepository.getCostAggsWithTT(Arrays.asList("app"),Arrays.asList("type")))
        .isInstanceOf(Exception.class);
	}
	
	@Test
	public void fetchApplicationMasterListTest() throws Exception {
		
		List<Map<String,Object>> appList = new ArrayList<>();
		Map<String,Object> app = new HashMap<>();
		app.put("appTag", "app");
		appList.add(app);
        when(elasticSearchRepository.getDataFromES(anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenReturn(appList);
        assertTrue(costRepository.fetchApplicationMasterList().size() == 1);
	}
}
