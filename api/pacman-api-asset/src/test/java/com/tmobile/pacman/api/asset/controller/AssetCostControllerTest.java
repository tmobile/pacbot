package com.tmobile.pacman.api.asset.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.tmobile.pacman.api.asset.domain.ApplicationDetail;
import com.tmobile.pacman.api.asset.domain.ApplicationDetailsResponse;
import com.tmobile.pacman.api.asset.domain.Organization;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.asset.service.CostService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class })
public class AssetCostControllerTest {

	@Mock
	CostService costService;
	
	@Mock
	AssetService assetService;
	
	AssetCostController controller = new AssetCostController();
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAssetCostByApplicationTest() throws Exception {
		Map<String, Object> agInfo = new HashMap<>();
		agInfo.put("count","1");
		when(assetService.getAssetGroupInfo(anyString())).thenReturn(agInfo);
		ReflectionTestUtils.setField(controller, "assetService", assetService);
		ApplicationDetailsResponse applications = buildApplicationDetails();
		when(assetService.getApplicationDetailsByAssetGroup(anyString(), anyString())).thenReturn(applications);
		when(assetService.getAllCostTypes()).thenReturn(buildCostTypes());
		when(costService.getCostByApplication(anyString(), anyInt(), anyList(), anyList(), anyList(), anyList())).thenReturn(new HashMap<>());
		ReflectionTestUtils.setField(controller, "costService", costService);
		ResponseEntity<Object> responseObj0 = controller.getAssetCostByApplication("assetGroupId123", null, null, null, null, null);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
        
        responseObj0 = controller.getAssetCostByApplication("assetGroupId123", "name123", null, null, null, null);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
        
        responseObj0 = controller.getAssetCostByApplication("assetGroupId123", null, "type1", null, null, null);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
		
        responseObj0 = controller.getAssetCostByApplication("assetGroupId123", null, null , 1, 2019, null);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
        
        responseObj0 = controller.getAssetCostByApplication("assetGroupId123", null, null , null, 2019, 1);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
	}
	
