package com.tmobile.cso.pacman.qualys.jobs;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.CollectionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tmobile.cso.pacman.qualys.dto.Vuln;
import com.tmobile.cso.pacman.qualys.util.ElasticSearchManager;


/**
 * The Class Util.
 */
public class Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
    private static final String SOURCE = "_source";
    private static final String SCROLL_URI = "/_search/scroll?scroll=2m&scroll_id=";
    private static final String LAST_VULN_SCAN = "lastVulnScan";
    
    /**
     * Process and transform.
     *
     * @param hostAssets the host assets
     * @param vulnInfoMap the vuln info map
     * @param discoveryDate the discovery date
     */
    public static void processAndTransform(Map<String, Map<String, Object>> hostAssets,
            Map<String, Map<String, String>> vulnInfoMap, String discoveryDate) {
        if (null != hostAssets && !hostAssets.isEmpty()) {
            hostAssets.entrySet().forEach(entry -> {
                Map<String, Object> assetMap = entry.getValue();
                appendVulnInfo(assetMap, vulnInfoMap);
                flattenAgentVersionInfo(assetMap);
                assetMap.put("_docid", entry.getKey());
                assetMap.put("discoverydate", discoveryDate);
                assetMap.put("latest", true);
            });
        }
    }

    /**
     * Append vuln info.
     *
     * @param assetMap the asset map
     * @param vulnInfoMap the vuln info map
     */
    @SuppressWarnings("unchecked")
    public static void appendVulnInfo(Map<String, Object> assetMap, Map<String, Map<String, String>> vulnInfoMap) {
        Map<String, Map<String, Object>> vulnMap = (Map<String, Map<String, Object>>) assetMap.get("vuln");
        if (vulnMap != null) {
            List<Map<String, Object>> vulnList = (List<Map<String, Object>>) vulnMap.get("list");
            if (vulnList != null) {
                Iterator<Map<String, Object>> it = vulnList.iterator();
                while (it.hasNext()) {
                    Map<String, Object> vuln = (Map<String, Object>) it.next().get("HostAssetVuln");
                    if (vuln != null) {
                        String qid = String.valueOf(Double.valueOf(vuln.get("qid").toString()).longValue());
                        Map<String, ?> vulninfo = vulnInfoMap.get(qid);
                        if (vulninfo != null) {
                            vuln.putAll(vulninfo);
                            vuln.put("qid", qid);
                            vuln.put("_status", "Open");
                        }
                    }
                }
            }
        }

    }

    /**
     * Calculte age in days.
     *
     * @param firstFound the first found
     * @param lastFound the last found
     * @return the long
     */
    public static long calculteAgeInDays(Object firstFound, Object lastFound) {
        if (firstFound != null) {
            if (lastFound != null) {
                return Duration.between(LocalDateTime.parse(firstFound.toString(), DateTimeFormatter.ISO_DATE_TIME),
                        LocalDateTime.parse(lastFound.toString(), DateTimeFormatter.ISO_DATE_TIME)).toDays();
            }
            return Duration.between(LocalDateTime.parse(firstFound.toString(), DateTimeFormatter.ISO_DATE_TIME),
                    LocalDateTime.now()).toDays();
        }
        return 0;
    }

    /**
     * Classify vuln.
     *
     * @param vuln the vuln
     * @return the string
     */
    public static String classifyVuln(Vuln vuln) {
        String classification = "OS";
        String title = vuln.getTitle().toLowerCase();
        String cateogry = vuln.getCategory().toLowerCase();

        List<String> titlteMatchStrings = Arrays.asList("adobe", "apache", "apple", "cisco", "elasticsearch",
                "git server", "google", "openssl", "ibm business process manager", "jetbrains", "mozilla", "notepad",
                "opera ", "mysql", "oracle java", "oracle jrockit", "python", "putty", "virtualbox", "rubygems",
                "crystal reports", "netweaver", "firefox", "postgresql", "pidgin", "solarwinds", "sourcetree",
                "sun java", "sun jdk", "tableau", "tibco", "netbackup", "videolan", "vmware", "winrar", "winscp",
                "wireshark", ".net", "sql server", "eol/obsolete", "java");
        List<String> catetoryMatchStrings = Arrays.asList("web server", "database", "web application", "e-commerce",
                "cgi", "proxy");

        if (titlteMatchStrings.parallelStream().anyMatch(title::contains)
                || catetoryMatchStrings.parallelStream().anyMatch(cateogry::contains))
            classification = "Application";

        return classification;
    }

    /**
     * Find qid status.
     *
     * @param qid the qid
     * @param currentQids the current qids
     * @return the string
     */
    public static String findQidStatus(String qid, List<String> currentQids) {
        return currentQids == null || !currentQids.contains(qid) ? "New" : "Open";
    }

    /**
     * Fetch current qid info.
     *
     * @param type the type
     * @return the map
     */
    public static Map<String, List<String>> fetchCurrentQidInfo(String type) {

        String endPoint = "/aws_" + type + "/vulninfo/_search?scroll=2m&size=10000";
        String payLoad = "{\"_source\":[\"_resourceid\",\"qid\"],\"query\":{\"bool\":{\"must\":[{\"terms\":{\"severitylevel\":[3,4,5]}}]}}}";

        List<Map<String, String>> data = new ArrayList<>();
        String scrollId = fetchDataAndScrollId(endPoint, data, payLoad);
        do {
            endPoint = SCROLL_URI + scrollId;
            scrollId = fetchDataAndScrollId(endPoint, data, null);
        } while (scrollId != null);

        Map<String, List<String>> hostQidMapping = new HashMap<>();
        data.stream().collect(Collectors.groupingBy(obj -> obj.get("_resourceid"))).entrySet().stream()
                .forEach(entry -> {
                    String key = entry.getKey();
                    List<String> value = entry.getValue().stream().map(obj -> obj.get("qid"))
                            .collect(Collectors.toList());
                    hostQidMapping.put(key, value);
                });
        return hostQidMapping;
    }

    /**
     * Fetch VP cto nat IP info.
     *
     * @return the map
     */
    public static Map<String, List<String>> fetchVPCtoNatIPInfo() {
        String endPoint = "/aws_nat/nat/_search?filter_path=hits.hits._source.vpcid,hits.hits.inner_hits.nat_addresses.hits.hits._source.publicip";
        String payLoad = "{\"size\":10000,\"_source\":\"vpcid\",\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"has_child\":{\"type\":\"nat_addresses\",\"query\":{\"match_all\":{}},\"inner_hits\":{\"size\":100,\"_source\":\"publicip\"}}}]}}}{\"size\":10000,\"_source\":\"vpcid\",\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"has_child\":{\"type\":\"nat_addresses\",\"query\":{\"match_all\":{}},\"inner_hits\":{\"size\":100,\"_source\":\"publicip\"}}}]}}}";
        Map<String, List<String>> VpcPublicIpInfo = new HashMap<>();
        try {
            Response response = ElasticSearchManager.invokeAPI("GET", endPoint, payLoad);
            String responseJson = EntityUtils.toString(response.getEntity());
            JsonParser jsonParser = new JsonParser();
            JsonObject resultJson = (JsonObject) jsonParser.parse(responseJson);
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get("hits").toString());
            JsonArray jsonArray = hitsJson.getAsJsonObject().get("hits").getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = (JsonObject) jsonArray.get(i);
                String vpcId = obj.get(SOURCE).getAsJsonObject().get("vpcid").getAsString();
                JsonArray innerHits = obj.getAsJsonObject("inner_hits").getAsJsonObject("nat_addresses")
                        .getAsJsonObject("hits").getAsJsonArray("hits");
                for (int j = 0; j < innerHits.size(); j++) {
                    String ip = innerHits.get(j).getAsJsonObject().getAsJsonObject(SOURCE).get("publicip")
                            .getAsString();
                    List<String> ipList = VpcPublicIpInfo.get(vpcId);
                    if (ipList == null) {
                        ipList = new ArrayList<>();
                        VpcPublicIpInfo.put(vpcId, ipList);
                    }
                    ipList.add(ip);
                }
            }

        } catch (Exception e) {
           LOGGER.error("Error in fetchVPCtoNatIPInfo",e);
        }
        return VpcPublicIpInfo;
    }

    /**
     * Fetch data and scroll id.
     *
     * @param endPoint the end point
     * @param _data the data
     * @param payLoad the pay load
     * @return the string
     */
    private static String fetchDataAndScrollId(String endPoint, List<Map<String, String>> _data, String payLoad) {
        try {
            Response response = ElasticSearchManager.invokeAPI("GET", endPoint, payLoad);
            String responseJson;

            responseJson = EntityUtils.toString(response.getEntity());
            JsonParser jsonParser = new JsonParser();
            JsonObject resultJson = (JsonObject) jsonParser.parse(responseJson);
            String scrollId = resultJson.get("_scroll_id").getAsString();
            JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get("hits").toString());
            JsonArray jsonArray = hitsJson.getAsJsonObject().get("hits").getAsJsonArray();
            if (jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject obj = (JsonObject) jsonArray.get(i);
                    JsonObject sourceJson = (JsonObject) obj.get(SOURCE);
                    if (sourceJson != null) {
                        Map<String, String> doc = new Gson().fromJson(sourceJson, new TypeToken<Map<String, String>>() {
                        }.getType());
                        _data.add(doc);
                    }
                }
                return scrollId;
            } else {
                return null;
            }

        } catch (ParseException | IOException e) {
           LOGGER.error("Error in fetchDataAndScrollId",e);
        }
        return null;
    }

    /**
     * Find latest profile.
     *
     * @param resp the resp
     * @return the map
     */
   /* public static Map<String, Object> findLatestProfile(List<Map<String, Object>> resp) {

        List<Map<String, Object>> respData = resp.stream().filter(host -> host.get("vuln") != null)
                .filter(Util::isScanInfoAvailable).collect(Collectors.toList());
        if (!CollectionUtils.isNullOrEmpty(respData)) {
            respData.sort((obj1, obj2) -> 
                 LocalDateTime.parse(obj2.get(LAST_VULN_SCAN).toString(), DateTimeFormatter.ISO_DATE_TIME)
                        .compareTo(LocalDateTime.parse(obj1.get(LAST_VULN_SCAN).toString(),
                                DateTimeFormatter.ISO_DATE_TIME))
            );

            return respData.get(0);
        }
        return null;

    }*/

    /**
     * Sort on last vuln scan.
     *
     * @param resp the resp
     * @return the list
     */
    public static List<Map<String, Object>> sortOnLastVulnScan(List<Map<String, Object>> resp) {

        List<Map<String, Object>> respData = resp.stream().filter(host -> host.get(LAST_VULN_SCAN) != null)
                .collect(Collectors.toList());
        if (!CollectionUtils.isNullOrEmpty(respData)) {
            respData.sort((obj1, obj2) -> 
                 LocalDateTime.parse(obj2.get(LAST_VULN_SCAN).toString(), DateTimeFormatter.ISO_DATE_TIME)
                        .compareTo(LocalDateTime.parse(obj1.get(LAST_VULN_SCAN).toString(),
                                DateTimeFormatter.ISO_DATE_TIME))
            );

        }
        return respData;
    }

    /**
     * Fetch ec 2 eni info.
     *
     * @return the map
     */
    public static Map<String, List<String>> fetchEc2EniInfo() {
        String endPoint = "/aws_ec2/ec2_nwinterfaces/_search?scroll=2m&size=10000";
        String payLoad = "{\"_source\":[\"instanceid\",\"networkinterfaceid\"],\"query\":{\"has_parent\":{\"parent_type\":\"ec2\",\"query\":{\"match\":{\"latest\":\"true\"}}}}}";
        List<Map<String, String>> data = new ArrayList<>();
        String scrollId = fetchDataAndScrollId(endPoint, data, payLoad);
        do {
            endPoint = SCROLL_URI + scrollId;
            scrollId = fetchDataAndScrollId(endPoint, data, null);
        } while (scrollId != null);

        Map<String, List<String>> ec2EniMap = new HashMap<>();
        data.stream().collect(Collectors.groupingBy(obj -> obj.get("instanceid"))).entrySet().stream()
                .forEach(entry -> {
                    String key = entry.getKey();
                    List<String> value = entry.getValue().stream()
                            .map(obj -> obj.get("networkinterfaceid").toLowerCase()).collect(Collectors.toList());
                    ec2EniMap.put(key, value);
                });
        return ec2EniMap;
    }

    /**
     * Fetch eni mac info.
     *
     * @return the map
     */
    public static Map<String, String> fetchEniMacInfo() {
        String endPoint = "/aws_eni/eni/_search?scroll=2m&size=10000";
        String payLoad = "{\"_source\":[\"_resourceid\",\"macaddress\"],\"query\":{\"match\":{\"latest\":\"true\"}}}";
        List<Map<String, String>> data = new ArrayList<>();
        String scrollId = fetchDataAndScrollId(endPoint, data, payLoad);
        do {
            endPoint = SCROLL_URI + scrollId;
            scrollId = fetchDataAndScrollId(endPoint, data, null);
        } while (scrollId != null);

        return data.stream().collect(Collectors.toMap(info -> info.get("_resourceid").toLowerCase(),
                info -> info.get("macaddress").toLowerCase()));

    }

    /**
     * Checks if is scan info available.
     *
     * @param host the host
     * @return true, if is scan info available
     */
    public static boolean isScanInfoAvailable(Map<String, Object> host,int scanThreshold) {
        if (host != null) {
            return (host.get(LAST_VULN_SCAN) != null
                    && LocalDateTime.parse(host.get(LAST_VULN_SCAN).toString(), DateTimeFormatter.ISO_DATE_TIME)
                            .isAfter(LocalDateTime.now().minusDays(scanThreshold)));
        }
        return false;
    }

    /**
     * Fetch trackin method and qids.
     *
     * @param respData the resp data
     * @return the map
     */
    public static Map<String, List<String>> fetchTrackinMethodAndQids(List<Map<String, Object>> respData) {

        if (respData != null && !respData.isEmpty()) {
            return respData.stream()
                    .collect(Collectors.groupingBy(host -> host.get("trackingMethod").toString(),
                            Collectors.mapping(
                                    host -> Long.toString(Double.valueOf(host.get("id").toString()).longValue()),
                                    Collectors.toList())));
        }
        return new HashMap<>();

    }

    /**
     * Fetch curret qualys info.
     *
     * @param type the type
     * @param resourceId the resource id
     * @return the map
     * @throws ParseException the parse exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Map<String, Object> fetchCurretQualysInfo(String type, String resourceId)
            throws IOException {
        String endPoint = "/aws_" + type + "/qualysinfo/_search";
        String payLoad = "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}},{\"match\":{\"_resourceid.keyword\":\""
                + resourceId + "\"}},{\"exists\":{\"field\":\"vuln\"}}]}}}";
        Response response = ElasticSearchManager.invokeAPI("GET", endPoint, payLoad);
        String responseJson = EntityUtils.toString(response.getEntity());
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = (JsonObject) jsonParser.parse(responseJson);
        JsonObject hitsJson = (JsonObject) jsonParser.parse(resultJson.get("hits").toString());
        JsonArray jsonArray = hitsJson.getAsJsonObject().get("hits").getAsJsonArray();
        if (jsonArray.size() > 0) {
            JsonObject obj = (JsonObject) jsonArray.get(0);
            JsonObject sourceJson = (JsonObject) obj.get(SOURCE);
            return new Gson().fromJson(sourceJson, new TypeToken<Map<String, Object>>() {
            }.getType());

        }
        return null;

    }

    /**
     * 
     * 
     * @param assetMap
     * 
     * Convert the manifestVersion in agentInfo to a String to make the qualysinfo ingest working.
     * manifestVersion is changed from string to object and es type is created as string
     */
    @SuppressWarnings("unchecked")
    private static void flattenAgentVersionInfo(Map<String, Object> assetMap) {
        Map<String,Object> agentInfo = (Map<String, Object>) assetMap.get("agentInfo");
        if(agentInfo!=null && agentInfo.get("manifestVersion")!=null){
            agentInfo.put("manifestVersion", agentInfo.get("manifestVersion").toString());
        }
    }
}
