package com.tmobile.pacman.commons.singleton;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.ESFailedException;
import com.tmobile.pacman.commons.utils.CommonUtils;
import com.tmobile.pacman.commons.utils.ESUtils;

public class NatIPProvider {

	private JsonArray data;
    private static NatIPProvider dataProvider;
    
    /**
     * @throws ESFailedException 
     * @throws Exception 
	 * 
	 */
    private NatIPProvider(String index, String subType,
			Map<String, String> filter, List<String> fields) throws ESFailedException {
    	String esHost = CommonUtils.getEnvVariableValue(PacmanSdkConstants.ES_URI);
		JsonObject resultJson = ESUtils.getQueryDetailsFromES(esHost, index, subType, filter, fields);
		data = ESUtils.getHitsFromESResponse(resultJson);
    }

    /**
     * Singleton thread safe method for caching the ES Query result
     * 
     * @return
     * @throws ESFailedException 
     * @throws Exception 
     */
	public static synchronized NatIPProvider getInstance(String index, String subType,
			Map<String, String> filter, List<String> fields) throws ESFailedException {
		if (dataProvider == null) {
			synchronized (NatIPProvider.class) {
				dataProvider = new NatIPProvider(index, subType, filter, fields);
			}
		}
		return dataProvider;
	}
    
    
    /**
     * 
     * @return
     */
    public JsonArray getData() {
        return data;
    }

}
