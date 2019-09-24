package com.tmobile.cso.pacman.qualys.jobs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.ParseException;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.tmobile.cso.pacman.qualys.Constants;
import com.tmobile.cso.pacman.qualys.util.ElasticSearchManager;
import com.tmobile.cso.pacman.qualys.util.ErrorManageUtil;


/**
 * The Class HostAssetDataImporter.
 */
public class HostAssetDataImporter extends QualysDataImporter implements Constants {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HostAssetDataImporter.class);
    
    /** The Constant TIME_FORMAT. */
    private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    
    /** The Constant MODIFIED. */
    private static final String MODIFIED = "modified";
    
    /** The Constant NW_INTERFACE. */
    private static final String NW_INTERFACE = "networkInterface";
    
    /** The Constant HOST_ASSET_INTERFACE. */
    private static final String HOST_ASSET_INTERFACE = "HostAssetInterface";
    
    /** The Constant ADDRESS. */
    private static final String ADDRESS = "address";
    
    /** The Constant MATCH_FOUND_BY. */
    private static final String MATCH_FOUND_BY = "matchFoundBy";
    
    /** The Constant TRACKING_METHOD. */
    private static final String TRACKING_METHOD = "trackingMethod";
    
    private static final String LAST_VULN_SCAN = "lastVulnScan";
    
    /** The Constant VPC_ID. */
    private static final String VPC_ID = "vpcid";

    /** The vuln info map. */
    private Map<String, Map<String, String>> vulnInfoMap;
    
    /** The current info. */
    private Map<String, Map<String, String>> currentInfo;
    
    /** The current qualys info. */
    private Map<String, Map<String, String>> currentQualysInfo;
    
    /** The vpc nat ip assets. */
    private Map<String, List<Map<String, Map<String, Object>>>> vpcNatIpAssets = new HashMap<>();// vpciid<natip<ip<qid>>>
    
    /** The ec 2 eni map. */
    private Map<String, List<String>> ec2EniMap;
    
    /** The eni mac map. */
    private Map<String, String> eniMacMap;

    /** The curr date. */
    private String CURR_DATE = new SimpleDateFormat(TIME_FORMAT).format(new java.util.Date());
    
    /** The type. */
    private String type = System.getProperty("server_type");
    
    /** The type. */
    private String ds = System.getProperty("datasource");
    
    /** The uri post. */
    private String uriPost = BASE_API_URL + apiMap.get("hostAssetSearch");
    
    private int scanThreshold = 30;
    
    /** The last vuln date. */
    private String lastVulnDate = LocalDate.now().minusDays(scanThreshold).toString();
    
    private static List<Map<String,String>> errorList = new ArrayList<>();
   
    /**
     * Execute.
     * @return 
     */
    public Map<String, Object> execute() {

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("type", type);
        stats.put("start_time", new SimpleDateFormat(TIME_FORMAT).format(new java.util.Date()));

        init();

        Map<String, List<?>> procssInfo = new HashMap<>();
        try {
            procssInfo = fetchHostAssets(type);
        } catch (Exception e) {
            LOGGER.error("Error fetching host assets ", e);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Error fetching host assets");
            errorMap.put(ERROR_TYPE, FATAL);
            errorMap.put(EXCEPTION, e.getMessage());
            errorList.add(errorMap);
        }

        List<?> processList = procssInfo.get(PROCESSED);
        List<?> uploadList = procssInfo.get(UPLOADED);
        List<?> failedList = procssInfo.get(FAILED);

        LOGGER.info("Total processed {}", uploadList.size());

        if (!uploadList.isEmpty()) {
           new HostAssetsEsIndexer().wrapUp(type, CURR_DATE,errorList);
        }

        stats.put("end_time", new SimpleDateFormat(TIME_FORMAT).format(new java.util.Date()));
        stats.put(PROCESSED, processList.size());
        stats.put("type", type);
        stats.put(UPLOADED, uploadList.size());
        stats.put(FAILED, failedList.size());
        stats.put("processedHosts", processList);
        stats.put("uploadedHosts", uploadList);
        stats.put("failedHosts", failedList);
        updateStas(stats);
        
        return ErrorManageUtil.formErrorCode(errorList);

    }

    /**
     * Inits the.
     */
    private void init() {

        String indexName = ds+"_" + type;
        List<String> filters = new ArrayList<>();
        if ("ec2".equals(type)) {
            filters = Arrays.asList("_docid", "privateipaddress", "tags.Name", RESOURCE_ID, "tags.Application",
                    "accountid", "accountname", "statename", "platform", VPC_ID, "publicipaddress", "imageid");
        } else if ("onpremserver".equals(type)) {
            filters = Arrays.asList("_docid", "name", "fqdn", "ip_address", RESOURCE_ID);
        }else if("virtualmachine".equals(type)){
            filters = Arrays.asList("_docid", "computerName", "privateIpAddress","primaryNCIMacAddress", RESOURCE_ID);
        }
        currentInfo = ElasticSearchManager.getExistingInfo(indexName, type, filters, true);

       
        filters = Arrays.asList(RESOURCE_ID, "id");
        currentQualysInfo = ElasticSearchManager.getExistingInfo(indexName, "qualysinfo", filters, true);

        LOGGER.debug("Total current resources  : {}", currentInfo.size());
        
        if ("ec2".equals(type)) {
            currentInfo = currentInfo.entrySet().stream()
                    .filter(entry -> "running".equals(entry.getValue().get("statename")))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

            LOGGER.info("Total current resources  Running : {}", currentInfo.size());

            ec2EniMap = Util.fetchEc2EniInfo();

            Set<String> ec2EniList = ec2EniMap.entrySet().stream().flatMap(entry -> entry.getValue().stream())
                    .collect(Collectors.toSet());
            eniMacMap = Util.fetchEniMacInfo();
            Set<String> eniList = eniMacMap.keySet();

            Set<String> missingEniMappings = ec2EniList.stream().filter(ec2Eni -> !eniList.contains(ec2Eni))
                    .collect(Collectors.toSet());

            LOGGER.info("Missing Enis {}  >  {}", missingEniMappings.size(), missingEniMappings);
            Map<String, List<String>> VpcPublicIpInfo = Util.fetchVPCtoNatIPInfo();

            Set<String> vpcList = currentInfo.entrySet().stream().map(entry -> entry.getValue().get(VPC_ID))
                    .collect(Collectors.toSet());
            VpcPublicIpInfo = VpcPublicIpInfo.entrySet().stream().filter(entry -> vpcList.contains(entry.getKey()))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            vpcNatIpAssets = fetchAssetsWithNatIpAsAddress(VpcPublicIpInfo);          
            LOGGER.debug("Initialisation Complete");
        }

        filters = Arrays.asList("qid", "vulntype", "severitylevel", "title", "category", "patchable", "pciflag",
                "classification");
        vulnInfoMap = ElasticSearchManager.getExistingInfo("qualys-kb", "kb", filters, true);
    }

    /**
     * Fetch host assets.
     *
     * @param type the type
     * @return the map
     * @throws Exception the exception
     */
    private Map<String, List<?>> fetchHostAssets(String type) throws Exception {

        Map<String, List<?>> procssInfo = new HashMap<>();

        List<Map<String, Object>> processList = new ArrayList<>();
        List<String> uploadList = new ArrayList<>();
        List<Map<String, String>> noPrfileList = new ArrayList<>();

        procssInfo.put(PROCESSED, processList);
        procssInfo.put(UPLOADED, uploadList);
        procssInfo.put(FAILED, noPrfileList);

        Map<String, Map<String, Object>> hostAssets = new HashMap<>();

        currentInfo.entrySet().stream().forEach(entry -> {
	        try {
	            String docid = entry.getKey();
	            String resouceId = entry.getValue().get(RESOURCE_ID);
	            String name = "";
	            String ip = "";
	            String vmMac="";
	            if ("onpremserver".equals(type)) {
	                name = entry.getValue().get("name");
	                ip = entry.getValue().get("ip_address");
	            } else if("ec2".equals("type")) {
	                name = entry.getValue().get("tags.Name");
	                ip = entry.getValue().get("privateipaddress");
	            }else if("virtualmachine".equals(type)){
	                name = entry.getValue().get("computerName");
	                ip = entry.getValue().get("privateIpAddress");
	                vmMac = entry.getValue().get("primaryNCIMacAddress");
	            }
	
	            Map<String, Object> processinfo = new LinkedHashMap<>();
	            processinfo.put("_resouceId", resouceId);
	            processinfo.put("name", name == null ? "" : name);
	            processinfo.put("ip", ip);
	            
	            Map<String, Object> hostAsset = null;
	            if ("ec2".equals(type)) {
	                /*
	                 * EC2 Instance ID based lookup is currently supported and is the preferred approach
	                 * If it fails, we need to search based on IP and other fallback approaches.
	                 * Onprem still continues with ip and name
	                 */
	                hostAsset = fetchBasedOnInstanceID(resouceId,processinfo);
	            }
	            
	            if(hostAsset==null){ // For ec2, instancedid based lookup fails
	                String inputXml = "<ServiceRequest> " + "<preferences><limitResults>100</limitResults></preferences>"
	                        + "<filters>" + "<Criteria field=\"address\" operator=\"EQUALS\">%s</Criteria>"
	                        + "<Criteria field=\"lastVulnScan\" operator=\"GREATER\">%s</Criteria>" + "</filters>"
	                        + "</ServiceRequest>";
	                
	                List<Map<String, Object>> respData = null;
	                if (!Strings.isNullOrEmpty(ip)) {
	                    String _inputXml = String.format(inputXml, ip, lastVulnDate);
	     
	                    respData = getHostData(uriPost, _inputXml);
	       
	                }
	
	                if (respData == null)
	                    respData = new ArrayList<>();
	
	                processinfo.put("totalProfilesFound", respData.size());
	                processinfo.put("profiles", Util.fetchTrackinMethodAndQids(respData));
	
	                List<Map<String, Object>> _respData = Util.sortOnLastVulnScan(respData);
	
	                if ("ec2".equals(type)) {
	                    for (int i = 0; i < _respData.size(); i++) {
	                        Map<String, Object> host = _respData.get(i);
	                        Long id = Double.valueOf(host.get("id").toString()).longValue();
	                        String trackingMethod = host.get(TRACKING_METHOD).toString();
	
	                        if (matchBasedonInstanceId(host, resouceId)) {
	                            processinfo.put(TRACKING_METHOD, trackingMethod);
	                            processinfo.put(MATCH_FOUND_BY, "InstanceId > Id:" + id);
	                            hostAsset = host;
	                        } else if (matchBasedonMacAddress(host, resouceId, ip, processinfo)) {
	                            processinfo.put(TRACKING_METHOD, trackingMethod);
	                            hostAsset = host;
	                            processinfo.put(MATCH_FOUND_BY, "Mac/Eni > Id:" + id);
	                        }
	                        if (hostAsset != null) {
	                            if (i > 0) {
	                                processinfo.put("matchFoundAt", i);
	                            }
	                            break;
	                        }
	                    }
	
	                    if (hostAsset == null) {
	                        hostAsset = fallbackNameBasedMatch(name, ip);
	                        if (hostAsset != null) {
	                            processinfo.put(TRACKING_METHOD, hostAsset.get(TRACKING_METHOD).toString());
	                            processinfo.put(MATCH_FOUND_BY, "FallBack Name Match > Id:"
	                                    + Double.valueOf(hostAsset.get("id").toString()).longValue());
	                        }
	                    }
	                    if (hostAsset == null) {
	                        hostAsset = fallbackNatIpBasedMatch(docid, ip);
	                        if (hostAsset != null) {
	                            processinfo.put(TRACKING_METHOD, hostAsset.get(TRACKING_METHOD).toString());
	                            processinfo.put(MATCH_FOUND_BY, "FallBack NAT-IP Match > Id:"
	                                    + Double.valueOf(hostAsset.get("id").toString()).longValue());
	                        }
	                    }
	                } else {
	                    
	                    // Azure VM : MacID based lookup
	                    if("virtualmachine".equals(type)){
	                        for (int i = 0; i < _respData.size(); i++) {
	                            Map<String, Object> host = _respData.get(i);
	                            if (matchBasedonMacAddressVM(ip, vmMac, host)) {
	                                hostAsset = host;
	                                processinfo.put(TRACKING_METHOD, hostAsset.get(TRACKING_METHOD).toString());
	                                processinfo.put(MATCH_FOUND_BY,
	                                        "MacAddress > Id:" + Double.valueOf(hostAsset.get("id").toString()).longValue());
	                            }
	                            if (hostAsset != null) {
	                                if (i > 0) {
	                                    processinfo.put("matchFoundAt", i);
	                                }
	                                break;
	                            }
	                        }
	                    }
	                        
	                    if(hostAsset==null){
	                        for (int i = 0; i < _respData.size(); i++) {
	                            Map<String, Object> host = _respData.get(i);
	                            if (matchBasedonName(host, name)) {
	                                hostAsset = host;
	                                processinfo.put(TRACKING_METHOD, hostAsset.get(TRACKING_METHOD).toString());
	                                processinfo.put(MATCH_FOUND_BY,
	                                        "Name > Id:" + Double.valueOf(hostAsset.get("id").toString()).longValue());
	                            }
	                            if (hostAsset != null) {
	                                if (i > 0) {
	                                    processinfo.put("matchFoundAt", i);
	                                }
	                                break;
	                            }
	                        }
	                    }
	
	                }
	
	                if (hostAsset == null) {
	                    hostAsset = fallbackIdBasedMatch(resouceId, processinfo);
	                }
	                
	                if (hostAsset == null) {
	                    hostAsset = fallbackToCurrentInfo(resouceId, processinfo);
	                }
	            }
	            
	            // This is needed to ensure the vulnifo is available in the hostasset if not we need to fetch it by retry or from current data
	            hostAsset = checkAndFetchVulnInfo(type, resouceId, processinfo, hostAsset);
	
	            synchronized (processList) {
	                processList.add(processinfo);
	            }
	
	            Map<String, Map<String, Object>> _hostAssets = null;
	            if (hostAsset != null) {
	                hostAsset.put(RESOURCE_ID, resouceId);
	                processinfo.put(LAST_VULN_SCAN, hostAsset.get(LAST_VULN_SCAN));
	                synchronized (hostAssets) {
	                    uploadList.add(resouceId);
	                    hostAssets.put(docid, hostAsset);
	                    if (hostAssets.size() >= 50) {
	                        _hostAssets = new HashMap<>(hostAssets);
	                        hostAssets.clear();
	                    }
	                }
	            } else {
	                synchronized (noPrfileList) {
	                    noPrfileList.add(entry.getValue());
	                }
	            }
	            if (_hostAssets != null) {
	                Util.processAndTransform(_hostAssets, vulnInfoMap, CURR_DATE);
	                new HostAssetsEsIndexer().postHostAssetToES(_hostAssets, ds,type,errorList);
	            }
	        } catch (Exception e) {
	            LOGGER.error("Error Fetching data for " + entry.getKey(), e);
	            Map<String,String> errorMap = new HashMap<>();
	            errorMap.put(ERROR, "Error Fetching data for " + entry.getKey());
	            errorMap.put(ERROR_TYPE, WARN);
	            errorMap.put(EXCEPTION, e.getMessage());
	            errorList.add(errorMap);
	        }
        });

        Util.processAndTransform(hostAssets, vulnInfoMap, CURR_DATE);
        new HostAssetsEsIndexer().postHostAssetToES(hostAssets,ds, type,errorList);
        return procssInfo;
    }
    
    private Map<String, Object> fetchBasedOnInstanceID(String resouceId, Map<String, Object> processinfo) {
        Map<String, Object> hostAsset = null;
        String inputXmlWithInstanceId = "<ServiceRequest> " + "<preferences><limitResults>100</limitResults></preferences>"
                + "<filters>" + "<Criteria field=\"instanceId\" operator=\"EQUALS\">%s</Criteria>"
                + "<Criteria field=\"lastVulnScan\" operator=\"GREATER\">%s</Criteria>" + "</filters>"
                + "</ServiceRequest>";
        String _inputXml = String.format(inputXmlWithInstanceId, resouceId, lastVulnDate);
        List<Map<String, Object>> respData = getHostData(uriPost, _inputXml);
        if(respData!=null && !respData.isEmpty()){
            hostAsset = respData.get(0);
            Long id = Double.valueOf(hostAsset.get("id").toString()).longValue();
            String trackingMethod = hostAsset.get(TRACKING_METHOD).toString();
            processinfo.put(TRACKING_METHOD, trackingMethod);
            processinfo.put(MATCH_FOUND_BY,  "InstanceId Lookup > Id:" + id);
        }
        return hostAsset;
    }
    private Map<String, Object> fallbackToCurrentInfo(String resouceId, Map<String, Object> processinfo) {
        Map<String, Object> hostAsset = null;
        try {
            if (currentQualysInfo.get(resouceId) != null) {
                String strQid = currentQualysInfo.get(resouceId).get("id");
                Long qualysId = Double.valueOf(strQid).longValue();
                hostAsset = Util.fetchCurretQualysInfo(type,resouceId);    
                if (hostAsset != null && Util.isScanInfoAvailable(hostAsset,scanThreshold)) {
                    processinfo.put("fallbackInfo", "Existing Match, Id:" + qualysId);
                } else {
                    hostAsset = null;
                }
            }
        }catch (Exception e) {
            LOGGER.error("Error Fetching Current Info ",e);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Error Fetching Current Info");
            errorMap.put(ERROR_TYPE, WARN);
            errorMap.put(EXCEPTION, e.getMessage());
            errorList.add(errorMap);
        }
        return hostAsset;
    }

    /**
     * Check and fetch vuln info.
     *
     * @param type the type
     * @param resouceId the resouce id
     * @param processinfo the processinfo
     * @param hostAsset the host asset
     * @return the map
     */
    private Map<String, Object> checkAndFetchVulnInfo(String type, String resouceId, Map<String, Object> processinfo,
            Map<String, Object> hostAsset) {

        Map<String, Object> host = hostAsset;
        if (host != null && host.get("vuln") == null) {

            processinfo.put(VULN_MISSING, "true");
            // Retry with the current matched ID
            host = fetchhostAssetWithID(Double.valueOf(host.get("id").toString()).longValue());

            if (host != null && host.get("vuln") != null) {
                processinfo.put(VULN_MISSING, "FetchedInRetry");
            } else {
                try {
                    Map<String, Object> _hostAsset = null;
                    if (currentQualysInfo.get(resouceId) != null) {
                        _hostAsset = Util.fetchCurretQualysInfo(type, resouceId);
                        if(!Util.isScanInfoAvailable(_hostAsset, scanThreshold)){
                            _hostAsset = null;
                        }
                    }
                    host = _hostAsset;
                    if (host != null) {
                        processinfo.put(VULN_MISSING, "OldInfoUsed");
                    }

                } catch (ParseException | IOException e) {
                    LOGGER.error("Error in checkAndFetchVulnInfo", e);
                    Map<String,String> errorMap = new HashMap<>();
                    errorMap.put(ERROR, "Error in checkAndFetchVulnInfo");
                    errorMap.put(ERROR_TYPE, WARN);
                    errorMap.put(EXCEPTION, e.getMessage());
                    errorList.add(errorMap);
                }
            }
        }
        return host;
    }

    /**
     * Match basedon instance id.
     *
     * @param host the host
     * @param instanceId the instance id
     * @return true, if successful
     */
    @SuppressWarnings("unchecked")
    private boolean matchBasedonInstanceId(Map<String, Object> host, String instanceId) {
        Map<String, Object> sourceInfo = (Map<String, Object>) host.get("sourceInfo");
        String _instanceId = "";
        if (sourceInfo != null) {
            List<Map<String, Object>> sourceInfoList = (List<Map<String, Object>>) sourceInfo.get("list");
            if (sourceInfoList != null) {
                _instanceId = sourceInfoList.stream()
                        .filter(_sourceInfo -> _sourceInfo.get("Ec2AssetSourceSimple") != null)
                        .map(_sourceInfo -> ((Map<String, Object>) _sourceInfo.get("Ec2AssetSourceSimple"))
                                .get("instanceId").toString())
                        .collect(Collectors.joining());
            }
        }
        return instanceId.equalsIgnoreCase(_instanceId);
    }

    /**
     * Update stas.
     *
     * @param stats the stats
     */
    private void updateStas(Map<String, Object> stats) {
        String statsJson = ElasticSearchManager.createESDoc(stats);
        try {
            ElasticSearchManager.invokeAPI("POST", "/datashipper/qualys-stats", statsJson);
        } catch (IOException e) {
            LOGGER.error("Error in updateStas", e);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Error in updateStas");
            errorMap.put(ERROR_TYPE, WARN);
            errorMap.put(EXCEPTION, e.getMessage());
            errorList.add(errorMap);
        }
    }

    /**
     * Fallback nat ip based match.
     *
     * @param resouceId the resouce id
     * @param ip the ip
     * @return the map
     */
    private Map<String, Object> fallbackNatIpBasedMatch(String resouceId, String ip) {

        Map<String, Object> hostAsset = null;

        String vpcid = currentInfo.get(resouceId).get(VPC_ID);
        List<Map<String, Map<String, Object>>> natIpAssets = vpcNatIpAssets.get(vpcid);
        if (natIpAssets != null) {
            List<Map<String, Object>> idList = natIpAssets.stream().filter(obj -> obj.get(ip) != null)
                    .map(obj -> obj.get(ip))
                    .sorted((obj1, obj2) -> LocalDateTime
                            .parse(obj2.get(MODIFIED).toString(), DateTimeFormatter.ISO_DATE_TIME)
                            .compareTo(LocalDateTime.parse(obj1.get(MODIFIED).toString(),
                                    DateTimeFormatter.ISO_DATE_TIME)))
                    .collect(Collectors.toList());
            if (!idList.isEmpty()) {
                Long qualysId = Double.valueOf(idList.get(0).get("id").toString()).longValue();
                hostAsset = fetchhostAssetWithID(qualysId);
            }
        }
        return hostAsset;
    }

    /**
     * Fallback name based match.
     *
     * @param name the name
     * @param ip the ip
     * @return the map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> fallbackNameBasedMatch(String name, String ip) {
        Map<String, Object> hostAsset = null;
        String inputXml = "<ServiceRequest> " + "<preferences><limitResults>100</limitResults></preferences>"
                + "<filters>" + "<Criteria field=\"name\" operator=\"CONTAINS\">%s</Criteria>"
                + "<Criteria field=\"lastVulnScan\" operator=\"GREATER\">%s</Criteria>" + "</filters>"
                + "</ServiceRequest>";
        String _inputXml = String.format(inputXml, name, lastVulnDate);
        List<Map<String, Object>> hosts = getHostData(uriPost, _inputXml);
        if (hosts != null && !hosts.isEmpty()) {
            String _ip = ip;
            hosts = hosts.stream().filter(host -> {
                boolean isIpMatches = false;
                Map<String, Object> nwinterfaces = (Map<String, Object>) host.get(NW_INTERFACE);
                if (nwinterfaces != null) {
                    List<Map<String, Map<String, String>>> nwInterfaceList = (List<Map<String, Map<String, String>>>) nwinterfaces
                            .get("list");
                    if (nwInterfaceList != null) {
                        isIpMatches = nwInterfaceList.stream()
                                .filter(obj -> _ip.equals(obj.get(HOST_ASSET_INTERFACE).get(ADDRESS))).count() > 0;
                    }
                }
                return isIpMatches;
            }).sorted((obj1, obj2) -> LocalDateTime
                    .parse(obj2.get("lastVulnScan").toString(), DateTimeFormatter.ISO_DATE_TIME).compareTo(
                            LocalDateTime.parse(obj1.get("lastVulnScan").toString(), DateTimeFormatter.ISO_DATE_TIME)))
                    .collect(Collectors.toList());

            if (hosts != null && !hosts.isEmpty()) {
                hostAsset = hosts.get(0);
            }
        }

        return hostAsset;

    }

    /**
     * Fallback id based match.
     *
     * @param resouceId the resouce id
     * @param processinfo the processinfo
     * @return the map
     */
    private Map<String, Object> fallbackIdBasedMatch(String resouceId, Map<String, Object> processinfo) {
        Map<String, Object> hostAsset = null;
        if (currentQualysInfo.get(resouceId) != null) {
            String strQid = currentQualysInfo.get(resouceId).get("id");
            Long qualysId = Double.valueOf(strQid).longValue();
            hostAsset = fetchhostAssetWithID(qualysId);
            if (hostAsset != null && Util.isScanInfoAvailable(hostAsset,scanThreshold)) {
                processinfo.put("fallbackInfo", "Id Match, Id:" + qualysId);
            } else {
                hostAsset = null;
            }
        }
        return hostAsset;
    }

    /**
     * Fetchhost asset with ID.
     *
     * @param qualysId the qualys id
     * @return the map
     */
    private Map<String, Object> fetchhostAssetWithID(Long qualysId) {
        Map<String, Object> hostAsset = null;
        String inputXml;
        String _inputXml;
        List<Map<String, Object>> hosts;
        inputXml = "<ServiceRequest> " + "<preferences><limitResults>1</limitResults></preferences>" + "<filters>"
                + "<Criteria field=\"id\" operator=\"EQUALS\">%s</Criteria>"
                + "<Criteria field=\"lastVulnScan\" operator=\"GREATER\">%s</Criteria>" + "</filters>"
                + "</ServiceRequest>";
        _inputXml = String.format(inputXml, qualysId, lastVulnDate);

        hosts = getHostData(uriPost, _inputXml);

        if (hosts != null && !hosts.isEmpty()) {
            hostAsset = hosts.get(0);
        }
        return hostAsset;
    }
    

    /**
     * Fetch assets with nat ip as address.
     *
     * @param vpcIpInfo the vpc ip info
     * @return the map
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<Map<String, Map<String, Object>>>> fetchAssetsWithNatIpAsAddress(
            Map<String, List<String>> vpcIpInfo) {
        
        Map<String, List<Map<String, Map<String, Object>>>> vpcAssets = new HashMap<>(); // vpciid-natip-ip-qid
        
        vpcIpInfo.entrySet().forEach(entry -> {
            String vpcId = entry.getKey();
            List<String> ipList = entry.getValue();
            List<Map<String, Map<String, Object>>> natIpQidInfo = new ArrayList<>();
            for (String natIp : ipList) {
                
                String inputXml = "<ServiceRequest> " + "<preferences><limitResults>1000</limitResults></preferences>"
                        + "<filters>" + "<Criteria field=\"address\" operator=\"EQUALS\">%s</Criteria>"
                        + "<Criteria field=\"lastVulnScan\" operator=\"GREATER\">%s</Criteria>" + "</filters>"
                        + "</ServiceRequest>";
                String _inputXml = String.format(inputXml, natIp, lastVulnDate);
             
                List<Map<String, Object>> hosts = getHostData(uriPost+"?fields=id,modified,networkInterface.list", _inputXml);
                Map<String, Map<String, Object>> ipQidInfo = new HashMap<>();
                if (hosts != null && !hosts.isEmpty()) {
                    hosts.stream().forEach(host -> {
                        String id = host.get("id").toString();
                        Object modified = host.get(MODIFIED);
                        Map<String, Object> nwinterfaces = (Map<String, Object>) host.get(NW_INTERFACE);
                        if (nwinterfaces != null) {
                            List<Map<String, Map<String, String>>> nwInterfaceList = (List<Map<String, Map<String, String>>>) nwinterfaces
                                    .get("list");
                            if (nwInterfaceList != null) {
                                nwInterfaceList.stream().forEach(obj -> {
                                    String ip = obj.get(HOST_ASSET_INTERFACE).get(ADDRESS);
                                    Map<String, Object> qidInfo = ipQidInfo.get(ip);
                                    if (qidInfo == null || (LocalDateTime
                                            .parse(modified.toString(), DateTimeFormatter.ISO_DATE_TIME)
                                            .isAfter(LocalDateTime.parse(qidInfo.get(MODIFIED).toString(),
                                                    DateTimeFormatter.ISO_DATE_TIME)))) {
                                        qidInfo = new HashMap<>();
                                        qidInfo.put("id", id);
                                        qidInfo.put(MODIFIED, modified);
                                        ipQidInfo.put(ip, qidInfo);
                                    }
                                });
                            }
                        }
                    });
                }
                natIpQidInfo.add(ipQidInfo);
            }
            vpcAssets.put(vpcId, natIpQidInfo);
        });
        return vpcAssets;
    }

    /**
     * Match basedon mac address.
     *
     * @param host the host
     * @param resouceId the resouce id
     * @param ip the ip
     * @param processInfo the process info
     * @return true, if successful
     */
    @SuppressWarnings("unchecked")
    private boolean matchBasedonMacAddress(Map<String, Object> host, String resouceId, String ip,
            Map<String, Object> processInfo) {
        List<String> eniList = ec2EniMap.get(resouceId);
        List<String> macAddressList = new ArrayList<>();
        if (eniList != null) {
            eniList.forEach(eni -> macAddressList.add(eniMacMap.get(eni)));
            Map<String, Object> nwinterfaces = (Map<String, Object>) host.get(NW_INTERFACE);
            if (nwinterfaces != null) {
                List<Map<String, Map<String, String>>> nwInterfaceList = (List<Map<String, Map<String, String>>>) nwinterfaces
                        .get("list");
                return isMatchBasedOnMac(ip, nwInterfaceList, eniList, macAddressList, host, processInfo);
            }
        }
        return false;
    }

    /**
     * Checks if is match based on mac.
     *
     * @param ip the ip
     * @param nwInterfaceList the nw interface list
     * @param eniList the eni list
     * @param macAddressList the mac address list
     * @param host the host
     * @param processInfo the process info
     * @return true, if is match based on mac
     */
    private boolean isMatchBasedOnMac(String ip, List<Map<String, Map<String, String>>> nwInterfaceList,
            List<String> eniList, List<String> macAddressList, Map<String, Object> host,
            Map<String, Object> processInfo) {
        String trackingMethod = host.get(TRACKING_METHOD).toString();
        String id = Long.toString(Double.valueOf(host.get("id").toString()).longValue());
        if (nwInterfaceList != null) {
            for (Map<String, Map<String, String>> nwInterface : nwInterfaceList) {
                String _ip = nwInterface.get(HOST_ASSET_INTERFACE).get(ADDRESS);
                String mac = nwInterface.get(HOST_ASSET_INTERFACE).get("macAddress");
                String eniId = nwInterface.get(HOST_ASSET_INTERFACE).get("interfaceId");

                if ("QAGENT".equals(trackingMethod) && mac == null) {
                    processInfo.put("QAGNT_MAC_MISSING", id);
                    LOGGER.info("QAGNT_MAC_MISSING : {}", id);
                }
                mac = (mac == null ? "" : mac.toLowerCase());
                eniId = (eniId == null ? "" : eniId.toLowerCase());
                if (_ip.equals(ip) && (macAddressList.contains(mac) || eniList.contains(eniId))) {
                    return true;
                }
            }
        }
        return false;

    }
    
    @SuppressWarnings("unchecked")
    private boolean matchBasedonMacAddressVM(String vmIp,String vmMac,Map<String, Object> host){
        Map<String, Object> nwinterfaces = (Map<String, Object>) host.get(NW_INTERFACE);
        if (nwinterfaces != null && vmMac !=null) {
            List<Map<String, Map<String, String>>> nwInterfaceList = (List<Map<String, Map<String, String>>>) nwinterfaces
                    .get("list");
            String macRegex = "[^a-z0-9]";
            vmMac = vmMac.toLowerCase().replaceAll(macRegex, "");
            if (nwInterfaceList != null) {
                for (Map<String, Map<String, String>> nwInterface : nwInterfaceList) {
                    String _ip = nwInterface.get(HOST_ASSET_INTERFACE).get(ADDRESS);
                    String mac = nwInterface.get(HOST_ASSET_INTERFACE).get("macAddress");
                    mac = (mac == null ? "" : mac.toLowerCase().replaceAll(macRegex, ""));
                    if (vmIp.equals(_ip) && vmMac.equals(mac)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Match basedon name.
     *
     * @param host the host
     * @param nameParam the name param
     * @return true, if successful
     */
    private boolean matchBasedonName(Map<String, Object> host, String nameParam) {
        String name = nameParam.toLowerCase();
        String _name = (String) host.get("name");
    
        _name = _name == null ? "null" : _name.toLowerCase();
        return (_name.contains(name) || name.contains(_name));

    }
}
