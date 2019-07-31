package com.tmobile.pacman.api.asset.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.domain.ApplicationDetail;
import com.tmobile.pacman.api.asset.repository.AssetRepository;
import com.tmobile.pacman.api.asset.repository.CostRepository;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;

@RunWith(PowerMockRunner.class)
public class CostServiceTest {

	@Mock
    ElasticSearchRepository elasticSearchRepository;
	
	@Mock
	AssetService assetService;
	
	@Mock
	CostRepository costRepository;
	
	@Mock
	AssetRepository assetRepository;
	
	@InjectMocks
	CostService costService;
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Test
	public void getCostByTypeTest() throws Exception {
		Map<String,Integer> yearMonth = new HashMap<>();
		yearMonth.put("month", 5);
		yearMonth.put("year", 2019);
		when(costRepository.findLatestCostFinalisedMonth()).thenReturn(yearMonth);
		when(costRepository.fetchApplicationMasterList()).thenReturn(new ArrayList<>());
		ReflectionTestUtils.setField(costService, "costRepository", costRepository);
		when(elasticSearchRepository.getDataFromES(anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenReturn(getCurrentCost(),getPreviousCost());
		List<Map<String,Object>> typeDataSource = new ArrayList<>();
		Map<String,Object> dataSource = new HashMap<>();
		dataSource.put(Constants.TYPE, Constants.TYPE);
		dataSource.put(Constants.PROVIDER, "aws");
		typeDataSource.add(dataSource);
		when(assetRepository.getDatasourceForCostMapping(anyObject())).thenReturn(typeDataSource);
		
		assertTrue(costService.getCostByType("ag", 2019, Arrays.asList("4"), Arrays.asList("app"), Arrays.asList("type")).size() == 7);
		assertTrue(costService.getCostByType("ag", null, new ArrayList<>(), Arrays.asList("app"), Arrays.asList("type")).size() == 7);
		assertTrue(costService.getCostByType("ag", 2019, Arrays.asList("2","3"), Arrays.asList("app"), Arrays.asList("type")).size() == 7);
		assertTrue(costService.getCostByType("ag", 2019, Arrays.asList("1","2","3"), Arrays.asList("app"), Arrays.asList("type")).size() == 7);
		assertTrue(costService.getCostByType("ag", 2019, Arrays.asList("1"), Arrays.asList("app"), Arrays.asList("type")).size() == 7);
	}
	
	@Test
	public void getCostByTypeTest_Exception() throws Exception {
		when(costRepository.findLatestCostFinalisedMonth()).thenReturn(new HashMap<>());
		assertThatThrownBy(() -> costService.getCostByType("ag", null, new ArrayList<>(), Arrays.asList("app"), Arrays.asList("type")))
	        .isInstanceOf(Exception.class);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Test
	public void getCostByApplicationTest() throws Exception {
		Map<String,Integer> yearMonth = new HashMap<>();
		yearMonth.put("month", 5);
		yearMonth.put("year", 2019);
		when(costRepository.findLatestCostFinalisedMonth()).thenReturn(yearMonth);
		when( costRepository.fetchApplicationMasterList()).thenReturn(Arrays.asList("app"));
		ReflectionTestUtils.setField(costService, "costRepository", costRepository);
		when(elasticSearchRepository.getDataFromES(anyString(),anyString(),anyObject(),anyObject(),anyObject(),anyObject(),anyObject())).thenReturn(getCurrentCost(),getPreviousCost());
		
		assertTrue(costService.getCostByApplication("ag", 2019, Arrays.asList("4"), Arrays.asList("app"), Arrays.asList("type"), getValidApplications()).size() == 7);
		assertTrue(costService.getCostByApplication("ag", null, new ArrayList<>(), Arrays.asList("app"), Arrays.asList("type"), getValidApplications()).size() == 7);
		assertTrue(costService.getCostByApplication("ag", 2019, Arrays.asList("2","3"), Arrays.asList("app"), Arrays.asList("type"), getValidApplications()).size() == 7);
		assertTrue(costService.getCostByApplication("ag", 2019, Arrays.asList("1","2","3"), Arrays.asList("app"), Arrays.asList("type"), getValidApplications()).size() == 7);
		assertTrue(costService.getCostByApplication("ag", 2019, Arrays.asList("1"), Arrays.asList("app"), Arrays.asList("type"), getValidApplications()).size() == 7);
	}
	
	@Test
	public void getCostByApplicationTest_Exception() throws Exception {
		when(costRepository.findLatestCostFinalisedMonth()).thenReturn(new HashMap<>());
		assertThatThrownBy(() -> costService.getCostByApplication("ag", null, new ArrayList<>(), Arrays.asList("app"), Arrays.asList("type"), getValidApplications()))
	        .isInstanceOf(Exception.class);
	}
	
	@Test
	public void getCostTrendTest() throws Exception {
		
		when(costRepository.getCostAggs(anyObject())).thenReturn(getCostTrend());
		assertTrue(costService.getCostTrend(Arrays.asList("app"), new ArrayList<>(), "monthly").size() == 5);
		
		when(costRepository.getCostAggsWithTT(anyObject(), anyObject())).thenReturn(getCostTrend());
		assertTrue(costService.getCostTrend(Arrays.asList("app"), Arrays.asList("type"), "quarterly").size() == 2);
	}
	
	private List<Map<String,Object>> getCurrentCost() {
		
		List<Map<String,Object>> currentCost = new ArrayList<>();
		Map<String,Object> cost = new HashMap<>();
		cost.put("application", "app");
		cost.put("month", 4.0);
		cost.put("year", 2019.0);
		cost.put("finalised", "false");
		cost.put("totalCost", 44942.096);
		List<Map<String,Object>> typeCost = new ArrayList<>();
		Map<String,Object> type = new HashMap<>();
		type.put("cost", 44942.096);
		type.put("type", "type");
		typeCost.add(type);
		cost.put("list",typeCost);
		currentCost.add(cost);
		return currentCost;
	}
	
	private List<Map<String,Object>> getPreviousCost() {
		
		List<Map<String,Object>> previousCost = new ArrayList<>();
		Map<String,Object> cost = new HashMap<>();
		cost.put("application", "app");
		cost.put("month", 4.0);
		cost.put("year", 2019.0);
		cost.put("finalised", "false");
		cost.put("totalCost", 44942.096);
		List<Map<String,Object>> typeCost = new ArrayList<>();
		Map<String,Object> type = new HashMap<>();
		type.put("cost", 44942.096);
		type.put("type", "type");
		typeCost.add(type);
		cost.put("list",typeCost);
		previousCost.add(cost);
		return previousCost;
	}
	
	private List<ApplicationDetail> getValidApplications() {
		List<ApplicationDetail> validApplications = new ArrayList<>();
		ApplicationDetail app = new ApplicationDetail();
		app.setName("app");
		validApplications.add(app);
		return validApplications;
	}
	
	private List<Map<String,Object>> getCostTrend() {
		
		List<Map<String,Object>> costTrend = new ArrayList<>();
		Map<String,Object> trend = new HashMap<>();
		trend.put("month", 1);
		trend.put("year", 2019);
		trend.put("cost", 57221);
		trend.put("finalised", true);
		costTrend.add(trend);
		trend = new HashMap<>();
		trend.put("month", 2);
		trend.put("year", 2019);
		trend.put("cost", 50138);
		trend.put("finalised", true);
		costTrend.add(trend);
		trend = new HashMap<>();
		trend.put("month", 3);
		trend.put("year", 2019);
		trend.put("cost", 44942);
		trend.put("finalised", true);
		costTrend.add(trend);
		trend = new HashMap<>();
		trend.put("month", 4);
		trend.put("year", 2019);
		trend.put("cost", 37134);
		trend.put("finalised", false);
		costTrend.add(trend);
		trend = new HashMap<>();
		trend.put("month", 5);
		trend.put("year", 2019);
		trend.put("cost", 6517);
		trend.put("finalised", false);
		costTrend.add(trend);
		return costTrend;
	}
}
