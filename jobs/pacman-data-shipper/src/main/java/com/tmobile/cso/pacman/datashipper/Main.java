package com.tmobile.cso.pacman.datashipper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.tmobile.cso.pacman.datashipper.entity.AssetGroupStatsCollector;
import com.tmobile.cso.pacman.datashipper.entity.EntityAssociationManager;
import com.tmobile.cso.pacman.datashipper.entity.EntityManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.pacman.commons.jobs.PacmanJob;


/**
 * The Class Main.
 */
@PacmanJob(methodToexecute = "shipData", jobName = "Redshfit-ES-Datashipper", desc = "Job to load data from Redshfit to ES", priority = 5)
public class Main {

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
     */
    public static void shipData(Map<String, String> params) {
        MainUtil.setup(params);
        String ds = params.get("datasource");
        ESManager.configureIndexAndTypes(ds);
        new EntityManager().uploadEntityData(ds);
        new EntityAssociationManager().uploadAssociationInfo(ds);
        new AssetGroupStatsCollector().collectAssetGroupStats();
    }

}
