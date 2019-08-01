package com.tmobile.pacman.api.asset.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tmobile.pacman.api.asset.domain.ApplicationDetail;
import com.tmobile.pacman.api.asset.domain.Organization;
import com.tmobile.pacman.api.asset.repository.AssetRepository;
import com.tmobile.pacman.api.asset.repository.CostRepository;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;

@Service
public class CostService {
    /** The es host. */
    @Value("${elastic-search.host}")
    private String esHost;

    /** The es port. */
    @Value("${elastic-search.port}")
    private int esPort;
    
    /** The cost indicator percent. */
    @Value("${cost-indicator.percent}")
    private int costIndicatorPercent;

    /** The Constant PROTOCOL. */
    static final String PROTOCOL = "http";

    /** The es url. */
    private String esUrl;

    /** The elastic search repository. */
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private CostRepository costRepository;

    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(CostService.class);
    
    @SuppressWarnings("deprecation")
	public Map<String, Object> getCostByType(String assetGroup, Integer year, List<String> monthList,
            List<String> appNameList, List<String> tTypeList) throws Exception {

    	Integer prevYear = year;
    	List<String> prevMonthList = new ArrayList<>();
    	if(year==null && monthList.isEmpty()){ // Defaulted to latest finalised
    		Map<String,Integer> yearMonth = costRepository.findLatestCostFinalisedMonth();
    		if(yearMonth.isEmpty()){
    			throw new ServiceException("Could not find cost as year/month to fetch the data can not be determined");
    		}
    		year = yearMonth.get("year");
    		monthList.add(yearMonth.get("month")+"");
    		Integer currMonth = Integer.valueOf(yearMonth.get("month").toString());
    		if(currMonth >1 ) {
    			prevYear = year;
    			prevMonthList.add(String.valueOf(currMonth-1)+"");
    		} else {
    			prevYear = year -1;
    			prevMonthList.add("12");
    		}
    	} else {
    		if(monthList.size() > 1) {
    			if(monthList.contains("1") && monthList.contains("2") && monthList.contains("3")) {
    				prevYear = year-1;
    				prevMonthList.add("10");
    				prevMonthList.add("11");
    				prevMonthList.add("12");
    			} else {
	    			for(String month : monthList) {
	    				prevMonthList.add(String.valueOf(Integer.valueOf(month)-1)+"");
	    			}
    			}
    			
    		} else {
    			if(Integer.valueOf(monthList.get(0)) >1 ) {
        			prevYear = year;
        			prevMonthList.add(String.valueOf(Integer.valueOf(monthList.get(0))-1)+"");
        		} else {
        			prevYear = year -1;
        			prevMonthList.add("12");
        		}
    		}
    	}
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put("year", year);
        mustTermsFilter.put(CommonUtils.convertAttributetoKeyword("application"), filterApps(appNameList));
        if(!tTypeList.isEmpty()){
        	mustTermsFilter.put(CommonUtils.convertAttributetoKeyword("costInfo.type"), tTypeList);
        }
        mustTermsFilter.put("month", monthList);
        List<Map<String, Object>> currResult = elasticSearchRepository.getDataFromES("aws-cost", "monthly-cost", mustFilter,
                null, null, null, mustTermsFilter);
        mustFilter.put("year", prevYear);
        mustTermsFilter.put("month", prevMonthList);
        List<Map<String, Object>> prevResult = elasticSearchRepository.getDataFromES("aws-cost", "monthly-cost", mustFilter,
                null, null, null, mustTermsFilter);
        
        boolean finalised = true;
        for(Map<String, Object> resultMap :currResult){
        	finalised = finalised && Boolean.valueOf(resultMap.get("finalised").toString());
        }
        List<Map<String, Object>> currCostByTypeMapList = formCostByType(currResult, tTypeList);
        List<Map<String, Object>> prevCostByTypeMapList = formCostByType(prevResult, tTypeList);
        
        findCostIndicatorByType(currCostByTypeMapList, prevCostByTypeMapList);
        
        LocalDate date1 = LocalDate.of(year, new Integer(monthList.get(0)), 1);
        LocalDate start = date1.withDayOfMonth(1);

        LocalDate date2 = LocalDate.of(year, new Integer(monthList.get(monthList.size() - 1)), 1);
        LocalDate end = date2.withDayOfMonth(date2.lengthOfMonth());

        responseMap.put("ag", assetGroup);
        responseMap.put("startDate", start.format(DateTimeFormatter.ISO_LOCAL_DATE));
        responseMap.put("endDate", end.format(DateTimeFormatter.ISO_LOCAL_DATE));
        responseMap.put("costByType", currCostByTypeMapList);
        responseMap.put("finalised", finalised);
        
        responseMap.put("year", start.getYear());
        if(start.getMonthValue() == end.getMonth().getValue()){
        	 responseMap.put("month", start.getMonthValue());
        }else{
        	 responseMap.put("quarter", ((start.getMonthValue()-1)/3)+1);
        }
        return responseMap;

    }
    
