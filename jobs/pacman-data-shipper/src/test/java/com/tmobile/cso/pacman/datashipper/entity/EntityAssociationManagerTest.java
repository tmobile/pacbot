package com.tmobile.cso.pacman.datashipper.entity;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
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
import com.tmobile.cso.pacman.datashipper.es.ESManager;
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigManager.class,ESManager.class})
public class EntityAssociationManagerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void uploadAssociationInfoTest() {
        
        PowerMockito.mockStatic(ConfigManager.class);
        List<String> types = new ArrayList<>();
        types.add("type1");
        when(ConfigManager.getTypes(anyString())).thenReturn(new HashSet<>(types));
        
       /* PowerMockito.mockStatic(DBManager.class);
        List<String> childTableNames = new ArrayList<>();
        childTableNames.add("child_table");
        when(DBManager.getChildTableNames(anyString())).thenReturn(childTableNames);
        */
        when(ConfigManager.getKeyForType(anyString(), anyString())).thenReturn("type");
        
        PowerMockito.mockStatic(ESManager.class);
        doNothing().when(ESManager.class);
        ESManager.createType(anyString(), anyString(),anyList());
        
        List<Map<String, String>> entities = new ArrayList<>();
        entities.add(new HashMap<>());
        //when(DBManager.executeQuery(anyString())).thenReturn(entities);
        ESManager.uploadData(anyString(), anyString(), anyList(), anyString(), anyBoolean());
        ESManager.deleteOldDocuments(anyString(), anyString(), anyString(), anyString());
        
        new EntityAssociationManager().uploadAssociationInfo("dataSource","type");
    }
}
