package com.tmobile.cso.pacman.datashipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacman.datashipper.entity.AssetGroupStatsCollector;
import com.tmobile.cso.pacman.datashipper.entity.EntityManager;
import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;
import com.tmobile.cso.pacman.datashipper.util.ErrorManageUtil;
import com.tmobile.pacman.commons.jobs.PacmanJob;


/**
 * The Class Main.
 */
@PacmanJob(methodToexecute = "shipData", jobName = "Redshfit-ES-Datashipper", desc = "Job to load data from Redshfit to ES", priority = 5)
public class Main implements Constants {

    
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
    	String jobName = System.getProperty("jobName");
        List<Map<String,String>> errorList = new ArrayList<>();
        try {
			MainUtil.setup(params);
		} catch (Exception e) {
			  Map<String,String> errorMap = new HashMap<>();
              errorMap.put(ERROR, "Exception in setting up Job ");
              errorMap.put(ERROR_TYPE, WARN);
              errorMap.put(EXCEPTION, e.getMessage());
              errorList.add(errorMap);
              return ErrorManageUtil.formErrorCode(jobName, errorList);
		}
        String ds = params.get("datasource");
        ESManager.configureIndexAndTypes(ds,errorList);
        errorList.addAll(new EntityManager().uploadEntityData(ds));
        errorList.addAll(new AssetGroupStatsCollector().collectAssetGroupStats());
        Map<String, Object> status = ErrorManageUtil.formErrorCode(jobName, errorList);
        LOGGER.info("Job Return Status {} ",status);
        return status;
    }

}
