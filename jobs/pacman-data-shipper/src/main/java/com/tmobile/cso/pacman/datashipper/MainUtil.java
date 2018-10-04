package com.tmobile.cso.pacman.datashipper;

import java.util.Map;

import com.tmobile.cso.pacman.datashipper.util.Constants;
import com.tmobile.cso.pacman.datashipper.util.Util;


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
    public static void setup(Map<String, String> params) {

        String redshfitInfo = Util.base64Decode(params.get(Constants.REDSHFIT_PARAM));
        String[] redshiftUserPwd = redshfitInfo.split(":");
        System.setProperty(Constants.REDSHIFT_USER, redshiftUserPwd[0]);
        System.setProperty(Constants.REDSHIFT_PWD, redshiftUserPwd[1]);

        String rdsInfo = Util.base64Decode(params.get(Constants.RDS_PARAM));
        String[] rdsUserPws = rdsInfo.split(":");
        System.setProperty(Constants.RDS_USER, rdsUserPws[0]);
        System.setProperty(Constants.RDS_PWD, rdsUserPws[1]);
       
        System.setProperty(Constants.API_AUTH_INFO, params.get(Constants.API_AUTH_INFO));

        if(params.get(Constants.TARGET_TYPE_INFO)!=null)
            System.setProperty(Constants.TARGET_TYPE_INFO, params.get(Constants.TARGET_TYPE_INFO));
        
        if(params.get(Constants.CONFIG_QUERY)==null){
            System.setProperty(Constants.CONFIG_QUERY, "select targetName,targetConfig from cf_Target where domain ='Infra & Platforms'");
        }else{
            System.setProperty(Constants.CONFIG_QUERY,params.get(Constants.CONFIG_QUERY));
        }
    }

}