    private List<Map<String, Object>> formCostByType(List<Map<String, Object>> result, List<String> tTypeList) {
    	
    	Map<String, Map<String,Long>> typeToAppMap = new HashMap<>();
    	for(Map<String, Object> resultMap :result){
         	String application = resultMap.get("application").toString();
         	String typeCostListJson = resultMap.get("list").toString();
         	List<Map<String,Object>> typeCostList =  new Gson().fromJson(typeCostListJson, new TypeToken<ArrayList<Map<String, Object>>>() {}.getType());
         	typeCostList.stream().filter(typeCost ->  tTypeList.isEmpty()?true:tTypeList.contains(typeCost.get("type").toString()) ).forEach(typeCost-> {
	     		String type = typeCost.get("type").toString();
	     	    long cost = Math.round(Double.valueOf(typeCost.get("cost").toString()));
	     	    if(cost>0){
	        	    Map<String,Long> appMap = typeToAppMap.get(type);
	        	    if(appMap==null){
	        	    	appMap = new HashMap<>();
	        	    	typeToAppMap.put(type, appMap);
	        	    }
	        	    Long currCost = appMap.get(application);
	        	    currCost = currCost==null?0l:currCost;
	        	    appMap.put(application, cost+currCost);
	     	    }
         	});
        }
    	
		List<String> typeList = new ArrayList<String>(typeToAppMap.keySet());
		List<Map<String, Object>> typeDataSource = assetRepository.getDatasourceForCostMapping(typeList);
        
        List<Map<String, Object>> costByTypeMapList = new ArrayList<>();
        Iterator<String> typeIterator = typeToAppMap.keySet().iterator();
        while (typeIterator.hasNext()) {
            String typeValue = typeIterator.next();
            Map<String, Object> typeDataMap = new HashMap<>();
            typeDataMap.put("type", typeValue);
            
            typeDataMap.put(Constants.PROVIDER,
					typeDataSource.stream()
							.filter(datasource -> datasource.get(Constants.TYPE).toString().equals(typeValue))
							.findFirst().get().get(Constants.PROVIDER));

            Map<String, Long> appMap = (typeToAppMap.get(typeValue) != null)
                    ? ((Map<String, Long>) (typeToAppMap.get(typeValue)))
                    : new HashMap<>();

            Double typeCost = appMap.values().stream().mapToDouble(value -> value).sum();
            typeDataMap.put("typeTotalCost", typeCost);
            List<Map<String, Object>> appLineItemsMapList = new ArrayList<>();
            typeDataMap.put("applicationLineItems", appLineItemsMapList);

            Iterator<String> appIterator = appMap.keySet().iterator();
            while (appIterator.hasNext()) {
                Map<String, Object> appLineItemsMap = new HashMap<>();
                String appName = appIterator.next();
                Long cost = appMap.get(appName);
                appLineItemsMap.put("lineItemName", appName);
                appLineItemsMap.put("lineItemCost", cost);
                appLineItemsMapList.add(appLineItemsMap);
            }

            costByTypeMapList.add(typeDataMap);
        }
    	
    	return costByTypeMapList;
    }
    
    private void findCostIndicatorByType(List<Map<String, Object>> currCostList, List<Map<String, Object>> prevCostList) {
    	for(Map<String,Object> currCost : currCostList) {
    		String costIndicator = "NC";
    		for(Map<String,Object> prevCost : prevCostList) {
        		if(currCost.get("type").equals(prevCost.get("type"))) {
        			Double currTotalCost = Double.valueOf(currCost.get("typeTotalCost").toString());
        			Double prevTotalCost = Double.valueOf(prevCost.get("typeTotalCost").toString());
        			Double percentage = (currTotalCost-prevTotalCost)/prevTotalCost*100;
        			if(percentage>0){
        				if(percentage>costIndicatorPercent) {
        					costIndicator = "UP";
        				}
        			} else if(percentage<0){
        				costIndicator = "DOWN";
        			}
        			break;
        		}
        	}
    		currCost.put("costTrendIndicator", costIndicator);
    	}
    }

