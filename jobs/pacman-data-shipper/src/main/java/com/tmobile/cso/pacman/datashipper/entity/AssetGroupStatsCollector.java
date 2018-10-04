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
import com.tmobile.cso.pacman.datashipper.util.AuthUtil;
import com.tmobile.cso.pacman.datashipper.util.Constants;
import com.tmobile.cso.pacman.datashipper.util.Util;

/**
 * The Class AssetGroupStatsCollector.
 */
public class AssetGroupStatsCollector {

    /** The Constant asstApiUri. */
    private static final String ASSET_API_URL = System.getenv("ASSET_API_URL");

    /** The Constant compApiUri. */
    private static final String COMP_API_URL = System.getenv("CMPL_API_URL");

    /** The Constant compApiUri. */
    private static final String AUTH_API_URL = System.getenv("AUTH_API_URL");
    
    
    /** The ag stats. */
    private static final String AG_STATS = "assetgroup_stats";

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(AssetGroupStatsCollector.class);
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DOMAIN = "domain";
    
    private static final String CURR_DATE = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());

    
    /**
     * Collect asset group stats.
     */
    public void collectAssetGroupStats() {

        
        log.info("Start Collecting asset group stats");
        String token;
        try {
            token = authorise();
        } catch (Exception e1) {
            log.error("collectAssetGroupStats failed as unable to authenticate " , e1);
            return;
        }
        Map<String, List<String>> assetGroupMap;
        try {
            assetGroupMap = AssetGroupUtil.fetchAssetGroups(ASSET_API_URL,token);
        } catch (Exception e1) {
            log.error("collectAssetGroupStats failed as unable to fetch asset groups " , e1);
            return;
        }

        ESManager.createIndex(AG_STATS);
        ESManager.createType(AG_STATS, "count_type");
        ESManager.createType(AG_STATS, "count_vuln");
        ESManager.createType(AG_STATS, "patching");
        ESManager.createType(AG_STATS, "issuecompliance");
        ESManager.createType(AG_STATS, "compliance");
        ESManager.createType(AG_STATS, "vulncompliance");
        ESManager.createType(AG_STATS, "certcompliance");
        ESManager.createType(AG_STATS, "tagcompliance");
        ESManager.createType(AG_STATS, "issues");

        List<String> assetGroups = new ArrayList<>(assetGroupMap.keySet());
        
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.execute(() -> {
            try {
                uploadAssetGroupCountStats(assetGroups);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupCountStats " , e);
            }
        });

        executor.execute(() -> {
            try {
                uploadAssetGroupVulnStats(assetGroups);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupVulnStats " , e);
            }
        });

        executor.execute(() -> {
            try {
                uploadAssetGroupPatchingCompliance(assetGroups);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupPatchingCompliance " , e);
            }
        });

        executor.execute(() -> {
            try {
                uploadAssetGroupRuleCompliance(assetGroupMap);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupRuleCompliance " ,e);
            }
        });
        executor.execute(() -> {
            try {
                uploadAssetGroupCompliance(assetGroupMap);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupCompliance " , e);
                log.error(Util.getStackTrace(e));
            }
        });

        executor.execute(() -> {
            try {
                uploadAssetGroupVulnCompliance(assetGroups);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupVulnCompliance " , e);
            }
        });

        executor.execute(() -> {
            try {
                uploadAssetGroupCertCompliance(assetGroups);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupCertCompliance " , e);
            }
        });

        executor.execute(() -> {
            try {
                uploadAssetGroupTagCompliance(assetGroups);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupTagCompliance" , e);
            }
        });

        executor.execute(() -> {
            try {
                uploadAssetGroupIssues(assetGroupMap);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupIssues" , e);
            }
        });

        executor.shutdown();
        while (!executor.isTerminated());

        log.info("End Collecting asset group stats");
    }
    
    public String authorise() throws Exception{
        String credentials = System.getProperty(Constants.API_AUTH_INFO);
        return AuthUtil.authorise(AUTH_API_URL,credentials);
    }

    /**
     * Upload asset group vuln compliance.
     *
     * @param assetGroups
     *            the asset groups
     * @throws Exception 
     */
    public void uploadAssetGroupVulnCompliance(List<String> assetGroups) throws Exception {
        
        
        log.info("    Start Collecing vuln compliance");
        String token = authorise();
        List<Map<String, Object>> docs = new ArrayList<>();
        for (String ag : assetGroups) {
            try {
                Map<String, Object> doc = AssetGroupUtil.fetchVulnSummary(COMP_API_URL, ag,token);
                if (!doc.isEmpty()) {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + CURR_DATE));
                    docs.add(doc);
                }
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupVulnCompliance" , e);
            }
        }
        ESManager.uploadData(AG_STATS, "vulncompliance", docs, "@id", false);
        log.info("    End Collecing vuln compliance");
    }

    /**
     * Upload asset group cert compliance.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupCertCompliance(List<String> assetGroups) throws Exception {
        log.info("    Start Collecing cert compliance");
        String token = authorise();
        List<Map<String, Object>> docs = new ArrayList<>();
        for (String ag : assetGroups) {
            try {
                Map<String, Object> doc = AssetGroupUtil.fetchCertSummary(COMP_API_URL, ag,token);
                if (!doc.isEmpty()) {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + CURR_DATE));
                    docs.add(doc);
                }
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupVulnCompliance " ,e);
            }
        }
        ESManager.uploadData(AG_STATS, "certcompliance", docs, "@id", false);
        log.info("    End Collecing cert compliance");
    }

    /**
     * Upload asset group tag compliance.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupTagCompliance(List<String> assetGroups) throws Exception {
        log.info("    Start Collecing tag compliance");
        String token = authorise();
        List<Map<String, Object>> docs = new ArrayList<>();
        for (String ag : assetGroups) {
            try {
                Map<String, Object> doc = AssetGroupUtil.fetchTaggingSummary(COMP_API_URL, ag,token);
                if (!doc.isEmpty()) {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + CURR_DATE));
                    docs.add(doc);
                }
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupTagCompliance" , e);
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
        String token = authorise();
        assetGroups.entrySet().stream().forEach(entry -> {
            String ag = entry.getKey();
            List<String> domains = entry.getValue();
            List<Map<String, Object>> docList = new ArrayList<>();
            try {
                docList = AssetGroupUtil.fetchRuleComplianceInfo(COMP_API_URL, ag, domains,token);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupRuleCompliance" , e);
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
     * Upload asset group patching compliance.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupPatchingCompliance(List<String> assetGroups) throws Exception {
        log.info("    Start Collecing patching compliance");
        List<Map<String, Object>> docs = new ArrayList<>();
        String token = authorise();
        for (String ag : assetGroups) {
            try {
                Map<String, Object> doc = AssetGroupUtil.fetchPatchingCompliance(COMP_API_URL, ag,token);
                if (!doc.isEmpty()) {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + CURR_DATE));
                    docs.add(doc);
                }
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupPatchingCompliance" , e);
            }
        }
        ESManager.uploadData(AG_STATS, "patching", docs, "@id", false);
        log.info("    End Collecing patching compliance");
    }

    /**
     * Upload asset group compliance.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupCompliance(Map<String, List<String>> assetGroups) throws Exception {
        log.info("    Start Collecing  compliance");
        String token = authorise();
        List<Map<String, Object>> docs = new ArrayList<>();
        assetGroups.entrySet().stream().forEach(entry -> {
            String ag = entry.getKey();
            List<String> domains = entry.getValue();
            try {
                List<Map<String, Object>> docList = AssetGroupUtil.fetchComplianceInfo(COMP_API_URL, ag, domains,token);
                docList.parallelStream().forEach(doc -> {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + doc.get(DOMAIN) + CURR_DATE));
                });
                docs.addAll(docList);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupCompliance " , e);
            }
        });
        ESManager.uploadData(AG_STATS, "compliance", docs, "@id", false);
        log.info("    End Collecing  compliance");
    }

    /**
     * Upload asset group vuln stats.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupVulnStats(List<String> assetGroups) throws Exception {
        log.info("    Start Collecting vuln info");
        String token = authorise();
        List<Map<String, Object>> docs = new ArrayList<>();
        for (String ag : assetGroups) {
            try {
                List<Map<String, Object>> docList = AssetGroupUtil.fetchVulnDistribution(COMP_API_URL, ag,token);
                docList.parallelStream().forEach(doc -> {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + doc.get("tags.Application") + doc.get("tags.Environment")
                            + doc.get("severitylevel") + CURR_DATE));
                });
                docs.addAll(docList);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupVulnStats" , e);
            }
        }
        ESManager.uploadData(AG_STATS, "count_vuln", docs, "@id", false);
        log.info("    End Collecting vuln info");
    }

    /**
     * Need to collect the asset group stats and upload to ES.
     *
     * @param assetGroups
     *            the asset groups
     */
    public void uploadAssetGroupCountStats(List<String> assetGroups) throws Exception {

        log.info("     Start Collecing  Asset count");
        String token = authorise();
        Map<String, Map<String, Map<String, Object>>> currentInfo = ESManager
                .fetchCurrentCountStatsForAssetGroups(CURR_DATE);
        List<Map<String, Object>> docs = new ArrayList<>();
        for (String ag : assetGroups) {
            try {
                List<Map<String, Object>> typeCounts = AssetGroupUtil.fetchTypeCounts(ASSET_API_URL, ag,token);
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
        String token = authorise();
        List<Map<String, Object>> docs = new ArrayList<>();
        assetGroups.entrySet().stream().forEach(entry -> {
            String ag = entry.getKey();
            List<String> domains = entry.getValue();
            try {
                List<Map<String, Object>> docList = AssetGroupUtil.fetchIssuesInfo(COMP_API_URL, ag, domains,token);
                docList.parallelStream().forEach(doc -> {
                    doc.put("ag", ag);
                    doc.put("date", CURR_DATE);
                    doc.put("@id", Util.getUniqueID(ag + doc.get(DOMAIN) + CURR_DATE));
                });
                docs.addAll(docList);
            } catch (Exception e) {
                log.error("Exception in uploadAssetGroupIssues" , e);
            }
        });
        ESManager.uploadData(AG_STATS, "issues", docs, "@id", false);
        log.info("    End Collecing  issues");
    }
}
