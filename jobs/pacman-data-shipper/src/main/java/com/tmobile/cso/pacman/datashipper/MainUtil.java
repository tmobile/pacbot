package com.tmobile.cso.pacman.datashipper;

import java.util.Map;

import com.tmobile.cso.pacman.datashipper.util.ConfigUtil;
import com.tmobile.cso.pacman.datashipper.util.Constants;


/**
 * The Class MainUtil.
 */
public class MainUtil {

    /**
     * Setup.
     *
     * @param params
     *            the params
     */
    public static void setup(Map<String, String> params) throws Exception {
    	
    	ConfigUtil.setConfigProperties(params.get(Constants.CONFIG_CREDS));
    	
        if( !(params==null || params.isEmpty())){
			params.forEach((k,v) -> System.setProperty(k, v));
		}
        
        if(params.get(Constants.CONFIG_QUERY)==null){
            System.setProperty(Constants.CONFIG_QUERY, "select targetName,targetConfig from cf_Target where domain ='Infra & Platforms'");
        }
      
    }

}
