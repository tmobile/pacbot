package com.tmobile.cso.pacman.qualys.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tmobile.cso.pacman.qualys.Constants;



public class ErrorManageUtil implements Constants{
    
    private ErrorManageUtil() {
        
    }

    public static Map<String,Object> formErrorCode(List<Map<String,String>> errorList) {
        Map<String,Object> errorCode = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        errorCode.put("endTime", sdf.format(new Date()));
        
        String status = "";
        
        List<Map<String,Object>> errors = new ArrayList<>();
        if(!errorList.isEmpty()) {
            for(Map<String, String> errorDetail :errorList) {
                Map<String,Object> error = new HashMap<>();
                error.put(ERROR, errorDetail.get(ERROR));
                
                List<Map<String,String>> details = new ArrayList<>();
                Map<String,String> detail = new HashMap<>();
                detail.put(EXCEPTION,errorDetail.get(EXCEPTION));
                details.add(detail);
                error.put("details",details);
                errors.add(error);
                
                if(!FAILED.equalsIgnoreCase(status)) {
                    status = (FATAL.equalsIgnoreCase(errorDetail.get(ERROR_TYPE))) ? FAILED:"Partial Success";
                }
            }
        }
        else {
            status = "success";
        }
        
        errorCode.put("errors", errors);
        errorCode.put("status", status);
        return errorCode;
    }
}
