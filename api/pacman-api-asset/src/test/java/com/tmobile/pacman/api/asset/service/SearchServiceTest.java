package com.tmobile.pacman.api.asset.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyBoolean;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tmobile.pacman.api.asset.domain.ResponseWithFieldsByTargetType;
import com.tmobile.pacman.api.asset.domain.SearchCriteria;
import com.tmobile.pacman.api.asset.domain.SearchResult;
import com.tmobile.pacman.api.asset.repository.AssetRepository;
import com.tmobile.pacman.api.asset.repository.PacmanRedshiftRepository;
import com.tmobile.pacman.api.asset.repository.SearchRepository;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PacHttpUtils.class, EntityUtils.class, Response.class, RestClient.class })
public class SearchServiceTest {

    @Mock
    ElasticSearchRepository elasticSearchRepository;

    @Mock
    RestClient restClient;

    @Mock
    StatusLine sl;

    @Mock
    Response response;

    @Mock
    PacmanRdsRepository pacmanRdsRepository;

    @Mock
    SearchRepository searchRepository;

    SearchServiceImpl service = new SearchServiceImpl();

    @Test
    public void testgetSearchCategories() throws Exception {

        List<String> categs = service.getSearchCategories("Infra");
        assertTrue(categs.size() == 3);

        categs = service.getSearchCategories("other");
        assertTrue(categs.size() == 2);

    }

    @Test
    public void testSearch() throws Exception {
        SearchCriteria incomingCrit = new SearchCriteria();
        SearchResult sr = new SearchResult();
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> dataMap1 = new HashMap<>();
        dataMap1.put("_id", "a1");
        dataMap1.put("searchCategory", "Assets");
        dataList.add(dataMap1);
        sr.setResults(dataList);
        sr.setTotal(1);
        
        List<Map<String,Object>> tTypeList = new ArrayList<>();
        Map<String,Object> tTypeMap = new HashMap<>();
        tTypeMap.put("fieldName", "snapshot");
        tTypeMap.put("count",(long)1);
        tTypeList.add(tTypeMap);
        
        
        Map<String, List<Map<String, Object>>> distMap = new HashMap<>();
        List<Map<String, Object>> distList = new ArrayList();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("type", "searchFilterAttribute");
        dataMap.put("fieldName", "low");
        dataMap.put("applied", true);
        dataMap.put("groupBy", null);
        dataMap.put("count", (long)3);
        distList.add(dataMap);
        distMap.put("severity", distList);

        
        when(searchRepository.fetchSearchResultsAndSetTotal(anyString(), anyString(), anyBoolean(), anyString(),
                anyString(), anyObject(), anyInt(), anyInt(), anyObject(), anyString())).thenReturn(sr);
        when(searchRepository.fetchTargetTypes(anyString(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(tTypeList);
        when(searchRepository.fetchDistributionForTargetType(anyString(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(distMap );
        ReflectionTestUtils.setField(service, "searchRepository", searchRepository);
 
        SearchResult result = service.search(incomingCrit);
        assertTrue(result.getTotal()==1);
    }

}
