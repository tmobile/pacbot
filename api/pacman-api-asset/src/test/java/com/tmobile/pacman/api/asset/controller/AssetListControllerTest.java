package com.tmobile.pacman.api.asset.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doThrow;
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

import com.tmobile.pacman.api.asset.domain.Request;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class })
public class AssetListControllerTest {

    @Mock
    AssetService service;

    AssetListController controller = new AssetListController();

    @Test
    public void testlistAssets() throws Exception {
        List<Map<String, Object>> aList = new ArrayList<>();
        Request request = new Request();

        when(service.getAssetCount(anyObject(), anyObject(), anyObject())).thenReturn((long)100);
        when(service.getListAssets(anyObject(), anyObject(), anyInt(),anyInt(),anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj0 = controller.listAssets(request, "domain");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
        
        request.setFrom(-1);
        ResponseEntity<Object> responseObj = controller.listAssets(request, "domain");
        assertTrue(responseObj.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        request.setFrom(0);

        Map<String,String> filter = new HashMap<>();
        filter.put("filterKey", "filterValue");
        request.setFilter(filter);
        when(service.getListAssets(anyObject(), anyObject(), anyInt(),anyInt(),anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj1 = controller.listAssets(request, "domain");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFilter(null);
        doThrow(new NullPointerException()).when(service).getListAssets(anyObject(), anyObject(), anyInt(),anyInt(),anyObject());
        ResponseEntity<Object> responseObj2 = controller.listAssets(request, "domain");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
       
        
    }
    @Test
    public void testlistTaggableAssets() throws Exception{
        List<Map<String, Object>> aList = new ArrayList<>();
        Request request = new Request();

        ResponseEntity<Object> responseObj1 = controller.listTaggableAssets(request);
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(-1);
        request.setAg("ag");
        ResponseEntity<Object> responseObj2 = controller.listTaggableAssets(request);
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(0);
        ResponseEntity<Object> responseObj3 = controller.listTaggableAssets(request);
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        
        Map<String,String> filter = new HashMap<>();
        filter.put("filterKey", "filterValue");
        request.setFilter(filter);
        ResponseEntity<Object> responseObj4 = controller.listTaggableAssets(request);
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        filter.clear();
        filter.put("tagName", "filterValue");
        request.setFilter(filter);
        when(service.getListAssetsTaggable(anyString(), anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj5 = controller.listTaggableAssets(request);
        assertTrue(responseObj5.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        filter.clear();
        request.setFilter(filter);
        when(service.getListAssetsTaggable(anyString(), anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj6 = controller.listTaggableAssets(request);
        assertTrue(responseObj6.getStatusCode() == HttpStatus.OK);

    }
    
    @Test
    public void testlistPatchableAssets() throws Exception{
        List<Map<String, Object>> aList = new ArrayList<>();
        Request request = new Request();

        ResponseEntity<Object> responseObj1 = controller.listPatchableAssets(request);
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(-1);
        request.setAg("ag");
        ResponseEntity<Object> responseObj2 = controller.listPatchableAssets(request);
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(0);
        ResponseEntity<Object> responseObj3 = controller.listPatchableAssets(request);
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        
        Map<String,String> filter = new HashMap<>();
        filter.put("filterKey", "filterValue");
        request.setFilter(filter);
        ResponseEntity<Object> responseObj4 = controller.listPatchableAssets(request);
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        filter.clear();
        filter.put("resourceType", "filterValue");
        request.setFilter(filter);
        when(service.getListAssetsPatchable(anyString(), anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj5 = controller.listPatchableAssets(request);
        assertTrue(responseObj5.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        filter.clear();
        request.setFilter(filter);
        when(service.getListAssetsPatchable(anyString(), anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj6 = controller.listPatchableAssets(request);
        assertTrue(responseObj6.getStatusCode() == HttpStatus.OK);
        
        Map<String,Object> aMap = new HashMap<>();
        aMap.put("type", "ec2");
        aList.add(aMap);
        request.setFrom(3);
        ResponseEntity<Object> responseObj7 = controller.listPatchableAssets(request);
        assertTrue(responseObj7.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        request.setFrom(0);
        ResponseEntity<Object> responseObj8 = controller.listPatchableAssets(request);
        assertTrue(responseObj8.getStatusCode() == HttpStatus.OK);
        
        request.setSize(8);
        ResponseEntity<Object> responseObj9 = controller.listPatchableAssets(request);
        assertTrue(responseObj9.getStatusCode() == HttpStatus.OK);
        
    }

    @Test
    public void testlistVulnerableAssets() throws Exception{
        List<Map<String, Object>> aList = new ArrayList<>();
        Request request = new Request();

        ResponseEntity<Object> responseObj1 = controller.listVulnerableAssets(request);
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(-1);
        request.setAg("ag");
        ResponseEntity<Object> responseObj2 = controller.listVulnerableAssets(request);
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(0);
        ResponseEntity<Object> responseObj3 = controller.listVulnerableAssets(request);
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        
        Map<String,String> filter = new HashMap<>();
        filter.put("filterKey", "filterValue");
        filter.put("qid", "filterValue");
        request.setFilter(filter);
        ResponseEntity<Object> responseObj4 = controller.listVulnerableAssets(request);
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        filter.remove("qid");
        filter.put("resourceType", "filterValue");
        request.setFilter(filter);
        when(service.getListAssetsVulnerable(anyString(), anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj5 = controller.listVulnerableAssets(request);
        assertTrue(responseObj5.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        filter.clear();
        filter.put("qid", "filterValue");
        request.setFilter(filter);
        when(service.getListAssetsVulnerable(anyString(), anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj6 = controller.listVulnerableAssets(request);
        assertTrue(responseObj6.getStatusCode() == HttpStatus.OK);

    }
    
    @Test
    public void testlistScannedAssets() throws Exception{
        List<Map<String, Object>> aList = new ArrayList<>();
        Request request = new Request();

        ResponseEntity<Object> responseObj1 = controller.listScannedAssets(request,"domain");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(-1);
        request.setAg("ag");
        ResponseEntity<Object> responseObj2 = controller.listScannedAssets(request,"domain");
        assertTrue(responseObj2.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(0);
        ResponseEntity<Object> responseObj3 = controller.listScannedAssets(request,"domain");
        assertTrue(responseObj3.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        
        Map<String,String> filter = new HashMap<>();
        filter.put("filterKey", "filterValue");
        filter.put("ruleId", "filterValue");
        request.setFilter(filter);
        ResponseEntity<Object> responseObj4 = controller.listScannedAssets(request,"domain");
        assertTrue(responseObj4.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        filter.remove("ruleId");
        filter.put("resourceType", "filterValue");
        request.setFilter(filter);
        when(service.getListAssetsScanned(anyString(), anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj5 = controller.listScannedAssets(request,"domain");
        assertTrue(responseObj5.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        filter.clear();
        filter.put("ruleId", "filterValue");
        request.setFilter(filter);
        when(service.getListAssetsScanned(anyString(), anyObject())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj6 = controller.listScannedAssets(request,"domain");
        assertTrue(responseObj6.getStatusCode() == HttpStatus.OK);

    }
    @Test 
    public void testgetAssetLists() throws Exception{
        Request request = new Request();
        ResponseEntity<Object> responseObj1 = controller.getAssetLists(request);
        assertTrue(responseObj1.getStatusCode()==HttpStatus.EXPECTATION_FAILED);
        
        request.setAg("ag");
        ResponseEntity<Object> responseObj2 = controller.getAssetLists(request);
        assertTrue(responseObj2.getStatusCode()==HttpStatus.EXPECTATION_FAILED);
        
        Map<String,String> filter = new HashMap<>();
        filter.put("resourceType", "filterValue");
        request.setFilter(filter);
        ResponseEntity<Object> responseObj3 = controller.getAssetLists(request);
        assertTrue(responseObj3.getStatusCode()==HttpStatus.EXPECTATION_FAILED);
        
        request.setFrom(-1);
        request.setSearchtext("pacman");
        request.toString();
        assertTrue(request.getKey().contains("pacman"));
        ResponseEntity<Object> responseObj4 = controller.getAssetLists(request);
        assertTrue(responseObj4.getStatusCode()==HttpStatus.EXPECTATION_FAILED);
        request.setFrom(0);
      
        filter.put("filterType", "filterValue");
        ResponseEntity<Object> responseObj5 = controller.getAssetLists(request);
        assertTrue(responseObj5.getStatusCode()==HttpStatus.EXPECTATION_FAILED);
        
        filter.remove("filterType");
        ResponseEntity<Object> responseObj6 = controller.getAssetLists(request);
        assertTrue(responseObj6.getStatusCode()==HttpStatus.EXPECTATION_FAILED);
        
        List<Map<String, Object>> aList = new ArrayList<>();
        when(service.getAssetLists(anyString(), anyObject(),anyInt(),anyInt(),anyString())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj7 = controller.getAssetLists(request);
        assertTrue(responseObj7.getStatusCode()==HttpStatus.OK);
    }
    
    @Test
    public void testgetEditableFieldsByTargetType() throws Exception{
        ResponseEntity<Object> responseObj1 = controller.getEditableFieldsByTargetType("","ec2");
        assertTrue(responseObj1.getStatusCode()==HttpStatus.EXPECTATION_FAILED);
       
        List<Map<String, Object>> aList = new ArrayList<>();
        Map<String,Object> aMap = new HashMap<>();
        aMap.put("type", "ec2");
        aList.add(aMap);
        
        when(service.getTargetTypesForAssetGroup(anyString(),anyString(),anyString())).thenReturn(aList);
        ReflectionTestUtils.setField(controller, "assetService", service);
        ResponseEntity<Object> responseObj2 = controller.getEditableFieldsByTargetType("ag","ec2");
        assertTrue(responseObj2.getStatusCode()==HttpStatus.OK);
        
        ResponseEntity<Object> responseObj3 = controller.getEditableFieldsByTargetType("ag","s3");
        assertTrue(responseObj3.getStatusCode()==HttpStatus.EXPECTATION_FAILED);
      
    }
}