    @SuppressWarnings("deprecation")
	public Map<String, Object> getCostByApplication(String assetGroup, Integer year, List<String> monthList,
            List<String> appNameList, List<String> tTypeList, List<ApplicationDetail> validApplications)
            throws Exception {
    	
    	Integer prevYear = year;
    	List<String> prevMonthList = new ArrayList<>();
    	if(year==null && monthList.isEmpty()){ // Defaulted to latest finalised
    		Map<String,Integer> yearMonth = costRepository.findLatestCostFinalisedMonth();
    		if(yearMonth.isEmpty()){
    			throw new ServiceException("Could not find cost as year/month to fetch the data can not be determined");
    		}
    		year = yearMonth.get("year");
    		monthList.add(yearMonth.get("month")+"");
    		Integer currMonth = Integer.valueOf(yearMonth.get("month").toString());
    		if(currMonth >1 ) {
    			prevYear = year;
    			prevMonthList.add(String.valueOf(currMonth-1)+"");
    		} else {
    			prevYear = year -1;
    			prevMonthList.add("12");
    		}
    	} else {
    		if(monthList.size() > 1) {
    			if(monthList.contains("1") && monthList.contains("2") && monthList.contains("3")) {
    				prevYear = year-1;
    				prevMonthList.add("10");
    				prevMonthList.add("11");
    				prevMonthList.add("12");
    			} else {
	    			for(String month : monthList) {
	    				prevMonthList.add(String.valueOf(Integer.valueOf(month)-1)+"");
	    			}
    			}
    			
    		} else {
    			if(Integer.valueOf(monthList.get(0)) >1 ) {
        			prevYear = year;
        			prevMonthList.add(String.valueOf(Integer.valueOf(monthList.get(0))-1)+"");
        		} else {
        			prevYear = year -1;
        			prevMonthList.add("12");
        		}
    		}
    	}

        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> mustFilter = new HashMap<>();
        Map<String, Object> mustTermsFilter = new HashMap<>();
        mustFilter.put("year", year);
        mustTermsFilter.put(CommonUtils.convertAttributetoKeyword("application"), filterApps(appNameList));
        if(!tTypeList.isEmpty()){
        	mustTermsFilter.put(CommonUtils.convertAttributetoKeyword("costInfo.type"), tTypeList);
        }
        mustTermsFilter.put("month", monthList);
        List<Map<String, Object>> currResult = elasticSearchRepository.getDataFromES("aws-cost", "monthly-cost", mustFilter,
                null, null, null, mustTermsFilter);
        
        mustFilter.put("year", prevYear);
        mustTermsFilter.put("month", prevMonthList);
        List<Map<String, Object>> prevResult = elasticSearchRepository.getDataFromES("aws-cost", "monthly-cost", mustFilter,
                null, null, null, mustTermsFilter);
        
        boolean finalised = true;
        for(Map<String, Object> resultMap :currResult){
        	finalised = finalised && Boolean.valueOf(resultMap.get("finalised").toString());
        }
        
        List<Map<String, Object>> currCostByAPPMapList = formCostByApplication(currResult, tTypeList, validApplications);
        List<Map<String, Object>> prevCostByAPPMapList = formCostByApplication(prevResult, tTypeList, validApplications);
        
        findCostIndicatorByApplication(currCostByAPPMapList,prevCostByAPPMapList);

        LocalDate date1 = LocalDate.of(year, new Integer(monthList.get(0)), 1);
        LocalDate start = date1.withDayOfMonth(1);

        LocalDate date2 = LocalDate.of(year, new Integer(monthList.get(monthList.size() - 1)), 1);
        LocalDate end = date2.withDayOfMonth(date2.lengthOfMonth());
       

        responseMap.put("ag", assetGroup);
        responseMap.put("finalised", finalised);
        responseMap.put("startDate", start.format(DateTimeFormatter.ISO_LOCAL_DATE));
        responseMap.put("endDate", end.format(DateTimeFormatter.ISO_LOCAL_DATE));
        responseMap.put("costByApplication", currCostByAPPMapList);
        
        responseMap.put("year", start.getYear());
        if(start.getMonthValue() == end.getMonth().getValue()){
        	responseMap.put("month", start.getMonthValue());
        }else{
        	responseMap.put("quarter", ((start.getMonthValue()-1)/3)+1);
        }

        return responseMap;
    }
    
private List<Map<String, Object>> formCostByApplication(List<Map<String, Object>> result, List<String> tTypeList, List<ApplicationDetail> validApplications) {
    	
    	Map<String, Map<String,Long>> appToTypeMap = new HashMap<>();
    	for(Map<String, Object> resultMap :result){
        	String appllcation = resultMap.get("application").toString();
        	String typeCostListJson = resultMap.get("list").toString();
        	List<Map<String,Object>> typeCostList =  new Gson().fromJson(typeCostListJson, new TypeToken<ArrayList<Map<String, Object>>>() {}.getType());
        	if(!typeCostList.isEmpty()){
	        	typeCostList.stream().filter(typeCost -> tTypeList.isEmpty()?true:tTypeList.contains(typeCost.get("type").toString()) ).forEach(typeCost-> {
	        		String type = typeCost.get("type").toString();
	        	    long cost = Math.round(Double.valueOf(typeCost.get("cost").toString()));
	        	    Map<String,Long> typeMap = appToTypeMap.get(appllcation);
	        	    if(typeMap==null){
	        	    	typeMap = new HashMap<>();
	        	    	appToTypeMap.put(appllcation, typeMap);
	        	    }
	        	    Long currCost = typeMap.get(type);
	        	    currCost = currCost==null?0l:currCost;
	        	    typeMap.put(type, cost+currCost);
		        	
	        	});
        	}else{
        		Map<String,Long>  typeMap = new HashMap<>();
        		typeMap.put("", 0l);
    	    	appToTypeMap.put(appllcation, typeMap);
        	}

        }
        
        List<Map<String, Object>> costByApplicationMapList = new ArrayList<>();
        Iterator<String> appIterator = appToTypeMap.keySet().iterator();
        while (appIterator.hasNext()) {
            String appValue = appIterator.next();
            Map<String, Object> applicationDataMap = new HashMap<>();
            applicationDataMap.put("name", appValue);
            List<Organization> orgList = getOrganization(appValue, validApplications);
            if (null != orgList && !orgList.isEmpty()) {
                applicationDataMap.put("organization", orgList);
            }

            Map<String, Long> typeMap = (appToTypeMap.get(appValue) != null)
                    ? ( (appToTypeMap.get(appValue)))
                    : new HashMap<>();

            Long applicationCost = typeMap.values().stream().mapToLong(value -> value).sum();
            applicationDataMap.put("applicationTotalCost", applicationCost);
            List<Map<String, Object>> typeLineItemsMapList = new ArrayList<>();
            applicationDataMap.put("typeLineItems", typeLineItemsMapList);

            Iterator<String> typeIterator = typeMap.keySet().iterator();
            while (typeIterator.hasNext()) {
                Map<String, Object> typeLineItemsMap = new HashMap<>();
                String typeName = typeIterator.next();
                Long cost = typeMap.get(typeName);
                if(cost>0){
                	typeLineItemsMap.put("lineItemName", typeName);
                	typeLineItemsMap.put("lineItemCost", cost);
                	typeLineItemsMapList.add(typeLineItemsMap);
                }
            }

            costByApplicationMapList.add(applicationDataMap);
        }
    	return costByApplicationMapList;
    }
    
