package com.tmobile.pacman.api.asset.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.asset.domain.Request;
import com.tmobile.pacman.api.asset.service.RecommendationsService;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class })
public class RecommendationsControllerTest {

	@Mock
	RecommendationsService recommendationsService;
	
	RecommendationsController recommendationsController = new RecommendationsController();
	
	@Test
	public void getRecommendationSummaryTest() throws Exception {
		
		when(recommendationsService.getRecommendationSummary(anyString(),anyString(),anyBoolean())).thenReturn(new ArrayList<>());
		ReflectionTestUtils.setField(recommendationsController, "recommendationsService", recommendationsService);
		assertTrue(recommendationsController.getRecommendationSummary("ag", null, false).getStatusCode() == HttpStatus.OK);
		assertTrue(recommendationsController.getRecommendationSummary(null, null, true).getStatusCode() == HttpStatus.OK);
	}
	
	@Test
	public void getRecommendationSummaryTest_Exception() throws Exception {
		
		when(recommendationsService.getRecommendationSummary(anyString(),anyString(),anyBoolean())).thenThrow(new DataException());
		ReflectionTestUtils.setField(recommendationsController, "recommendationsService", recommendationsService);
		assertTrue(recommendationsController.getRecommendationSummary("ag", null, false).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		assertTrue(recommendationsController.getRecommendationSummary(null, null, false).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
	}
	
	@Test
	public void getSummaryByApplicationTest() throws Exception {
		
		when(recommendationsService.getSummaryByApplication(anyString(),anyString())).thenReturn(new HashMap<>());
		ReflectionTestUtils.setField(recommendationsController, "recommendationsService", recommendationsService);
		assertTrue(recommendationsController.getSummaryByApplication("ag", "app").getStatusCode() == HttpStatus.OK);
		
		when(recommendationsService.getSummaryByApplication(anyString(),anyString())).thenThrow(new DataException());
		assertTrue(recommendationsController.getSummaryByApplication("ag", "app").getStatusCode() == HttpStatus.EXPECTATION_FAILED);
	}
	
	@Test
	public void getRecommendationsTest() throws Exception {
		
		Map<String,Object> recommendations = new HashMap<>();
		recommendations.put("response", new ArrayList<>());
		when(recommendationsService.getRecommendations(anyString(),anyString(),anyString(),anyString())).thenReturn(recommendations);
		ReflectionTestUtils.setField(recommendationsController, "recommendationsService", recommendationsService);
		Request request = new Request();
		request.setAg("ag");
		Map<String,String> filter = new HashMap<>();
		filter.put("general", "false");
		filter.put("category", "category");
		request.setFilter(filter);
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.OK);
		
		List<Map<String,Object>> resp = new ArrayList<>();
 		Map<String,Object> recommendation = new HashMap<>();
		recommendation.put("recommendationId", "id");
		resp.add(recommendation);
		recommendations.put("response", resp);
		when(recommendationsService.getRecommendations(anyString(),anyString(),anyString(),anyString())).thenReturn(recommendations);
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.OK);
		
		request.setFrom(0);
		request.setSize(2);
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.OK);
	}
	
	@Test
	public void getRecommendationsTest_Exception() throws Exception {
		
		Request request = new Request();
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		Map<String,String> filter = new HashMap<>();
		filter.put("test", "test");
		request.setFilter(filter);
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		filter = new HashMap<>();
		filter.put("category", "category");
		request.setFilter(filter);
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		filter.put("general", "false");
		request.setFilter(filter);
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		filter.put("general", "true");
		request.setFilter(filter);
		request.setAg("ag");
		request.setFrom(-1);
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);

		Map<String,Object> recommendations = new HashMap<>();
		List<Map<String,Object>> resp = new ArrayList<>();
 		Map<String,Object> recommendation = new HashMap<>();
		recommendation.put("recommendationId", "id");
		resp.add(recommendation);
		recommendations.put("response", resp);
		
		request.setFrom(2);
		when(recommendationsService.getRecommendations(anyString(),anyString(),anyString(),anyString())).thenReturn(recommendations);
		ReflectionTestUtils.setField(recommendationsController, "recommendationsService", recommendationsService);
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		request.setFrom(0);
		request.setSize(1);
		when(recommendationsService.getRecommendations(anyString(),anyString(),anyString(),anyString())).thenThrow(new DataException());
		assertTrue(recommendationsController.getRecommendations(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
	}
	
	@Test
	public void getRecommendationDetailTest() throws Exception {
		
		Map<String,Object> recommendations = new HashMap<>();
		recommendations.put("resources", new ArrayList<>());
		when(recommendationsService.getRecommendationDetail(anyString(),anyString(),anyString(),anyString())).thenReturn(recommendations);
		ReflectionTestUtils.setField(recommendationsController, "recommendationsService", recommendationsService);
		Request request = new Request();
		request.setAg("ag");
		Map<String,String> filter = new HashMap<>();
		filter.put("general", "false");
		filter.put(AssetConstants.FILTER_RECOMMENDATION_ID,AssetConstants.FILTER_RECOMMENDATION_ID);
		request.setFilter(filter);
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.OK);
		
		List<Map<String,Object>> resp = new ArrayList<>();
 		Map<String,Object> recommendation = new HashMap<>();
		recommendation.put("recommendationId", "id");
		resp.add(recommendation);
		recommendations.put("resources", resp);
		when(recommendationsService.getRecommendationDetail(anyString(),anyString(),anyString(),anyString())).thenReturn(recommendations);
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.OK);
		
		request.setFrom(0);
		request.setSize(2);
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.OK);
	}
	
	@Test
	public void getRecommendationDetailTest_Exception() throws Exception {
		
		Request request = new Request();
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		Map<String,String> filter = new HashMap<>();
		filter.put("test", "test");
		request.setFilter(filter);
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		filter = new HashMap<>();
		filter.put(AssetConstants.FILTER_RECOMMENDATION_ID,AssetConstants.FILTER_RECOMMENDATION_ID);
		request.setFilter(filter);
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		filter.put("general", "false");
		request.setFilter(filter);
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		filter.put("general", "true");
		request.setFilter(filter);
		request.setAg("ag");
		request.setFrom(-1);
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);

		Map<String,Object> recommendations = new HashMap<>();
		List<Map<String,Object>> resp = new ArrayList<>();
 		Map<String,Object> recommendation = new HashMap<>();
		recommendation.put("recommendationId", "id");
		resp.add(recommendation);
		recommendations.put("resources", resp);
		
		request.setFrom(2);
		when(recommendationsService.getRecommendationDetail(anyString(),anyString(),anyString(),anyString())).thenReturn(recommendations);
		ReflectionTestUtils.setField(recommendationsController, "recommendationsService", recommendationsService);
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		request.setFrom(0);
		request.setSize(1);
		when(recommendationsService.getRecommendationDetail(anyString(),anyString(),anyString(),anyString())).thenThrow(new DataException());
		assertTrue(recommendationsController.getRecommendationDetail(request).getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
	}
	
	@Test
	public void getRecommendationInfoTest() throws Exception {
		
		when(recommendationsService.getRecommendationInfo(anyString())).thenReturn(new HashMap<>());
		ReflectionTestUtils.setField(recommendationsController, "recommendationsService", recommendationsService);
		assertTrue(recommendationsController.getRecommendationInfo("id").getStatusCode() == HttpStatus.OK);
		
		when(recommendationsService.getRecommendationInfo(anyString())).thenThrow(new DataException());
		assertTrue(recommendationsController.getRecommendationInfo("id").getStatusCode() == HttpStatus.EXPECTATION_FAILED);
	}
}