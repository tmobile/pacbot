package com.tmobile.cso.pacman.datashipper.util;

import java.util.Map;

public class AuthUtil {
    
    public static String  authorise(String authApi,String authToken) throws Exception{
        String response = HttpUtil.post(authApi+"/oauth/token?grant_type=client_credentials","",authToken,"Basic");
        Map<String,Object> authInfo = Util.parseJson(response);
        Object accssToken = authInfo.get("access_token");
        if( accssToken!=null){
            return accssToken.toString();
        }
        return "";
       
    }
   
}
