package com.tmobile.cso.pacman.datashipper.es;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cso.pacman.datashipper.config.ConfigManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigManager.class, EntityUtils.class, Response.class, RestClient.class})
public class ESManagerTest {
    
    @InjectMocks
    ESManager esManager;
    
    @Mock
    private RestClient restClient;
    
    @Mock
    private Response response;
    
    @Mock
    private ObjectMapper mapper;
    
    @Mock
    private StatusLine sl;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        PowerMockito.mockStatic(ConfigManager.class);
        when(ConfigManager.getKeyForType(anyString(),anyString())).thenReturn("key");
        PowerMockito.whenNew(RestClient.class).withAnyArguments().thenReturn(restClient);
    }

    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void uploadDataTest() throws Exception{
        
        List<Map<String, Object>> docs = new ArrayList<>();
        Map<String,Object> doc = new HashMap<>();
        doc.put("id", "id");
        docs.add(doc);
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.uploadData("index", "type", docs, "date");
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void uploadDataWithIdTest() throws Exception{
        
        List<Map<String, Object>> docs = new ArrayList<>();
        Map<String,Object> doc = new HashMap<>();
        doc.put("id", "id");
        docs.add(doc);
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.uploadData("index", "type", docs,"id",true);
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void configureIndexAndTypesTest() throws Exception{
        
        PowerMockito.mockStatic(ConfigManager.class);
        List<String> types = new ArrayList<>();
        types.add("ds");
        when(ConfigManager.getTypes(anyString())).thenReturn(new HashSet<>(types));
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(100);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.configureIndexAndTypes("index",new ArrayList<>());
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void getExistingInfoTest() throws Exception{
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(100);
        when(response.getStatusLine()).thenReturn(sl);
        
        String countString = "{\"count\":\"1\"}";
        String jsonString = "{\"hits\":{\"hits\":[{\"k1\":\"v1\",\"k2\":\"v2\"}]}}";
        JsonNode countObj = new ObjectMapper().readTree(countString);
        JsonNode jsonObj = new ObjectMapper().readTree(jsonString);
        when(mapper.readTree(anyString())).thenReturn(countObj,jsonObj);
        
        List<String> filters = new ArrayList<>();
        filters.add("filter");
        esManager.getExistingInfo("indexName", "type", filters);
        
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void fetchCurrentCountStatsForAssetGroupsTest() throws Exception{
        
        HttpEntity jsonEntity = new StringEntity("{\"hits\":{\"hits\":[{\"_source\":{\"ag\":\"ag\",\"type\":\"type\"}}]}}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(100);
        when(response.getStatusLine()).thenReturn(sl);
        
        assertThat(esManager.fetchCurrentCountStatsForAssetGroups("date").size(),is(1));
        
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void uploadDataWithParentTest() throws Exception{
        
        List<Map<String, Object>> docs = new ArrayList<>();
        Map<String,Object> doc = new HashMap<>();
        doc.put("id", "id");
        docs.add(doc);
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(sl);
        
        String parent = "parent";
        esManager.uploadData("index", "type", docs,parent.split(","));
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void deleteOldDocumentsTest() throws Exception{
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        
        esManager.deleteOldDocuments("index", "type", "field","value");
        
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenThrow(new IOException());
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        esManager.deleteOldDocuments("index", "type", "field","value");
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void createTypeTest() throws Exception{
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(100);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.createType("index", "type", new ArrayList<>());
        
        when(sl.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.createType("index", "type", new ArrayList<>());
        
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenThrow(new IOException());
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        esManager.createType("index", "type", new ArrayList<>());
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void createTypeWithParentTest() throws Exception{
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(100);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.createType("index","type","parent");
        
        when(sl.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.createType("index","type","parent");
        
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenThrow(new IOException());
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        esManager.createType("index","type","parent");
    }
    
    @SuppressWarnings({ "unchecked", "static-access" })
    @Test 
    public void createIndexTest() throws Exception{
        
        HttpEntity jsonEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
        when(response.getEntity()).thenReturn(jsonEntity);
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenReturn(response);
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        when(sl.getStatusCode()).thenReturn(100);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.createIndex("index", new ArrayList<>());
        
        when(sl.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(sl);
        
        esManager.createIndex("index", new ArrayList<>());
        
        when(restClient.performRequest(anyString(), anyString(), anyMap(), any(HttpEntity.class),
        Matchers.<Header>anyVararg())).thenThrow(new IOException());
        ReflectionTestUtils.setField(esManager, "restClient", restClient);
        esManager.createIndex("index", new ArrayList<>());
    }
}
