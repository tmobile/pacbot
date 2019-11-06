package com.tmobile.cso.pacman.datashipper.error;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tmobile.cso.pacman.datashipper.es.ESManager;

public class AwsErrorManager extends ErrorManager {
	
	protected AwsErrorManager() {
	
	}
	/**
	 * Handle error.
	 *
	 * @param dataSource the data source
	 * @param index the index
	 * @param type the type
	 * @param loaddate the loaddate
	 * @param errorList the error list
	 * @param checkLatest the check latest
	 * @return 
	 */
	public Map<String, Long> handleError(String index, String type, String loaddate,List<Map<String,String>> errorList,boolean checkLatest) {
		Map<String,List<Map<String,String>>> errorInfo = getErrorInfo(errorList);
		String parentType = index.replace(dataSource+"_", "");
		Map<String,Long> errorUpdateInfo = new HashMap<>();
		if(errorInfo.containsKey(parentType) || errorInfo.containsKey("all")) {
			List<Map<String,String>> errorByType = errorInfo.get(parentType);
			if(errorByType==null){
				errorByType = errorInfo.get("all");
			}
			errorByType.forEach(errorData ->  {
					String accountId = errorData.get("accountid");
					String region = errorData.get("region");
					long updateCount = ESManager.updateLoadDate(index, type, accountId, region, loaddate,checkLatest);
		    		errorUpdateInfo.put(accountId+":"+region, updateCount);
				}
	    	);
	     }
		return errorUpdateInfo;
	}

}
