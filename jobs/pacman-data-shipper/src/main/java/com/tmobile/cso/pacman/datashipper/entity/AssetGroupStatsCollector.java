package com.tmobile.cso.pacman.datashipper.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmobile.cso.pacman.datashipper.es.ESManager;
import com.tmobile.cso.pacman.datashipper.util.AssetGroupUtil;
import com.tmobile.cso.pacman.datashipper.util.AuthManager;
import com.tmobile.cso.pacman.datashipper.util.Constants;
import com.tmobile.cso.pacman.datashipper.util.Util;

/**
 * The Class AssetGroupStatsCollector.
 */
public class AssetGroupStatsCollector implements Constants{

    /** The Constant asstApiUri. */
    private static final String ASSET_API_URL = System.getenv("ASSET_API_URL");

    /** The Constant compApiUri. */
    private static final String COMP_API_URL = System.getenv("CMPL_API_URL");
    
    /** The Constant compApiUri. */
    private static final String VULN_API_URL = System.getenv("VULN_API_URL");

    
    /** The ag stats. */
    private static final String AG_STATS = "assetgroup_stats";

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(AssetGroupStatsCollector.class);
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DOMAIN = "domain";
    
    private static final String CURR_DATE = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());

    private List<Map<String,String>> errorList = new ArrayList<>();
    
    /**
     * Collect asset group stats.
     */
    public List<Map<String, String>> collectAssetGroupStats() {

        
        log.info("Start Collecting asset group stats");
        String token;
        try {
            token = getToken();
        } catch (Exception e1) {
            log.error("collectAssetGroupStats failed as unable to authenticate " , e1);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Exception in collectAssetGroupStats. Authorisation failed");
            errorMap.put(ERROR_TYPE,FATAL);
            errorMap.put(EXCEPTION, e1.getMessage());
            errorList.add(errorMap);
            return errorList;
        }
        Map<String, List<String>> assetGroupMap;
        try {
            assetGroupMap = AssetGroupUtil.fetchAssetGroups(ASSET_API_URL,token);
        } catch (Exception e1) {
            log.error("collectAssetGroupStats failed as unable to fetch asset groups " , e1);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Exception in fetchAssetGroups");
            errorMap.put(ERROR_TYPE, ERROR);
            errorMap.put(EXCEPTION, e1.getMessage());
            errorList.add(errorMap);
            return errorList;
        }

        ESManager.createIndex(AG_STATS, errorList);
        ESManager.createType(AG_STATS, "count_type", errorList);
        ESManager.createType(AG_STATS, "issuecompliance", errorList);
        ESManager.createType(AG_STATS, "compliance", errorList);
        ESManager.createType(AG_STATS, "tagcompliance", errorList);
        ESManager.createType(AG_STATS, "issues", errorList);
        if(VULN_API_URL!=null)
        	ESManager.createType(AG_STATS, "vulncompliance", errorList);


        List<String> assetGroups = new ArrayList<>(assetGroupMap.keySet());
        
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.execute(() -> {
            try {
                uploadAssetGroupCountStats(assetGroups);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupCountStats " , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupCountStats");
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        });

     

        executor.execute(() -> {
            try {
                uploadAssetGroupRuleCompliance(assetGroupMap);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupRuleCompliance " ,e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupRuleCompliance");
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        });
        executor.execute(() -> {
            try {
                uploadAssetGroupCompliance(assetGroupMap);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupCompliance " , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupCompliance");
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        });



 

        executor.execute(() -> {
            try {
                uploadAssetGroupTagCompliance(assetGroups);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupTagCompliance" , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupTagCompliance");
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        });

        executor.execute(() -> {
            try {
                uploadAssetGroupIssues(assetGroupMap);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupIssues" , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupIssues");
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        });
        
        if(VULN_API_URL!=null) {
	        executor.execute(() -> {
	            try {
	                uploadAssetGroupVulnCompliance(assetGroups);
	            } catch (Exception e) {
	                log.error("Exception in uploadAssetGroupVulnCompliance " , e);
	                Map<String,String> errorMap = new HashMap<>();
	                errorMap.put(ERROR, "Exception in uploadAssetGroupVulnCompliance");
	                errorMap.put(ERROR_TYPE, WARN);
	                errorMap.put(EXCEPTION, e.getMessage());
	                synchronized(errorList){
	                    errorList.add(errorMap);
	                }
	            }
	        });
        }
        



        executor.shutdown();
        while (!executor.isTerminated());

        log.info("End Collecting asset group stats");
        return errorList;
    }
    
    private String getToken() throws Exception{
        return AuthManager.getToken();
    }

 

 

    /**
     * Upload asset group tag compliance.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupTagCompliance(List<String> assetGroups) throws Exception {
        log.info("    Start Collecing tag compliance");
        List<Map<String, Object>> docs = new ArrayList<>();
        for (String ag : assetGroups) {
            try {
                Map<String, Object> doc = AssetGroupUtil.fetchTaggingSummary(COMP_API_URL, ag, getToken());
                if (!doc.isEmpty()) {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + CURR_DATE));
                    docs.add(doc);
                }
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupTagCompliance" , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupTagCompliance for Asset Group"+ag);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        }
        ESManager.uploadData(AG_STATS, "tagcompliance", docs, "@id", false);
        log.info("    End Collecing tag compliance");
    }

    /**
     * Upload asset group rule compliance.
     *
     * @param assetGroups
     *            the asset groups
     */
    public  void uploadAssetGroupRuleCompliance(Map<String, List<String>> assetGroups) throws Exception {
        log.info("    Start Collecing Rule  compliance");
        List<Map<String, Object>> docs = new ArrayList<>();
        assetGroups.entrySet().stream().forEach(entry -> {
            String ag = entry.getKey();
            List<String> domains = entry.getValue();
            List<Map<String, Object>> docList = new ArrayList<>();
            try {
                docList = AssetGroupUtil.fetchRuleComplianceInfo(COMP_API_URL, ag, domains,getToken());
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupRuleCompliance" , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupRuleCompliance for Asset Group"+ag);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
            docList.parallelStream().forEach(doc -> {
                doc.put("ag", ag);
                doc.put("date", CURR_DATE);
                doc.put("@id", Util.getUniqueID(ag + doc.get(DOMAIN) + doc.get("ruleId") + CURR_DATE));
            });
            docs.addAll(docList);
        });

        ESManager.uploadData(AG_STATS, "issuecompliance", docs, "@id", false);
        log.info("    End Collecing Rule  compliance");
    }

 

    /**
     * Upload asset group compliance.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupCompliance(Map<String, List<String>> assetGroups) throws Exception {
        log.info("    Start Collecing  compliance");
        List<Map<String, Object>> docs = new ArrayList<>();
        assetGroups.entrySet().stream().forEach(entry -> {
            String ag = entry.getKey();
            List<String> domains = entry.getValue();
            try {
                List<Map<String, Object>> docList = AssetGroupUtil.fetchComplianceInfo(COMP_API_URL, ag, domains,getToken());
                docList.parallelStream().forEach(doc -> {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + doc.get(DOMAIN) + CURR_DATE));
                });
                docs.addAll(docList);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupCompliance " , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupCompliance for Asset Group"+ag);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        });
        ESManager.uploadData(AG_STATS, "compliance", docs, "@id", false);
        log.info("    End Collecing  compliance");
    }

 

    /**
     * Need to collect the asset group stats and upload to ES.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupCountStats(List<String> assetGroups) throws Exception {

        log.info("     Start Collecing  Asset count");
        Map<String, Map<String, Map<String, Object>>> currentInfo = ESManager
                .fetchCurrentCountStatsForAssetGroups(CURR_DATE);
        List<Map<String, Object>> docs = new ArrayList<>();
        for (String ag : assetGroups) {
            try {
                List<Map<String, Object>> typeCounts = AssetGroupUtil.fetchTypeCounts(ASSET_API_URL, ag,getToken());
                Map<String, Map<String, Object>> currInfoMap = currentInfo.get(ag);
                typeCounts.forEach(typeCount -> {
                    String type = typeCount.get("type").toString();
                    long count = Long.valueOf(typeCount.get("count").toString());
                    long min;
                    long max;
                    if (currInfoMap != null) {
                        Map<String, Object> _minMax = currInfoMap.get(type);
                        long _min;
                        long _max;
                        if (_minMax != null) {
                            _min = Long.valueOf(_minMax.get("min").toString());
                            _max = Long.valueOf(_minMax.get("max").toString());
                        } else {
                            _min = count;
                            _max = count;
                        }
                        min = count < _min ? count : _min;
                        max = count > _max ? count : _max;

                    } else {
                        min = count;
                        max = count;
                    }
                    Map<String, Object> doc = new HashMap<>();
                    doc.put("ag", ag);
                    doc.put("type", type);
                    doc.put("min", min);
                    doc.put("max", max);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + type + CURR_DATE));
                    docs.add(doc);
                });
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupCountStats" , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupCountStats for Asset Group"+ag);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        }
        ESManager.uploadData(AG_STATS, "count_type", docs, "@id", false);

        log.info("    End Collecing  Asset count");
    }

    /**
     * Upload asset group issues.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupIssues(Map<String, List<String>> assetGroups) throws Exception {
        log.info("    Start Collecing  issues");
        List<Map<String, Object>> docs = new ArrayList<>();
        assetGroups.entrySet().stream().forEach(entry -> {
            String ag = entry.getKey();
            List<String> domains = entry.getValue();
            try {
                List<Map<String, Object>> docList = AssetGroupUtil.fetchIssuesInfo(COMP_API_URL, ag, domains,getToken());
                docList.parallelStream().forEach(doc -> {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + doc.get(DOMAIN) + CURR_DATE));
                });
                docs.addAll(docList);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupIssues" , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupIssues for Asset Group"+ag);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        });
        ESManager.uploadData(AG_STATS, "issues", docs, "@id", false);
        log.info("    End Collecing  issues");
    }
    
    /**
     * Upload asset group vuln compliance.
     *
     * @param assetGroups            the asset groups
     * @throws Exception the exception
     */
    public void uploadAssetGroupVulnCompliance(List<String> assetGroups) throws Exception {
        
        
        log.info("    Start Collecing vuln compliance");
        List<Map<String, Object>> docs = new ArrayList<>();
        for (String ag : assetGroups) {
            try {
                Map<String, Object> doc = AssetGroupUtil.fetchVulnSummary(VULN_API_URL, ag, getToken());
                if (!doc.isEmpty()) {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + CURR_DATE));
                    docs.add(doc);
                }
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupVulnCompliance" , e);
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put(ERROR, "Exception in uploadAssetGroupVulnCompliance for Asset Group"+ag);
                errorMap.put(ERROR_TYPE, WARN);
                errorMap.put(EXCEPTION, e.getMessage());
                synchronized(errorList){
                    errorList.add(errorMap);
                }
            }
        }
        ESManager.uploadData(AG_STATS, "vulncompliance", docs, "@id", false);
        log.info("    End Collecing vuln compliance");
    }
    
  
}