	@Test
	public void getAssetCostByApplicationTest_Failed() throws Exception {
		
		ResponseEntity<Object> responseObj0 = controller.getAssetCostByApplication("assetGroupId123", null, null, 1, null, 5);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        ResponseEntity<Object> responseObj1 = controller.getAssetCostByApplication("assetGroupId123", null, null, 1, null, null);
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        ResponseEntity<Object> responseObj2 = controller.getAssetCostByApplication("assetGroupId123", null, null, null, 2019, null);
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        ResponseEntity<Object> responseObj3 = controller.getAssetCostByApplication("assetGroupId123", null, null, null, null, 5);
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        ResponseEntity<Object> responseObj4 = controller.getAssetCostByApplication("assetGroupId123", null, null, 5, 2019, null);
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        responseObj4 = controller.getAssetCostByApplication("assetGroupId123", null, null, 0, 2019, null);
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        ResponseEntity<Object> responseObj5 = controller.getAssetCostByApplication("assetGroupId123", null, null, null, 2019, 13);
        assertTrue(responseObj5.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        responseObj5 = controller.getAssetCostByApplication("assetGroupId123", null, null, null, 2019, 0);
        assertTrue(responseObj5.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        ResponseEntity<Object> responseObj6 = controller.getAssetCostByApplication("assetGroupId123", null, null, 1, 2017, null);
        assertTrue(responseObj6.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        when(assetService.getAssetGroupInfo(anyString())).thenReturn(new HashMap<>());
        ReflectionTestUtils.setField(controller, "assetService", assetService);
        ResponseEntity<Object> responseObj9 = controller.getAssetCostByApplication("assetGroupId123", null, null, null, null, null);
        assertTrue(responseObj9.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        Map<String, Object> agInfo = new HashMap<>();
		agInfo.put("count","1");
		when(assetService.getAssetGroupInfo(anyString())).thenReturn(agInfo);
		ReflectionTestUtils.setField(controller, "assetService", assetService);
		ApplicationDetailsResponse applications = buildApplicationDetails();
		when(assetService.getApplicationDetailsByAssetGroup(anyString(), anyString())).thenReturn(applications);
		
        ResponseEntity<Object> responseObj7 = controller.getAssetCostByApplication("assetGroupId123", "test", null, null, null, null);
        assertTrue(responseObj7.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        ResponseEntity<Object> responseObj8 = controller.getAssetCostByApplication("assetGroupId123", null, "test", null, null, null);
        assertTrue(responseObj8.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAssetCostByApplicationTest_Exception() throws Exception {
		Map<String, Object> agInfo = new HashMap<>();
		agInfo.put("count","1");
		when(assetService.getAssetGroupInfo(anyString())).thenReturn(agInfo);
		ReflectionTestUtils.setField(controller, "assetService", assetService);
		ApplicationDetailsResponse applications = buildApplicationDetails();
		when(assetService.getApplicationDetailsByAssetGroup(anyString(), anyString())).thenReturn(applications);
		when(assetService.getAllCostTypes()).thenReturn(buildCostTypes());
		when(costService.getCostByApplication(anyString(), anyInt(), anyList(), anyList(), anyList(), anyList())).thenThrow(new Exception());
		ReflectionTestUtils.setField(controller, "costService", costService);
		ResponseEntity<Object> responseObj0 = controller.getAssetCostByApplication("assetGroupId123", null, null, null, null, null);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAssetCostByTypeTest() throws Exception {
		Map<String, Object> agInfo = new HashMap<>();
		agInfo.put("count","1");
		when(assetService.getAssetGroupInfo(anyString())).thenReturn(agInfo);
		ReflectionTestUtils.setField(controller, "assetService", assetService);
		ApplicationDetailsResponse applications = buildApplicationDetails();
		when(assetService.getApplicationDetailsByAssetGroup(anyString(), anyString())).thenReturn(applications);
		when(assetService.getAllCostTypes()).thenReturn(buildCostTypes());
		when(costService.getCostByType(anyString(), anyInt(), anyList(), anyList(), anyList())).thenReturn(new HashMap<>());
		ReflectionTestUtils.setField(controller, "costService", costService);
		ResponseEntity<Object> responseObj0 = controller.getAssetCostByType("assetGroupId123", null, null, null, null, null);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
		
	}
	
	private ApplicationDetailsResponse buildApplicationDetails()
	{
    	Organization organization = new Organization();
    	organization.setDesignation("designation123");
    	organization.setName("name123");
    	ApplicationDetail applicationDetail = new ApplicationDetail();
    	applicationDetail.setAssetGroupId("assetGroupId123");
    	applicationDetail.setDescription("description123");
    	applicationDetail.setName("name123");
    	applicationDetail.setOrganization(Lists.newArrayList(organization));
    	ApplicationDetailsResponse applicationDetailsResponse = new ApplicationDetailsResponse();
    	applicationDetailsResponse.setInvalidApplications(Lists.newArrayList(applicationDetail));
    	applicationDetailsResponse.setValidApplications(Lists.newArrayList(applicationDetail));
		return applicationDetailsResponse;
	}
	
	private List<Map<String,Object>> buildCostTypes() {
		List<Map<String,Object>> costTypes = new ArrayList<>();
		Map<String,Object> costType = new HashMap<>();
		costType.put("type", "type1");
		costTypes.add(costType);
		return costTypes;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAssetCostTrendByTypeTest() throws Exception {
		
		ApplicationDetailsResponse applications = buildApplicationDetails();
		when(assetService.getApplicationDetailsByAssetGroup(anyString(), anyString())).thenReturn(applications);
		ReflectionTestUtils.setField(controller, "assetService", assetService);
		when(assetService.getAllCostTypes()).thenReturn(buildCostTypes());
		when(costService.getCostTrend(anyList(), anyList(), anyString())).thenReturn(new ArrayList<>());
		ReflectionTestUtils.setField(controller, "costService", costService);
		ResponseEntity<Object> responseObj0 = controller.getAssetCostTrendByType("assetGroupId123", null, null, Period.monthly);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
        
        responseObj0 = controller.getAssetCostTrendByType("assetGroupId123", "name123", null, Period.monthly);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
        
        responseObj0 = controller.getAssetCostTrendByType("assetGroupId123", null, "type1", Period.quarterly);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getAssetCostTrendByTypeTest_Failed() throws Exception {
		
		ResponseEntity<Object> responseObj7 = controller.getAssetCostTrendByType("assetGroupId123", "test", null, Period.monthly);
        assertTrue(responseObj7.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        ResponseEntity<Object> responseObj8 = controller.getAssetCostTrendByType("assetGroupId123", null, "test", Period.monthly);
        assertTrue(responseObj8.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
		ApplicationDetailsResponse applications = buildApplicationDetails();
		when(assetService.getApplicationDetailsByAssetGroup(anyString(), anyString())).thenReturn(applications);
		ReflectionTestUtils.setField(controller, "assetService", assetService);
		when(assetService.getAllCostTypes()).thenReturn(buildCostTypes());
		when(costService.getCostTrend(anyList(), anyList(), anyString())).thenThrow(new Exception());
		ReflectionTestUtils.setField(controller, "costService", costService);
		ResponseEntity<Object> responseObj0 = controller.getAssetCostTrendByType("assetGroupId123", null, null, Period.monthly);
        assertTrue(responseObj0.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
		
	}
}
