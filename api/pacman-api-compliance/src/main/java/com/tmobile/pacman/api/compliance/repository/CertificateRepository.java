/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.tmobile.pacman.api.compliance.repository;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * This is the Repository layer which makes call to ElasticSearch
 */
@Repository
public class CertificateRepository implements Constants {

    @Value("${elastic-search.host}")
    private String esHost;
    
    @Value("${elastic-search.port}")
    private int esPort;
    
    private static final String PROTOCOL = "http";
    
    private String esUrl;
    
    private Integer esPageSize = TEN_THOUSAND;
    
    private String esPageScrollTTL = "2m";
    
    @Autowired
    private ComplianceRepository complianceRepository;
    
    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    private static final Log LOGGER = LogFactory.getLog(CertificateRepository.class);

    /**
     * Initialises the esUrl.
     */
    @PostConstruct
    void init() {
        esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
    }

    /**
     * Gets the certicates expiry by application.
     *
     * @param assetGroup name of the asset group
     * @return the certicates expiry by application
     * @throws DataException when there is error while fetching data from ES
     */
    public Map<String, Object> getCertificatesExpiryByApplication(
            String assetGroup) throws DataException {

        Map<String, Object> expiryRules = new HashMap<>();

        StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(
                assetGroup);
        urlToQuery.append(CERT_SEARCH);
        String requestBody = "{\"size\":0,\"aggs\":{\"apps\":{\"terms\":{\"field\":\"tags.Application.keyword\",\"size\":"
                + TEN_THOUSAND
                + "},"
                + "\"aggs\":{\"certs\":{\"children\":{\"type\":\"issue_cert\"},\"aggs\":{\"openfilter\":{\"filter\":{\"term\":{\"issueStatus\":\"open\"}},"
                + "\"aggs\":{\"rules\":{\"terms\":{\"field\":\"ruleId.keyword\",\"size\":10}}}}}}}}}}";

        String responseJson = "";
        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQuery.toString(),
                    requestBody);
        } catch (Exception e) {
            LOGGER.error("Error in getVulnerabilitiesDistribution from ES", e);
            throw new DataException(e);
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = (JsonObject) jsonParser.parse(responseJson);
        JsonObject aggsJson = (JsonObject) jsonParser.parse(resultJson.get(
                "aggregations").toString());
        JsonArray outerBuckets = aggsJson.getAsJsonObject("apps")
                .getAsJsonArray(BUCKETS);
        if (outerBuckets.size() > 0) {
            for (int i = 0; i < outerBuckets.size(); i++) {
                String appName = outerBuckets.get(i).getAsJsonObject()
                        .get("key").getAsString();
                Map<String, Integer> expiryDetails = new HashMap<>();
                expiryDetails.put(EXP_IN_45_DAYS, 0);
                expiryDetails.put(EXP_IN_30_DAYS, 0);
                JsonArray rules = outerBuckets.get(i).getAsJsonObject()
                        .getAsJsonObject("certs").getAsJsonObject("openfilter")
                        .getAsJsonObject("rules").getAsJsonArray(BUCKETS);
                if (rules.size() > 0) {
                    for (int j = 0; j < rules.size(); j++) {
                        if (SSL_CERT_45_DAYS_EXP_RULE.equals(rules.get(j)
                                .getAsJsonObject().get("key").getAsString())) {
                            expiryDetails.put(
                                    EXP_IN_45_DAYS,
                                    Integer.valueOf(rules.get(j)
                                            .getAsJsonObject().get(DOC_COUNT)
                                            .toString()));
                        }
                        if (SSL_CERT_30_DAYS_EXP_RULE.equals(rules.get(j)
                                .getAsJsonObject().get("key").getAsString())) {
                            expiryDetails.put(
                                    EXP_IN_30_DAYS,
                                    Integer.valueOf(rules.get(j)
                                            .getAsJsonObject().get(DOC_COUNT)
                                            .toString()));
                        }
                    }
                }
                if (StringUtils.isNotBlank(appName)) {
                    expiryRules.put(appName, expiryDetails);
                }
            }
        }
        return expiryRules;
    }

    /**
     * Gets the certificates details.
     *
     * @param assetGroup name of the asset group
     * @param searchText used to match any text you are looking for
     * @param filter the filter
     * @return the certificates details
     * @throws DataException the DataException
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCertificatesDetails(String assetGroup,
            String searchText, Map<String, String> filter) throws DataException {

        List<Map<String, Object>> certificateDetails = new ArrayList<>();
        try {
            Map<String, Object> mustFilter = new HashMap<>();
            mustFilter.put("latest", true);
            if (null != filter && filter.size() > 0) {
                filter.forEach((key, value) -> mustFilter.put(key, value));
            }

            StringBuilder urlToQueryBuffer = new StringBuilder(esUrl)
                    .append("/").append(assetGroup).append(CERT_SEARCH)
                    .append("?scroll=").append(esPageScrollTTL);

            String urlToQuery = urlToQueryBuffer.toString();
            String urlToScroll = new StringBuilder(esUrl).append("/")
                    .append("_search").append("/scroll").toString();

            StringBuilder requestBody = new StringBuilder(
                    "{\"size\":").append(TEN_THOUSAND).append(",\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":\"true\"}}");
            if (filter.containsKey("tags.Application.keyword")) {
                requestBody
                        .append(",{\"match\":{\"tags.Application.keyword\":\"");
                requestBody.append(filter.get("tags.Application.keyword"));
                requestBody.append("\"}}");
            }
            if (filter.containsKey("tags.Environment.keyword")) {
                requestBody
                        .append(",{\"match\":{\"tags.Environment.keyword\":\"");
                requestBody.append(filter.get("tags.Environment.keyword"));
                requestBody.append("\"}}");
            }
            if (filter.containsKey(EXP_IN)) {
                if (filter.get(EXP_IN).equals(THIRTY)) {
                    requestBody
                            .append(",{\"has_child\":{\"type\":\"issue_cert\",\"query\":{\"bool\":{\"must\":[{\"term\":{\"issueStatus.keyword\":\"open\"}},{\"term\":{\"type.keyword\":\"issue\"}},{\"match\":{\"ruleId.keyword\":\"");
                    requestBody.append(SSL_CERT_30_DAYS_EXP_RULE);
                    requestBody.append("\"}}]}}}}");
                } else if (filter.get(EXP_IN).equals(FOURTYFIVE)) {
                    requestBody
                            .append(",{\"has_child\":{\"type\":\"issue_cert\",\"query\":{\"bool\":{\"must\":[{\"term\":{\"issueStatus.keyword\":\"open\"}},{\"term\":{\"type.keyword\":\"issue\"}},{\"match\":{\"ruleId.keyword\":\"");
                    requestBody.append(SSL_CERT_45_DAYS_EXP_RULE);
                    requestBody.append("\"}}]}}}}");
                }
                mustFilter.remove(EXP_IN);
            }
            requestBody.append("]}}}");

            List<Map<String, Object>> certificatesDetailsFromES = new ArrayList<>();
            Long totalDocs = getCertificatesDetailsCount(assetGroup, mustFilter);
            String request = requestBody.toString();
            String scroolId = null;
            String responseDetails;
            for (int index = 0; index <= (totalDocs / esPageSize); index++) {
          try{
                    if (!Strings.isNullOrEmpty(scroolId)) {
                        request = elasticSearchRepository.buildScrollRequest(
                                scroolId, esPageScrollTTL);
                        urlToQuery = urlToScroll;
                    }
                    responseDetails = PacHttpUtils.doHttpPost(urlToQuery,
                            request);
                    scroolId = elasticSearchRepository
                            .processResponseAndSendTheScrollBack(
                                    responseDetails, certificatesDetailsFromES);
                } catch (Exception e) {
                    LOGGER.error("Error in getCerticatesDetails from ES", e);
                    throw new DataException(e);
                }

            }

            certificatesDetailsFromES = (List<Map<String, Object>>) CommonUtils
                    .filterMatchingCollectionElements(
                            certificatesDetailsFromES, searchText, true);
            certificatesDetailsFromES.sort(Comparator
                    .comparing(m -> LocalDateTime.parse(
                            (String) m.get(VALID_TO),
                            DateTimeFormatter.ofPattern("M/d/yyyy H:m"))
                            .toLocalDate()));

            for (Map<String, Object> _certificatesDetailsFromES : certificatesDetailsFromES) {
                Map<String, Object> certificate = new LinkedHashMap<>();
                certificate.put("name",
                        _certificatesDetailsFromES.get("commonname"));
                if (StringUtils.isNotEmpty(_certificatesDetailsFromES.get(
                        VALID_TO).toString())) {
                    certificate.put(EXP_IN,
                            calculateExpiryDuration(_certificatesDetailsFromES
                                    .get(VALID_TO).toString()) + " days");
                } else {
                    certificate.put(EXP_IN, 0);
                }
                certificate.put("type",
                        _certificatesDetailsFromES.get("certType"));
                certificate.put("application",
                        _certificatesDetailsFromES.get("tags.Application"));
                certificate.put("environment",
                        _certificatesDetailsFromES.get("tags.Environment"));
                certificate.put("owner",
                        _certificatesDetailsFromES.get("tags.Owner"));
                certificate.put("issuer",
                        _certificatesDetailsFromES.get("issuerdn"));
                certificate.put("validUntil",
                        _certificatesDetailsFromES.get(VALID_TO));
                certificate.put("validfrom",
                        _certificatesDetailsFromES.get("validfrom"));
                certificate.put("status",
                        _certificatesDetailsFromES.get("status"));
                certificateDetails.add(certificate);
            }
        } catch (DataException e) {
            LOGGER.error("Error in getCerticatesDetails", e);
        }
        return certificateDetails;
    }

    /**
     * Gets the certificates details count.
     *
     * @param assetGroup name of the asset group
     * @param mustFilter the must filter
     * @return the certificates details count
     * @throws DataException the data exception
     */
    private long getCertificatesDetailsCount(String assetGroup,
            Map<String, Object> mustFilter) throws DataException {
        try{
        return elasticSearchRepository.getTotalDocumentCountForIndexAndType(
                assetGroup, "cert", mustFilter, null, null, null, null);
        }catch(Exception e){
            throw new DataException(e);
        }
    }

    /**
     * Gets the certificates summary.
     *
     * @param assetGroup name of the asset group
     * @return the certificates summary
     * @throws DataException the data exception
     */
    public Map<String, Object> getCertificatesSummary(String assetGroup)
            throws DataException {
        Map<String, Object> certificateSummary = new HashMap<>();

        Map<String, Long> certificates = complianceRepository
                .getCertificates(assetGroup);
        StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(
                assetGroup);
        urlToQuery.append(CERT_SEARCH);
        String requestBody = "{\"size\":0,\"aggs\":{\"certs\":{\"children\":{\"type\":\"issue_cert\"},\"aggs\":{\"openfilter\":{\"filter\":{\"term\":{\"issueStatus\":\"open\"}},"
                + "\"aggs\":{\"rules\":{\"terms\":{\"field\":\"ruleId.keyword\",\"size\":10}}}}}}}}";

        String responseJson = "";
        try {
            responseJson = PacHttpUtils.doHttpPost(urlToQuery.toString(),
                    requestBody);
        } catch (Exception e) {
          throw new DataException(e);
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject resultJson = (JsonObject) jsonParser.parse(responseJson);
        JsonObject aggsJson = (JsonObject) jsonParser.parse(resultJson.get(
                "aggregations").toString());

        certificateSummary.put(EXP_IN_45_DAYS, 0);
        certificateSummary.put(EXP_IN_30_DAYS, 0);
        try {
            JsonArray rules = aggsJson.getAsJsonObject()
                    .getAsJsonObject("certs").getAsJsonObject("openfilter")
                    .getAsJsonObject("rules").getAsJsonArray(BUCKETS);
            if (rules.size() > 0) {
                for (int j = 0; j < rules.size(); j++) {
                    if (SSL_CERT_45_DAYS_EXP_RULE.equals(rules.get(j)
                            .getAsJsonObject().get("key").getAsString())) {
                        certificateSummary.put(
                                EXP_IN_45_DAYS,
                                Integer.valueOf(rules.get(j).getAsJsonObject()
                                        .get(DOC_COUNT).toString()));
                    }
                    if (SSL_CERT_30_DAYS_EXP_RULE.equals(rules.get(j)
                            .getAsJsonObject().get("key").getAsString())) {
                        certificateSummary.put(
                                EXP_IN_30_DAYS,
                                Integer.valueOf(rules.get(j).getAsJsonObject()
                                        .get(DOC_COUNT).toString()));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in getCerticatesSummary", e);
        }

        float compliantCount = Float.valueOf(certificates.get(CERTIFICATES).toString())
                - Float.valueOf(certificates.get("certificates_expiring").toString());
        DecimalFormat df = new DecimalFormat("#.00");
        if (certificates.get(CERTIFICATES) > 0) {
            certificateSummary.put("compliantPercent", Math.floor(Float
                    .valueOf(df.format((compliantCount / certificates
                            .get(CERTIFICATES)) * HUNDRED))));
        } else {
            certificateSummary.put("compliantPercent", HUNDRED);
        }
        certificateSummary.put("totalCertificates",
                certificates.get(CERTIFICATES));
        return certificateSummary;
    }

    /**
     * Calculates the expiry duration.
     *
     * @param date the date
     * @return the long
     */
    private Long calculateExpiryDuration(String date) {
        LocalDate expiryDate = LocalDateTime.parse(date,
                DateTimeFormatter.ofPattern("M/d/yyyy H:m")).toLocalDate();
        LocalDate today = LocalDateTime.now().toLocalDate();
        return java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
    }

}
