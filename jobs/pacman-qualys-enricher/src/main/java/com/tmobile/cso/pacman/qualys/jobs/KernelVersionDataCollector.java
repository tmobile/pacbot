package com.tmobile.cso.pacman.qualys.jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.tmobile.cso.pacman.qualys.Constants;
import com.tmobile.cso.pacman.qualys.dto.HOSTLISTVMDETECTIONOUTPUT;
import com.tmobile.cso.pacman.qualys.dto.HOSTLISTVMDETECTIONOUTPUT.RESPONSE.HOSTLIST.HOST;
import com.tmobile.cso.pacman.qualys.util.ElasticSearchManager;
import com.tmobile.cso.pacman.qualys.util.ErrorManageUtil;

public class KernelVersionDataCollector extends QualysDataImporter implements Constants{
    
    /** The log. */
    private static Logger log = LoggerFactory.getLogger(KernelVersionDataCollector.class);
    private static String kvUri = System.getProperty("qualys_api_url") ;
    String loaddate = new SimpleDateFormat("yyyy-MM-dd H:mm:00Z").format(new java.util.Date());
    
    private static List<Map<String,String>> errorList = new ArrayList<>();
    
    public Map<String, Object> execute() {
        
        List<Map<String, Object>> docs = new ArrayList<>();
         
        ElasticSearchManager.createType(AWS_EC2, "kernelinfo", "ec2");
       
        
        log.info("Start fetching kernel info from Qualys");
        
        Map<String, Map<String, String>> tempQualysInfo = new HashMap<>();
        Map<String, Map<String, String>> qualysInfo = new HashMap<>();
        try {
            List<String> filters = Arrays.asList("qwebHostId",RESOURCE_ID,DOC_ID);
            tempQualysInfo= ElasticSearchManager.getExistingInfo(AWS_EC2,"qualysinfo", filters,true);
            tempQualysInfo.forEach((k,v)->qualysInfo.put(String.valueOf(Double.valueOf(k).longValue()), v));
        }
        catch (Exception e) {
            log.error("Error in KernelVersionDataCollector ", e);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Error in KernelVersionDataCollector");
            errorMap.put(ERROR_TYPE, FATAL);
            errorMap.put(EXCEPTION, e.getMessage());
            errorList.add(errorMap);
        }

        int splitter = 10;
        Set<String> qualysIds = qualysInfo.keySet();
       
        List<String> qualysIdList = StreamSupport.stream(Iterables.partition(qualysIds, splitter).spliterator(),false).map(obj->obj.stream().collect(Collectors.joining(","))).collect(Collectors.toList());
     
        Map<String,String> kernetInfo;
        for (String ids : qualysIdList){
            kernetInfo = fetchKernelVersion(ids);
            docs.addAll(prepareDocs(kernetInfo,qualysInfo)); 
        }
        ElasticSearchManager.uploadData(AWS_EC2, "kernelinfo", docs,DOC_ID,"@id",true);
        
        return ErrorManageUtil.formErrorCode(errorList);
    }
    
    
    private Map<String,String> fetchKernelVersion(String ids){
        Map<String,String> kernetInfo = new HashMap<>();
        try {
            String resultXML = callApi(kvUri+ids, "GET", null, null);
            XMLStreamReader reader = buildXMLReader(resultXML);
            
            JAXBContext jaxbContext = JAXBContext.newInstance(HOSTLISTVMDETECTIONOUTPUT.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            HOSTLISTVMDETECTIONOUTPUT resp = (HOSTLISTVMDETECTIONOUTPUT) jaxbUnmarshaller.unmarshal(reader);
            String kernelVersion = "";
            if(resp.getRESPONSE().getHOSTLIST() != null) {
                List<HOST> hostList = resp.getRESPONSE().getHOSTLIST().getHOST();
                for(HOST host : hostList){
                    kernelVersion = host.getDETECTIONLIST().getDETECTION().getRESULTS();
                    kernelVersion = kernelVersion.substring(26, kernelVersion.length()).trim();
                    if(!kernelVersion.isEmpty()) {
                        kernetInfo.put(String.valueOf(host.getID()), kernelVersion);
                    }
                }
            }
        }catch (Exception e) {
            log.error("Fetching kernel version ",e);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Error in Fetching kernel version");
            errorMap.put(ERROR_TYPE, WARN);
            errorMap.put(EXCEPTION, e.getMessage());
            errorList.add(errorMap);
        }
        return kernetInfo;
    }
    
    private List<Map<String, Object>> prepareDocs(Map<String,String> kernelInfo, Map<String, Map<String, String>> qualysInfo){
        List< Map<String,Object>> docs = new ArrayList<>();
    
        kernelInfo.forEach((k,v) -> {
        
            Map<String,Object> kernelDetails = new HashMap<>();
            String instanceId = qualysInfo.get(k).get(RESOURCE_ID);
            String docId = qualysInfo.get(k).get(DOC_ID);
            kernelDetails.put("_resourceid", instanceId);
            kernelDetails.put("source", "qualys");
            kernelDetails.put("kernel", v);
            kernelDetails.put("@id", instanceId+"_qualys");
            kernelDetails.put(DOC_ID,docId);
            kernelDetails.put("discoverydate",loaddate);
            kernelDetails.put("qwebhostid",k);
            docs.add(kernelDetails);
        });
        return docs;
    }
}
