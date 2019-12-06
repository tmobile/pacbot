package com.tmobile.pacman.api.asset.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class RecommendationsRepositoryTest {

	@Mock
    ElasticSearchRepository elasticSearchRepository;
	
	@Mock
	PacmanRdsRepository rdsRepository;
	
	@InjectMocks
	RecommendationsRepository recommendationsRepository;
	
	@Before
	public void init() {
		recommendationsRepository.init();
		ReflectionTestUtils.setField(recommendationsRepository, "recommendationCategories", "fault_tolerance, performance");
	}
	
	@Test
	public void getRecommendationSummaryTest() throws Exception {
		String summary = "{\"aggregations\":{\"recommendations\":{\"doc_count\":81294,\"latest\":{\"doc_count\":58372,\"category\":"
				+ "{\"buckets\":[{\"key\":\"cost\",\"doc_count\":9766,\"savings\":{\"value\":716724.5030126646}},{\"key\":\"perf\",\"doc_count\":876,\"savings\":{\"value\":0}}]}}}}}";
		 mockStatic(PacHttpUtils.class);
	     when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(summary);
	     assertTrue(recommendationsRepository.getRecommendationSummary("ag", "app").size() == 2);
	}
	
	@Test
	public void getRecommendationSummaryAzureTest() throws Exception {
		String summary = "{\"aggregations\":{\"recommendations\":{\"doc_count\":0}}}";
		 mockStatic(PacHttpUtils.class);
	     when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(summary);
	     assertTrue(recommendationsRepository.getRecommendationSummary("ag", null).size() == 2);	     
	}
	
	@Test
	public void getRecommendationSummaryTest_Exception() throws Exception {
		mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(() -> recommendationsRepository.getRecommendationSummary("ag", null)).isInstanceOf(DataException.class);
	}
	
	@Test
	public void getSummaryByApplicationTest() throws Exception {
		
		String summaryApp = "{\"aggregations\":{\"apps\":{\"buckets\":[{\"key\":\"PacMan\",\"doc_count\":1486,\"recommendations\":"
				+ "{\"doc_count\":593,\"latest\":{\"doc_count\":325,\"savings\":{\"value\":1008.0500058233738}}}}]}}}";
		String summaryAppByCat = "{\"aggregations\":{\"apps\":{\"buckets\":[{\"key\":\"app\",\"doc_count\":1486,"
				+ "\"recommendations\":{\"doc_count\":590,\"latest\":{\"doc_count\":323,\"category\":{\"doc_count\":1,\"savings\":{\"value\":0}}}}}]}}}";
		mockStatic(PacHttpUtils.class);
	    when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(summaryAppByCat,summaryApp);
	    assertTrue(recommendationsRepository.getSummaryByApplication("ag", "cost_optimizing").size() == 4);
	    assertTrue(recommendationsRepository.getSummaryByApplication("ag").size() == 3);
	}
	
	@Test
	public void getSummaryByApplicationTest_Exception() throws Exception {
		
		mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(() -> recommendationsRepository.getSummaryByApplication("ag", "cost_optimizing")).isInstanceOf(DataException.class);
        assertThatThrownBy(() -> recommendationsRepository.getSummaryByApplication("ag")).isInstanceOf(DataException.class);
        
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void getRecommendationsTest() throws Exception {
		String recommendationJson = "{\"aggregations\":{\"type\":{\"buckets\":[{\"key\":\"lambda\",\"doc_count\":270,\"recommendations\":{\"doc_count\":0}},{\"key\":\"volume\",\"doc_count\":199,\"recommendations\":{\"doc_count\":470,\"latest\":{\"doc_count\":242,\"category\":"
				+ "{\"doc_count\":43,\"recommendation\":{\"buckets\":[{\"key\":\"DAvU99Dc4C\",\"doc_count\":43,\"savings\":{\"value\":518.8000001013279}}]}}}}}]}}}";
		mockStatic(PacHttpUtils.class);
	    when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(recommendationJson);
	    List<Map<String,Object>> recommendations = new ArrayList<>();
	    Map<String,Object> recommendation = new HashMap<>();
	    recommendation.put("checkname","name");
	    recommendation.put("checkdescription","description");
	    recommendations.add(recommendation);
	    when(elasticSearchRepository.getDataFromES(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(recommendations);
	    assertTrue(recommendationsRepository.getRecommendations("ag", "cost_optimizing","app").size() == 4);
	}
	
	@Test
	public void getRecommendationsTest_Exception() throws Exception {
		
		mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(() -> recommendationsRepository.getRecommendations("ag", "cost_optimizing","app")).isInstanceOf(DataException.class);
	}
	
	@Test
	public void getRecommendationDetailTest() throws Exception {
		
		String response = "{\"_scroll_id\":\"id\",\"hits\":{\"hits\":[{\"_source\":{\"_resourceid\":\"123\",\"monthlysavings\":2,"
				+ "\"recommendation\":\"Amazon EBS Volumes\",\"recommendationId\":\"DAvU99Dc4C\",\"resourceinfo\":{\"Volume Type\":\"General purpose(SSD)\","
				+ "\"Volume Size\":\"20\",\"Snapshot Name\":\"null\",\"Monthly Storage Cost\":\"$2.00\"},\"category\":\"cost_optimizing\","
				+ "\"type\":\"volume\",\"_loaddate\":\"2019-06-12\",\"status\":\"warning\",\"latest\":true}}]}}";
		String countresponse = "{\"count\":43}";
		when(rdsRepository.queryForString(anyString())).thenReturn("parent");
		mockStatic(PacHttpUtils.class);
	    when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(countresponse,response);
	    when(elasticSearchRepository.buildScrollRequest(anyString(), anyString())).thenReturn("request");
	    assertTrue(recommendationsRepository.getRecommendationDetail("ag", "id","app").size() == 1);
	}
	
	@Test
	public void getGeneralRecommendationSummaryTest() throws Exception {
		String summary = "{\"aggregations\":{\"category\":{\"buckets\":[{\"key\":\"performance\",\"doc_count\":55}]}}}";
		 mockStatic(PacHttpUtils.class);
	     when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(summary);
	     assertTrue(recommendationsRepository.getGeneralRecommendationSummary(new ArrayList<String>()).size() == 1);
	}
	
	@Test
	public void getGeneralRecommendationSummaryTest_Exception() throws Exception {
		mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(() -> recommendationsRepository.getGeneralRecommendationSummary(new ArrayList<String>())).isInstanceOf(DataException.class);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void getGeneralRecommendationsTest() throws Exception {
		String recommendationJson = "{\"aggregations\":{\"recommendations\":{\"buckets\":[{\"key\":\"abcd\",\"doc_count\":29}]}}}";
		mockStatic(PacHttpUtils.class);
	    when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(recommendationJson);
	    List<Map<String,Object>> recommendations = new ArrayList<>();
	    Map<String,Object> recommendation = new HashMap<>();
	    recommendation.put("checkname","name");
	    recommendation.put("checkdescription","description");
	    recommendations.add(recommendation);
	    when(elasticSearchRepository.getDataFromES(anyString(), anyString(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(recommendations);
	    assertTrue(recommendationsRepository.getGeneralRecommendations("category", new ArrayList<String>()).size() == 2);
	}
	
	@Test
	public void getGeneralRecommendationsTest_Exception() throws Exception {
		
		mockStatic(PacHttpUtils.class);
        when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenThrow(new DataException());
        assertThatThrownBy(() -> recommendationsRepository.getGeneralRecommendations("category", new ArrayList<String>())).isInstanceOf(DataException.class);
	}
	
	@Test
	public void getGeneralRecommendationDetailTest() throws Exception {
		
		String response = "{\"_scroll_id\":\"id\",\"hits\":{\"hits\":[{\"_source\":{\"recommendation\":\"Route 53 recommendation\",\"recommendationId\":\"B913Ef6fb4\","
				+ "\"resourceinfo\":{\"Hosted Zone Name\":\"asdsd\",\"Hosted Zone ID\":\"asdad\",\"Resource Record Set Type\":\"name\","
				+ "\"Resource Record Set Identifier\":\"null\",\"Alias Target\":\"Load balancer\",\"Status\":\"Yellow\"},\"category\":\"performance\","
				+ "\"_loaddate\":\"2019-06-13T16:39:30+0530\",\"status\":\"warning\",\"latest\":true}}]}}";
		String countresponse = "{\"count\":43}";
		mockStatic(PacHttpUtils.class);
	    when(PacHttpUtils.doHttpPost(anyString(), anyString())).thenReturn(countresponse,response);
	    when(elasticSearchRepository.buildScrollRequest(anyString(), anyString())).thenReturn("request");
	    assertTrue(recommendationsRepository.getGeneralRecommendationDetail("id").size() == 1);
	}
}
