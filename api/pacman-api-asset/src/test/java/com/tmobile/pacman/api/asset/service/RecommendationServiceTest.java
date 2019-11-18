package com.tmobile.pacman.api.asset.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.repository.RecommendationsRepository;

@RunWith(PowerMockRunner.class)
public class RecommendationServiceTest {

	@Mock
	RecommendationsRepository recommendationsRepository;
	
	@Mock
	AssetService assetService;
	
	RecommendationsService recommendationsService = new RecommendationsService();
	
	@Test
	public void getRecommendationSummaryTest() throws Exception {
		
		when(recommendationsRepository.getGeneralRecommendationSummary(Matchers.anyListOf(String.class))).thenReturn(new ArrayList<>());
		when(assetService.getProvidersForAssetGroup(anyString())).thenReturn(new ArrayList<String>());
		ReflectionTestUtils.setField(recommendationsService, "recommendationsRepository", recommendationsRepository);
		ReflectionTestUtils.setField(recommendationsService, "assetService", assetService);
		assertTrue(recommendationsService.getRecommendationSummary(null,null,true).size() == 0);
		when(recommendationsRepository.getRecommendationSummary(anyString(), anyString())).thenReturn(new ArrayList<>());
		assertTrue(recommendationsService.getRecommendationSummary("ag","app",false).size() == 0);
	}
	
	@Test
	public void getSummaryByApplicationTest() throws Exception {
		
		when(recommendationsRepository.getSummaryByApplication(anyString())).thenReturn(new HashMap<>());
		ReflectionTestUtils.setField(recommendationsService, "recommendationsRepository", recommendationsRepository);
		assertTrue(recommendationsService.getSummaryByApplication("ag",null).size() == 0);
		when(recommendationsRepository.getSummaryByApplication(anyString(), anyString())).thenReturn(new HashMap<>());
		assertTrue(recommendationsService.getSummaryByApplication("ag","category").size() == 0);
	}
	
	@Test
	public void getRecommendationsTest() throws Exception {
		
		when(recommendationsRepository.getGeneralRecommendations(anyString(), Matchers.anyListOf(String.class))).thenReturn(new HashMap<>());
		ReflectionTestUtils.setField(recommendationsService, "recommendationsRepository", recommendationsRepository);
		assertTrue(recommendationsService.getRecommendations(null,"category",null,"false").size() == 0);
		when(recommendationsRepository.getRecommendations(anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
		when(assetService.getProvidersForAssetGroup(anyString())).thenReturn(new ArrayList<String>());
		ReflectionTestUtils.setField(recommendationsService, "assetService", assetService);
		assertTrue(recommendationsService.getRecommendations("ag","category","app","true").size() == 0);
	}
	
	@Test
	public void getRecommendationDetailTest() throws Exception {
		
		when(recommendationsRepository.getGeneralRecommendationDetail(anyString())).thenReturn(new HashMap<>());
		ReflectionTestUtils.setField(recommendationsService, "recommendationsRepository", recommendationsRepository);
		assertTrue(recommendationsService.getRecommendationDetail(null,"id",null,"false").size() == 0);
		when(recommendationsRepository.getRecommendationDetail(anyString(), anyString(), anyString())).thenReturn(new HashMap<>());
		assertTrue(recommendationsService.getRecommendationDetail("ag","id","app","true").size() == 0);
	}
	
	@Test
	public void getRecommendationInfoTest() throws Exception {
		
		Map<String,Object> recommendationInfo = new HashMap<>();
		recommendationInfo.put("checkdescription", "Checks for Provisioned IOPS (SSD) volume.Alert Criteria Yellow: An Amazon EC2 instance that can be EBS-optimized.Recommended Action Create a new instance that is EBS-optimized, detach the volume, and reattach the volume to your new instance.");
		when(recommendationsRepository.getRecommendation(anyString())).thenReturn(recommendationInfo);
		ReflectionTestUtils.setField(recommendationsService, "recommendationsRepository", recommendationsRepository);
		assertTrue(recommendationsService.getRecommendationInfo("id").size() == 3);
	}
}
