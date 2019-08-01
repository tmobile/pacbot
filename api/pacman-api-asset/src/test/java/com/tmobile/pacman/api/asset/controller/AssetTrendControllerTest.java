package com.tmobile.pacman.api.asset.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
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

import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResponseUtils.class })
public class AssetTrendControllerTest {

    @Mock
    AssetService service;

    AssetTrendController controller = new AssetTrendController();

    @Test
    public void testgetMinMaxAssetCount() throws Exception {
        List<Map<String, Object>> trendList = new ArrayList<>();

        when(service.getAssetMinMax(anyObject(), anyObject(), anyObject(), anyObject())).thenReturn(trendList);
        ReflectionTestUtils.setField(controller, "assetService", service);

        ResponseEntity<Object> responseObj0 = controller.getMinMaxAssetCount("ag","type",null,null, "domain");
        assertTrue(responseObj0.getStatusCode() == HttpStatus.OK);
        
        doThrow(new NullPointerException()).when(service).getAssetMinMax(anyObject(), anyObject(), anyObject(), anyObject());
        ResponseEntity<Object> responseObj1 = controller.getMinMaxAssetCount("ag","type",null,null, "domain");
        assertTrue(responseObj1.getStatusCode() == HttpStatus.EXPECTATION_FAILED);
        
        
        
    }

   
}
