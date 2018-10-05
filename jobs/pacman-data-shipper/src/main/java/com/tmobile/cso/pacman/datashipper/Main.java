package com.tmobile.cso.pacman.datashipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacman.datashipper.config.ConfigManager;
import com.tmobile.cso.pacman.datashipper.entity.AssetGroupStatsCollector;
import com.tmobile.cso.pacman.datashipper.entity.EntityAssociationManager;
import com.tmobile.cso.pacman.datashipper.entity.EntityManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.ErrorManageUtil;
import com.tmobile.pacman.commons.jobs.PacmanJob;


/**
 * The Class Main.
 */
@PacmanJob(methodToexecute = "shipData", jobName = "Redshfit-ES-Datashipper", desc = "Job to load data from Redshfit to ES", priority = 5)
public class Main {

    
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        Map<String, String> params = new HashMap<>();
        Arrays.asList(args).stream().forEach(obj -> {
                String[] paramArray = obj.split("[:]");
                params.put(paramArray[0], paramArray[1]);
        });
        shipData(params);
        System.exit(0);
    }

    /**
     * Ship data.
     *
     * @param params
     *            the params
     * @return 
     */
    public static Map<String, Object> shipData(Map<String, String> params) {
        List<Map<String,String>> errorList = new ArrayList<>();
        MainUtil.setup(params);
        String ds = params.get("datasource");
        ESManager.configureIndexAndTypes(ds,errorList);
        errorList.addAll(new EntityManager().uploadEntityData(ds));
        errorList.addAll(new EntityAssociationManager().uploadAssociationInfo(ds));
        errorList.addAll(new AssetGroupStatsCollector().collectAssetGroupStats());
        Map<String, Object> status = ErrorManageUtil.formErrorCode("shipData", errorList);
        LOGGER.info("Job Return Status {} ",status);
        return status;
    }

}
