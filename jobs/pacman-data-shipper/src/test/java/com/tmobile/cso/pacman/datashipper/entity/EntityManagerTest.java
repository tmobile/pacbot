package com.tmobile.cso.pacman.datashipper.entity;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tmobile.cso.pacman.datashipper.config.ConfigManager;
import com.tmobile.cso.pacman.datashipper.dao.RDSDBManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigManager.class,ESManager.class,RDSDBManager.class})
public class EntityManagerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void uploadEntityDataTest(){
        
        PowerMockito.mockStatic(ConfigManager.class);
        List<String> types = new ArrayList<>();
        types.add("onpremserver");
        when(ConfigManager.getTypes(anyString())).thenReturn(new HashSet<>(types));
        
        PowerMockito.mockStatic(ESManager.class);
        //doNothing().when(ESManager.class);
        Map<String, Map<String, String>> currentInfo = new HashMap<>();
        currentInfo.put("id",new HashMap<>());
        when(ESManager.getExistingInfo(anyString(),anyString(), anyList())).thenReturn(currentInfo);
      /*  
        PowerMockito.mockStatic(DBManager.class);
        List<Map<String, String>> entities = new ArrayList<>();
        Map<String,String> entity = new HashMap<>();
        entity.put("id", "id");
        entity.put("discoverydate", new Date().toString());
        entity.put("u_business_service", "application");
        entity.put("used_for", "environment");
        entities.add(entity);*/
        
        List<Map<String, String>> tags = new ArrayList<>();
        Map<String,String> tag = new HashMap<>();
        tag.put("id", "");
        tags.add(tag);
        
        PowerMockito.mockStatic(RDSDBManager.class);
        List<Map<String, String>> overridableInfo = new ArrayList<>();
        Map<String,String> overridefields = new HashMap<>();
        overridefields.put("updatableFields", "id,pac_override_id");
        overridableInfo.add(overridefields);
        when(RDSDBManager.executeQuery(anyString())).thenReturn(overridableInfo);
        
        List<Map<String, String>> overrides = new ArrayList<>();
        Map<String, String> override = new HashMap<>();
        override.put("_resourceid", "id");
        overrides.add(override);
       // when(DBManager.executeQuery(anyString())).thenReturn(entities,tags,overrides);
        
        when(ConfigManager.getKeyForType(anyString(),anyString())).thenReturn("id");
        when(ConfigManager.getIdForType(anyString(),anyString())).thenReturn("id");
        
        new EntityManager().uploadEntityData("onpremserver");
    }
}