    private void findCostIndicatorByApplication(List<Map<String, Object>> currCostList, List<Map<String, Object>> prevCostList) {
    	for(Map<String,Object> currCost : currCostList) {
    		String costIndicator = "NC";
    		for(Map<String,Object> prevCost : prevCostList) {
        		if(currCost.get("name").equals(prevCost.get("name"))) {
        			Double currTotalCost = Double.valueOf(currCost.get("applicationTotalCost").toString());
        			Double prevTotalCost = Double.valueOf(prevCost.get("applicationTotalCost").toString());
        			Double percentage = (currTotalCost-prevTotalCost)/prevTotalCost*100;
        			if(percentage>0){
        				if(percentage>5) {
        					costIndicator = "UP";
        				}
        			} else {
        				costIndicator = "DOWN";
        			}
        			break;
        		}
        	}
    		currCost.put("costTrendIndicator", costIndicator);
    	}
    }

    private List<Organization> getOrganization(String appName, List<ApplicationDetail> validApplications) {
        Iterator<ApplicationDetail> appDetailIterator = validApplications.iterator();
        while (appDetailIterator.hasNext()) {
            ApplicationDetail appDetail = appDetailIterator.next();
            if (appName.equals(appDetail.getName())) {
                return appDetail.getOrganization();
            }
        }
        return null;
    }
    
    
    private List<String> filterApps(List<String> apps){
    	List<String> masterList = costRepository.fetchApplicationMasterList();
    	if(masterList.isEmpty()){
    		return apps;
    	}else{
    		return apps.stream().filter(masterList::contains).collect(Collectors.toList());
    	}
    }

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getCostTrend(List<String> appNameList, List<String> tTypeList, String period) throws Exception {
		
		List<Map<String, Object>> trendList = new ArrayList<>();
		
		if(tTypeList.isEmpty()) {
			trendList.addAll(costRepository.getCostAggs(appNameList));
		} else {
			trendList.addAll(costRepository.getCostAggsWithTT(appNameList, tTypeList));
		}
		sortByYearAndMonth(trendList,"month");
		if("monthly".equals(period)) {
			return trendList;
		} else {
			Map<String,Object> yearMap =  new HashMap<>(); 
			for(Map<String, Object> trend : trendList) {
				if(!yearMap.isEmpty() && yearMap.containsKey(trend.get("year").toString())) {
					Map<Integer,Object> quarterMap = (Map<Integer, Object>) yearMap.get(trend.get("year").toString());
					int quarter = ((Integer.valueOf(trend.get("month").toString())-1) / 3)+1;
					if(quarterMap.containsKey(quarter)) {
						Map<String,Object> costMap = (Map<String, Object>) quarterMap.get(quarter);
						costMap.put("cost",  Double.valueOf(costMap.get("cost").toString()) + Double.valueOf(trend.get("cost").toString()));
						costMap.put("costStatus",  trend.get("costStatus"));
						quarterMap.put(quarter, costMap);
					} else {
						Map<String,Object> costMap =  new HashMap<>();
						costMap.put("cost", trend.get("cost"));
						costMap.put("costStatus", trend.get("costStatus"));
						quarterMap.put(quarter, costMap);
					}
				} else {
					Map<Integer,Object> quarterMap =  new HashMap<>();
					Map<String,Object> costMap =  new HashMap<>();
					costMap.put("cost", trend.get("cost"));
					costMap.put("costStatus", trend.get("costStatus"));
					quarterMap.put(((Integer.valueOf(trend.get("month").toString())-1) / 3)+1, costMap);
					yearMap.put(trend.get("year").toString(), quarterMap);
				}
			}
			
			trendList = new ArrayList<>(); 
			
		        
			for(Entry<String, Object> year : yearMap.entrySet()) {
				for(Entry<Integer, Object> quarter : ((Map<Integer,Object>)year.getValue()).entrySet()) {
					Map<String,Object> trend = new HashMap<>();
					trend.put("year", year.getKey());
					trend.put("quarter", quarter.getKey());
					Map<String,Object> cost = (Map<String,Object>)quarter.getValue();
					trend.put("cost", cost.get("cost"));
					trend.put("costStatus", cost.get("costStatus"));
					trendList.add(trend);
				}
			}
			sortByYearAndMonth(trendList,"quarter");
			return trendList;
		}
	}
	
	private void sortByYearAndMonth(List<Map<String, Object>> trendList, String compartor) {
		Comparator<Map<String, Object>> comp1 = (m1, m2) -> Integer.compare(
                new Integer(m1.get("year").toString()), new Integer(m2.get("year").toString()));
		Collections.sort(trendList, comp1);
		
		Comparator<Map<String, Object>> comp2 = (m1, m2) -> Integer.compare(
                new Integer(m1.get(compartor).toString()), new Integer(m2.get(compartor).toString()));
		Collections.sort(trendList, comp2);
	}
}


