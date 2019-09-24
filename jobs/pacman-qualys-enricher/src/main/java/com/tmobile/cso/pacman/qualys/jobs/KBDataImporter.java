package com.tmobile.cso.pacman.qualys.jobs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cso.pacman.qualys.Constants;
import com.tmobile.cso.pacman.qualys.dto.KNOWLEDGEBASEVULNLISTOUTPUT;
import com.tmobile.cso.pacman.qualys.dto.KNOWLEDGEBASEVULNLISTOUTPUT.RESPONSE.VULNLIST;
import com.tmobile.cso.pacman.qualys.dto.Vuln;
import com.tmobile.cso.pacman.qualys.util.ElasticSearchManager;
import com.tmobile.cso.pacman.qualys.util.ErrorManageUtil;


/**
 * The Class KBDataImporter.
 */
public class KBDataImporter extends QualysDataImporter implements Constants{

    /** The log. */
    private static Logger log = LoggerFactory.getLogger(QualysDataImporter.class);
    
    /** The Constant index. */
    private final static String index = "qualys-kb";
    
    /** The Constant type. */
    private final static String type = "kb";
    
    /** The Constant docid. */
    private final static String docid = "qid";
    
    private static List<Map<String,String>> errorList = new ArrayList<>();

    /**
     * Execute.
     * @return 
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> execute() {

       // long DAY_IN_MS = 1000 * 60 * 60 * 24l;
        String kbGetUri = BASE_API_URL + apiMap.get("listKnowledgebase") ;
        /*"&last_modified_after="
                + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        .format(new java.util.Date(System.currentTimeMillis() - (10 * DAY_IN_MS)));*/
        log.info("Calling API {}", kbGetUri);

        List<Map<String, Object>> vulnDetails = new ArrayList<>();
        try {
            String resultXML = callApi(kbGetUri, "GET", null, null);
            XMLStreamReader reader = buildXMLReader(resultXML);

            JAXBContext jaxbContext = JAXBContext.newInstance(KNOWLEDGEBASEVULNLISTOUTPUT.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            KNOWLEDGEBASEVULNLISTOUTPUT resp = (KNOWLEDGEBASEVULNLISTOUTPUT) jaxbUnmarshaller.unmarshal(reader);
            VULNLIST vulnList = resp.getRESPONSE().getVULNLIST();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'z'"));
            vulnList.getVULN().parallelStream().forEach(vuln -> {
                Vuln vulnInfo = new Vuln();
                vulnInfo.setQid(String.valueOf(vuln.getQID()));
                vulnInfo.setVulntype(vuln.getVULNTYPE());
                vulnInfo.setSeveritylevel(vuln.getSEVERITYLEVEL());
                vulnInfo.setTitle(vuln.getTITLE());
                vulnInfo.setCategory(vuln.getCATEGORY());
                vulnInfo.setLastservicemodificationdatetime(vuln.getLASTSERVICEMODIFICATIONDATETIME());
                vulnInfo.setPublisheddatetime(vuln.getPUBLISHEDDATETIME());
                vulnInfo.setBugtraqlist(vuln.getBUGTRAQLIST());
                vulnInfo.setPatchable(String.valueOf(vuln.getPATCHABLE()));
                vulnInfo.setSoftwarelist(vuln.getSOFTWARELIST());
                vulnInfo.setVendorreferencelist(vuln.getVENDORREFERENCELIST());
                vulnInfo.setCvelist(vuln.getCVELIST());
                vulnInfo.setDiagnosis(vuln.getDIAGNOSIS());
                vulnInfo.setDiagnosiscomment(vuln.getDIAGNOSISCOMMENT());
                vulnInfo.setConsequence(vuln.getCONSEQUENCE());
                vulnInfo.setConsequencecomment(vuln.getCONSEQUENCECOMMENT());
                vulnInfo.setSolution(vuln.getSOLUTION());
                vulnInfo.setSolutioncomment(vuln.getSOLUTIONCOMMENT());
                vulnInfo.setCompliancelist(vuln.getCOMPLIANCELIST());
                vulnInfo.setCorrelation(vuln.getCORRELATION());
                vulnInfo.setCvss(vuln.getCVSS());
                vulnInfo.setCvssv3(vuln.getCVSSV3());
                vulnInfo.setPciflag(vuln.getPCIFLAG());
                vulnInfo.setPcireasons(vuln.getPCIREASONS());
                vulnInfo.setSupportedmodules(vuln.getSUPPORTEDMODULES());
                vulnInfo.setDiscovery(vuln.getDISCOVERY());
                vulnInfo.setIsdisabled(vuln.getISDISABLED());
                vulnInfo.setIsdisabled(vuln.getISDISABLED());
                vulnInfo.set_loadDate(new java.util.Date());
                vulnInfo.setLatest(true);
                vulnInfo.setClassification(Util.classifyVuln(vulnInfo));
                synchronized (vulnDetails) {
                    vulnDetails.add(objectMapper.convertValue(vulnInfo, Map.class));
                }
            });
        } catch (JAXBException | IOException | XMLStreamException e) {
            log.error("Error in KBDataImporter ", e);
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put(ERROR, "Error in KBDataImporter");
            errorMap.put(ERROR_TYPE, FATAL);
            errorMap.put(EXCEPTION, e.getMessage());
            errorList.add(errorMap);
        }
        ElasticSearchManager.createIndex(index);
        ElasticSearchManager.createType(index, type);
        ElasticSearchManager.uploadData(index, type, vulnDetails, docid);
        
        return ErrorManageUtil.formErrorCode(errorList);
    }
}
