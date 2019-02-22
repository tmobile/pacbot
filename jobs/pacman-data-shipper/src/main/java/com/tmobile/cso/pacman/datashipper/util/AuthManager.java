package com.tmobile.cso.pacman.datashipper.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthManager.class);
    private static final String AUTH_API_URL = System.getenv("AUTH_API_URL");
   
    private static AccessToken accessToken ;
    
    private AuthManager(){
        
    }
    private static void  authorise() throws Exception{
        LOGGER.info("Called Authorise");
        String credentials = System.getProperty(Constants.API_AUTH_INFO);
        String response = HttpUtil.post(AUTH_API_URL+"/oauth/token?grant_type=client_credentials","",credentials,"Basic");
        Map<String,Object> authInfo = Util.parseJson(response);
        Object token = authInfo.get("access_token");
        Object expiresIn = authInfo.get("expires_in"); // In seconds
        if( token!=null){
            long tokenExpiresAt = System.currentTimeMillis() + Long.valueOf(expiresIn.toString())*1000 - (20*1000) ; // 20 second buffer
            accessToken = new AccessToken(token.toString(), tokenExpiresAt);
        }
    }
    
    public static String getToken(){
        if(!isTokenValid()){
            try {
                authorise();
            } catch (Exception e) {
                LOGGER.error("Authorisation Failed",e);
            }
        }
        if(accessToken!=null)
            return accessToken.getToken();
        else
            return "";
    }
    
    private static boolean isTokenValid(){
        return accessToken !=null && accessToken.getExpiresAt() > System.currentTimeMillis();
    }
   
}

class AccessToken {
    private String token;
    private long expiresAt;
    
    AccessToken(String token, long expiresAt){
        this.token = token;
        this.expiresAt = expiresAt;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public long getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
    public String toString(){
        return "Token:"+token+" ,ExpiresIn (sec)"+ (expiresAt- System.currentTimeMillis())/1000;
    }
    
}
