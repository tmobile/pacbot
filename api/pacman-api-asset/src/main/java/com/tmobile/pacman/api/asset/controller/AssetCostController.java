package com.tmobile.pacman.api.asset.controller;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.pacman.api.asset.domain.ApplicationDetail;
import com.tmobile.pacman.api.asset.domain.ApplicationDetailsResponse;
import com.tmobile.pacman.api.asset.service.AssetService;
import com.tmobile.pacman.api.asset.service.CostService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;

@RestController
@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_USER')")
@CrossOrigin
public class AssetCostController {

    @Autowired
    CostService costService;

    @Autowired
    private AssetService assetService;
    
    private static final Log LOGGER = LogFactory.getLog(AssetCostController.class);

    @GetMapping(value = "/v1/costByApplication")
    public ResponseEntity<Object> getAssetCostByApplication(
            @RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "quarter", required = false) Integer quarter,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month) {
        return getAssetCost(assetGroup, type, application, quarter, year, month, false);
    }

    @GetMapping(value = "/v1/costByType")
    public ResponseEntity<Object> getAssetCostByType(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "quarter", required = false) Integer quarter,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month) {
        return getAssetCost(assetGroup, type, application, quarter, year, month, true);
    }
    
    @GetMapping(value = "/v1/cost/trend")
    public ResponseEntity<Object> getAssetCostTrendByType(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "period", required = true) Period period) {
    	try {
    		
    		ApplicationDetailsResponse appDetailsResponse = null;
            appDetailsResponse = assetService.getApplicationDetailsByAssetGroup(assetGroup, null);
            List<ApplicationDetail> validApplications = appDetailsResponse.getValidApplications();
            List<String> appNameList = new ArrayList<>();
            Iterator<ApplicationDetail> it = validApplications.iterator();
            while (it.hasNext()) {
                String appName = it.next().getName();
                appNameList.add(appName);

            }
            
            List<String> tTypeList = new ArrayList<>();
            List<Map<String, Object>> tTypeMapList = assetService.getAllCostTypes();
            Iterator<Map<String, Object>> iter = tTypeMapList.iterator();
            while (iter.hasNext()) {
                Map<String, Object> tTypeMap = iter.next();
                tTypeList.add(tTypeMap.get("type").toString());
            }

            if (StringUtils.isNotBlank(application) && !appNameList.contains(application)) {
                return ResponseUtils.buildFailureResponse(new Exception("Invalid application entered"), null, null);
            }
            if (StringUtils.isNotBlank(type) && !tTypeList.contains(type)) {
                return ResponseUtils.buildFailureResponse(new Exception("Invalid type entered"), null, null);
            }
            if (StringUtils.isNotBlank(application)) {
                appNameList = Arrays.asList(application);
            }
            if (StringUtils.isNotBlank(type)) {
                tTypeList = Arrays.asList(type);
            }else{
            	tTypeList = new ArrayList<>();
            }
    		
    		
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("ag", assetGroup);
            if(StringUtils.isNotBlank(type)) {
                response.put("type", type);
            }
            if(StringUtils.isNotBlank(application)) {
                assetGroup = application.toLowerCase().replaceAll("[^a-z0-9-_]", "");
            }
            List<Map<String, Object>> trendList = costService.getCostTrend(appNameList,tTypeList,period.toString());
            response.put("trend", trendList);
            return ResponseUtils.buildSucessResponse(response);
        } catch (Exception e) {
            return ResponseUtils.buildFailureResponse(e);
        }
    }
    

    public ResponseEntity<Object> getAssetCost(@RequestParam(name = "ag", required = true) String assetGroup,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "quarter", required = false) Integer quarter,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "byType", required = false) boolean byType) {
        try {

            if (month != null && quarter != null) {
                return ResponseUtils.buildFailureResponse(
                        new Exception("Both quarter AND month cannot be entered together. Enter only one of these."),
                        null, null);
            }

            if(year==null && (month != null || quarter!=null)){
            	 return ResponseUtils.buildFailureResponse(new Exception("Year is mandatory"), null,
                         null);
            }
            
            if(year!=null && month == null && quarter==null){
           	 	return ResponseUtils.buildFailureResponse(new Exception("Month or quarter is mandatory a year is entered"), null,
                        null);
           }
            
            if (quarter != null && (quarter < 1 || quarter > 4)) {
                return ResponseUtils.buildFailureResponse(new Exception("Quarter should be one of 1,2,3,4"), null,
                        null);
            }

            if (month != null && (month < 1 || month > 12)) {
                return ResponseUtils.buildFailureResponse(new Exception("Month should be a number from 1-12"), null,
                        null);
            }
           
            if (year!=null && (year < 2019 || year > LocalDate.now().getYear())) {
                return ResponseUtils.buildFailureResponse(new Exception("Invalid year entered : Please enter between 2019 - "+LocalDate.now().getYear()), null, null);
            }
            if (assetService.getAssetGroupInfo(assetGroup).isEmpty()) {
                return ResponseUtils.buildFailureResponse(new Exception("Invalid asset group entered"), null, null);
            }

            ApplicationDetailsResponse appDetailsResponse = null;
            appDetailsResponse = assetService.getApplicationDetailsByAssetGroup(assetGroup, null);
            List<ApplicationDetail> validApplications = appDetailsResponse.getValidApplications();
            List<String> appNameList = new ArrayList<>();
            Iterator<ApplicationDetail> it = validApplications.iterator();
            while (it.hasNext()) {
                String appName = it.next().getName();
                appNameList.add(appName);

            }
            
            List<String> tTypeList = new ArrayList<>();
            List<Map<String, Object>> tTypeMapList = assetService.getAllCostTypes();
            Iterator<Map<String, Object>> iter = tTypeMapList.iterator();
            while (iter.hasNext()) {
                Map<String, Object> tTypeMap = iter.next();
                tTypeList.add(tTypeMap.get("type").toString());
            }

            if (StringUtils.isNotBlank(application) && !appNameList.contains(application)) {
                return ResponseUtils.buildFailureResponse(new Exception("Invalid application entered"), null, null);
            }
            if (StringUtils.isNotBlank(type) && !tTypeList.contains(type)) {
                return ResponseUtils.buildFailureResponse(new Exception("Invalid type entered"), null, null);
            }
            if (StringUtils.isNotBlank(application)) {
                appNameList = Arrays.asList(application);
            }
            if (StringUtils.isNotBlank(type)) {
                tTypeList = Arrays.asList(type);
            }else{
            	tTypeList = new ArrayList<>();
            }

            List<String> monthList = new ArrayList<>();

            if (quarter != null) {
                int monthBaseAddendum = (quarter - 1) * 3;
                int firstMonth = monthBaseAddendum + 1;
                int secondMonth = monthBaseAddendum + 2;
                int thirdMonth = monthBaseAddendum + 3;
                monthList.add(new Integer(firstMonth).toString());
                monthList.add(new Integer(secondMonth).toString());
                monthList.add(new Integer(thirdMonth).toString());
            }

            if (month != null) {
                monthList.add(month + "");
            }
            
            Map<String, Object> responseMap ;
            if (byType) {
                responseMap = costService.getCostByType(assetGroup, year, monthList, appNameList, tTypeList);
            } else{
                responseMap = costService.getCostByApplication(assetGroup, year, monthList, appNameList, tTypeList,
                        validApplications);
            }
            return ResponseUtils.buildSucessResponse(responseMap);

        } catch (Exception e) {
        	LOGGER.error("Error occured fetching cost info",e);
            return ResponseUtils.buildFailureResponse(e, null, null);
        }
    }
}

enum Period {
	monthly,quarterly;
}
