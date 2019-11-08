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
package com.tmobile.pacman.api.compliance.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.exception.ServiceException;
import com.tmobile.pacman.api.compliance.domain.PatchingProgressResponse;
import com.tmobile.pacman.api.compliance.domain.ProjectionRequest;
import com.tmobile.pacman.api.compliance.domain.ProjectionResponse;
import com.tmobile.pacman.api.compliance.repository.ComplianceRepository;
import com.tmobile.pacman.api.compliance.repository.PatchingRepository;
import com.tmobile.pacman.api.compliance.repository.ProjectionRepository;

/**
 * The Class ProjectionServiceImpl.
 */
@Service
public class ProjectionServiceImpl implements ProjectionService, Constants {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The repository. */
    @Autowired
    private ProjectionRepository repository;

    /** The compliance service. */
    @Autowired
    private ComplianceService complianceService;
    
    /** The compliance repository. */
    @Autowired
    private ComplianceRepository complianceRepository;
    
    /** The patching repository. */
    @Autowired
    private PatchingRepository patchingRepository;
    
    /** The projection assetgroups. */
    @Value("${projections.assetgroups}")
    private String projectionAssetgroups;
    
    /** The projection target types. */
    @Value("${projections.targetTypes}")
    private String projectionTargetTypes;

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.ProjectionService#updateProjection(com.tmobile.pacman.api.compliance.domain.ProjectionRequest)
     */
    @Override
    public Boolean updateProjection(ProjectionRequest projectionRequest)
            throws ServiceException {
        try {
            return repository.updateProjectionByTargetType(projectionRequest);
        } catch (DataException e) {
        	logger.error("Error @ updateProjection", e);
            throw new ServiceException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.ProjectionService#getProjection(java.lang.String, int, int)
     */
    @Override
    public ProjectionResponse getProjection(String resourceType, int year,
            int quarter) throws ServiceException {
        Long totalAssets = 0l;
        List<Map<String, Object>> projectionList = new ArrayList<>();
        if (projectionTargetTypes.contains(resourceType)) {
            try {
                // get projection from Database
                projectionList = repository.getProjectionDetailsFromDb(
                        resourceType, year, quarter);
                // get total Inscope Assets from ES
                totalAssets = repository
                        .getTotalAssetCountByTargetType(resourceType);
            } catch (DataException e) {
            	logger.error("Error @ getProjection", e);
                throw new ServiceException(e);
            }
        }

        return new ProjectionResponse("", resourceType, totalAssets, year,
                quarter, projectionList);
    }

    /**
     * Gets the total asse count by target type.
     *
     * @param targetType the target type
     * @return the total asse count by target type
     * @throws ServiceException the service exception
     */
    public Long getTotalAsseCountByTargetType(String targetType)
            throws ServiceException {
        try {
            return repository.getTotalAssetCountByTargetType(targetType);
        } catch (DataException e) {
        	logger.error("Error @ getTotalAsseCountByTargetType", e);
            throw new ServiceException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.ProjectionService#getPatchingAndProjectionByWeek(java.lang.String)
     */
    @Override
    public ProjectionResponse getPatchingAndProjectionByWeek(String assetGroup)
            throws ServiceException {
        if (projectionAssetgroups.contains(assetGroup)) {
            List<Map<String, Object>> patchingAndProjectionProgressList = new ArrayList<>();
            Long totalAssets = 0l;
            LocalDate todayDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("yyyy-MM-dd");
            int quarter = todayDate.get(IsoFields.QUARTER_OF_YEAR);
            int year = todayDate.getYear();
            int weekNumber = 0;
            StringBuilder targetType = new StringBuilder();
            String targetTypes = complianceRepository.getTargetTypeForAG(
                    assetGroup, null);
            List<String> targetTypesList = new ArrayList<>(
                    Arrays.asList(targetTypes.split(",")));
            Map<Integer, Map<String, Object>> onpremProjectionByWeekMap = new HashMap<>();
            Map<Integer, Map<String, Object>> ec2ProjectionByWeekMap = new HashMap<>();
            long totalPatchCount = 0;
            long patchCount = 0;
            long previoudWeekTotalPatchCount = 0;
            long projectionCount;
            long onpremProjectionCount;
            long ec2ProjectionCount;
            long totalProjectionCount = 0;
            long previousWeekProjectionCount = 0;
            Map<String, Object> patchingAndProjection = null;
            // get data from repository
            for (String resourceType : targetTypesList) {
                try {
                    resourceType = resourceType.replaceAll("\'", "");

                    if (resourceType.equalsIgnoreCase(ONPREMSERVER)) {
                        if (targetType.length() > 0) {
                            targetType.append(",").append(resourceType);
                        } else {
                            targetType.append(resourceType);
                        }
                       Long onpremTotalAssets = 0l;
                       if(complianceService.getPatching(
                               assetGroup, resourceType, null).containsKey(TOTAL_INSTANCES)){
                           onpremTotalAssets = complianceService.getPatching(
                                   assetGroup, resourceType, null).get(TOTAL_INSTANCES);
                       }
                       
                        totalAssets += onpremTotalAssets;
                        List<Map<String, Object>> onpremProjectionByWeekList = repository
                                .getProjectionDetailsFromDb(resourceType, year,
                                        quarter);
                        if (!onpremProjectionByWeekList.isEmpty()) {
                            onpremProjectionByWeekMap = onpremProjectionByWeekList
                                    .parallelStream()
                                    .collect(
                                            Collectors.toMap(
                                                    projection -> Integer
                                                            .parseInt(projection
                                                                    .get("week")
                                                                    .toString()),
                                                    projection -> projection));
                        }
                    } else if (resourceType.equalsIgnoreCase(EC2)) {
                        if (targetType.length() > 0) {
                            targetType.append(",").append(resourceType);
                        } else {
                            targetType.append(resourceType);
                        }
                       Long ec2TotalAssets = 0l;
                       if(complianceService.getPatching(
                               assetGroup, resourceType, null).containsKey(TOTAL_INSTANCES)){
                           ec2TotalAssets = complianceService.getPatching(
                                   assetGroup, resourceType, null).get(TOTAL_INSTANCES);
                       }
                       
                        totalAssets += ec2TotalAssets;
 
                        List<Map<String, Object>> ec2ProjectionByWeekList = repository
                                .getProjectionDetailsFromDb(resourceType, year,
                                        quarter);
                        if (!ec2ProjectionByWeekList.isEmpty()) {
                            ec2ProjectionByWeekMap = ec2ProjectionByWeekList
                                    .parallelStream()
                                    .collect(
                                            Collectors.toMap(
                                                    projection -> Integer
                                                            .parseInt(projection
                                                                    .get("week")
                                                                    .toString()),
                                                    projection -> projection));
                        }
                    }
                } catch (DataException e) {
                	logger.error("Error @ getPatchingAndProjectionByWeek", e);
                    throw new ServiceException(e);
                }
            }
            Map<Integer, Long> patchingSnapshot;
            try {
                patchingSnapshot = repository.getPatchingSnapshot(assetGroup);
            } catch (DataException e) {
            	logger.error("Error @ getPatchingAndProjectionByWeek while getting the patching snapshot", e);
                throw new ServiceException(e);
            }
            List<LocalDate> lastDayOfEachWeek = repository
                    .getListOfLastWeekDateOfQuarter();
            for (LocalDate lastdayofWeek : lastDayOfEachWeek) {
                onpremProjectionCount = 0;
                ec2ProjectionCount = 0;
                projectionCount = 0;

                patchingAndProjection = new HashMap<>();

                weekNumber = repository.getWeekNoByDate(lastdayofWeek);
                if (null != patchingSnapshot.get(weekNumber)) {
                    totalPatchCount = patchingSnapshot.get(weekNumber);
                }
                if (totalPatchCount > previoudWeekTotalPatchCount) {
                    patchCount = totalPatchCount - previoudWeekTotalPatchCount;
                } else if (totalPatchCount == 0) {
                    patchCount = 0;
                    totalPatchCount = previoudWeekTotalPatchCount;
                } else {
                    patchCount = totalPatchCount;
                }

                previoudWeekTotalPatchCount = totalPatchCount;
                if (null != onpremProjectionByWeekMap.get(weekNumber)) {
                    Map<String, Object> onpremProjectionDetails = onpremProjectionByWeekMap
                            .get(weekNumber);
                    if (!onpremProjectionDetails.isEmpty()) {
                        onpremProjectionCount = Long
                                .parseLong(onpremProjectionDetails.get(
                                        "projection").toString());
                    }
                }

                if (null != ec2ProjectionByWeekMap.get(weekNumber)) {
                    Map<String, Object> ec2ProjectionDetails = ec2ProjectionByWeekMap
                            .get(weekNumber);
                    if (!ec2ProjectionDetails.isEmpty()) {
                        ec2ProjectionCount = Long
                                .parseLong(ec2ProjectionDetails.get(
                                        "projection").toString());
                    }
                }

                if (onpremProjectionCount > 0 || ec2ProjectionCount > 0) {
                    projectionCount = onpremProjectionCount
                            + ec2ProjectionCount;
                }

                totalProjectionCount = projectionCount
                        + previousWeekProjectionCount;
                previousWeekProjectionCount += projectionCount;
                patchingAndProjection.put("week", weekNumber);
                patchingAndProjection.put("date",
                        lastdayofWeek.format(formatter));
                patchingAndProjection.put("patched", patchCount);
                patchingAndProjection.put("projected", projectionCount);
                patchingAndProjection.put("totalPatched", totalPatchCount);
                patchingAndProjection.put("totalProjected",
                        totalProjectionCount);
                patchingAndProjectionProgressList.add(patchingAndProjection);

            }

            return new ProjectionResponse(assetGroup, targetType.toString(),
                    totalAssets, year, quarter,
                    patchingAndProjectionProgressList);
        } else {
            throw new ServiceException(
                    NOT_ELIGIBLE_PROJECTIONS);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.ProjectionService#getPatchingProgressByDirector(java.lang.String)
     */
    @Override
    public PatchingProgressResponse getPatchingProgressByDirector(
            String assetGroup) throws ServiceException {
        if (projectionAssetgroups.contains(assetGroup)) {
            LocalDate todayDate = LocalDate.now();
            int year = todayDate.getYear();
            int quarter = todayDate.get(IsoFields.QUARTER_OF_YEAR);
            Long totalAssets = 0l;
            StringBuilder targetType = new StringBuilder();
            String quarterScope = "q" + quarter + " scope";
            List<Map<String, Object>> patchingProgressByDirectorList = new ArrayList<>();
            String targetTypes = complianceRepository.getTargetTypeForAG(
                    assetGroup, null);
            List<String> targetTypesList = new ArrayList<>(
                    Arrays.asList(targetTypes.split(",")));
            Map<String, Map<String, Object>> directorListMap = new ConcurrentHashMap<>();
            for (String resourceType : targetTypesList) {
                try {
                    resourceType = resourceType.replaceAll("\'", "");
                    if (resourceType.equalsIgnoreCase(ONPREMSERVER)) {
                        if (targetType.length() > 0) {
                            targetType.append(",").append(resourceType);
                        } else {
                            targetType.append(resourceType);
                        }
                        Map<String, Long> onpremAssetsByApplicationMap = repository
                                .getAssetDetailsByApplication(assetGroup,
                                        resourceType);
                        Map<String, Long> onpremUnPatchedCountByApplicationMap = patchingRepository
                                .getNonCompliantNumberForAgAndResourceType(
                                        assetGroup, resourceType);

                        List<Map<String, Object>> appsDetails = repository.getAppsDetails("OnPrem");

                        if (!appsDetails.isEmpty()) {
                           Long onpremTotalAssets = complianceService.getPatching(
                                    assetGroup, resourceType, null).get(
                                    TOTAL_INSTANCES);
                            totalAssets += onpremTotalAssets;
                            directorListMap = getDirectorsOrExecutorsPatchingProgress(
                                    DIRECTOR, quarterScope, resourceType,
                                    onpremAssetsByApplicationMap, appsDetails,
                                    onpremUnPatchedCountByApplicationMap,
                                    directorListMap,
                                    patchingProgressByDirectorList);
                        }
                    } else if (resourceType.equalsIgnoreCase(EC2)) {
                        if (targetType.length() > 0) {
                            targetType.append(",").append(resourceType);
                        } else {
                            targetType.append(resourceType);
                        }
                        Map<String, Long> ec2AssetsByApplicationMap = repository
                                .getAssetDetailsByApplication(assetGroup,
                                        resourceType);
                        Map<String, Long> ec2UnPatchedCountByApplicationMap = patchingRepository
                                .getNonCompliantNumberForAgAndResourceType(
                                        assetGroup, resourceType);

                        List<Map<String, Object>> appsDetails = repository.getAppsDetails("Cloud");
                        if (!appsDetails.isEmpty()) {
                           Long ec2TotalAssets = complianceService.getPatching(
                                    assetGroup, resourceType, null).get(
                                    TOTAL_INSTANCES);
                            totalAssets += ec2TotalAssets;
                            directorListMap = getDirectorsOrExecutorsPatchingProgress(
                                    DIRECTOR, quarterScope, resourceType,
                                    ec2AssetsByApplicationMap, appsDetails,
                                    ec2UnPatchedCountByApplicationMap,
                                    directorListMap,
                                    patchingProgressByDirectorList);
                        }
                    }
                } catch (DataException e) {
                	logger.error("Error @ getPatchingProgressByDirector", e);
                    throw new ServiceException(e);
                }
            }

            for (Map.Entry<String, Map<String, Object>> entry : directorListMap
                    .entrySet()) {
                Map<String, Object> directorMap = entry.getValue();
                if (null != directorMap) {
                    patchingProgressByDirectorList.add(directorMap);
                }
            }
            Comparator<Map<String, Object>> comp = (m1, m2) -> Integer.compare(
                    new Integer(m2.get(quarterScope).toString()), new Integer(
                            m1.get(quarterScope).toString()));
            Collections.sort(patchingProgressByDirectorList, comp);

            return new PatchingProgressResponse(assetGroup,
                    targetType.toString(), totalAssets, year, quarter,
                    patchingProgressByDirectorList);
        } else {
            throw new ServiceException(
                    NOT_ELIGIBLE_PROJECTIONS);
        }
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.compliance.service.ProjectionService#patchProgByExSponsor(java.lang.String)
     */
    @Override
    public PatchingProgressResponse patchProgByExSponsor(String assetGroup)
            throws ServiceException {
        if (projectionAssetgroups.contains(assetGroup)) {
            LocalDate todayDate = LocalDate.now();
            int year = todayDate.getYear();
            int quarter = todayDate.get(IsoFields.QUARTER_OF_YEAR);
            Long totalAssets = 0l;
            StringBuilder targetType = new StringBuilder();
            String quarterScope = "q" + quarter + " scope"; 
            List<Map<String, Object>> patchingProgressByExecutorsList = new ArrayList<>();
            String targetTypes = complianceRepository.getTargetTypeForAG(
                    assetGroup, null);
            List<String> targetTypesList = new ArrayList<>(
                    Arrays.asList(targetTypes.split(",")));
            Map<String, Map<String, Object>> executorsListMap = new ConcurrentHashMap<>();
            for (String resourceType : targetTypesList) {

                resourceType = resourceType.replaceAll("\'", "");
                if (resourceType.equalsIgnoreCase(ONPREMSERVER)) {
                    if (targetType.length() > 0) {
                        targetType.append(",").append(resourceType);
                    } else {
                        targetType.append(resourceType);
                    }
                    try {
                        Map<String, Long> onpremAssetsByApplicationMap = repository
                                .getAssetDetailsByApplication(assetGroup,
                                        resourceType);
                        Map<String, Long> onpremUnPatchedCountByApplicationMap = patchingRepository
                                .getNonCompliantNumberForAgAndResourceType(
                                        assetGroup, resourceType);

                        List<Map<String, Object>> appsDetails = repository.getAppsDetails("OnPrem");
                        if (!appsDetails.isEmpty()) {
                            Long onpremTotalAssets = complianceService.getPatching(
                                    assetGroup, resourceType, null).get(TOTAL_INSTANCES);
                            totalAssets += onpremTotalAssets;
                            executorsListMap = getDirectorsOrExecutorsPatchingProgress(
                                    EXCUTIVE_SPONSOR, quarterScope, resourceType,
                                    onpremAssetsByApplicationMap, appsDetails,
                                    onpremUnPatchedCountByApplicationMap,
                                    executorsListMap,
                                    patchingProgressByExecutorsList);
                        }
                    } catch (DataException e) {
                    	logger.error("Error @ patchProgByExSponsor", e);
                        throw new ServiceException(e);
                    }
                   
                } else if (resourceType.equalsIgnoreCase(EC2)) {
                    if (targetType.length() > 0) {
                        targetType.append(",").append(resourceType);
                    } else {
                        targetType.append(resourceType);
                    }
                    try {
                        Map<String, Long> ec2AssetsByApplicationMap = repository
                                .getAssetDetailsByApplication(assetGroup,
                                        resourceType);
                        Map<String, Long> ec2UnPatchedCountByApplicationMap = patchingRepository
                                .getNonCompliantNumberForAgAndResourceType(
                                        assetGroup, resourceType);

                        List<Map<String, Object>> appsDetails = repository.getAppsDetails("Cloud");
                        if (!appsDetails.isEmpty()) {
                            Long ec2TotalAssets = complianceService.getPatching(
                                    assetGroup, resourceType, null).get(TOTAL_INSTANCES);
                            totalAssets += ec2TotalAssets;
                            executorsListMap = getDirectorsOrExecutorsPatchingProgress(
                                    EXCUTIVE_SPONSOR, quarterScope, resourceType,
                                    ec2AssetsByApplicationMap, appsDetails,
                                    ec2UnPatchedCountByApplicationMap,
                                    executorsListMap,
                                    patchingProgressByExecutorsList);
                        }
                    } catch (DataException e) {
                    	logger.error("Error @ patchProgByExSponsor", e);
                        throw new ServiceException(e);
                    }
                    
                }

            }

            for (Map.Entry<String, Map<String, Object>> entry : executorsListMap
                    .entrySet()) {
                Map<String, Object> executorsMap = entry.getValue();
                if (null != executorsMap) {
                    patchingProgressByExecutorsList.add(executorsMap);
                }
            }

            Comparator<Map<String, Object>> comp = (m1, m2) -> Integer.compare(
                    new Integer(m2.get(quarterScope).toString()), new Integer(
                            m1.get(quarterScope).toString()));
            Collections.sort(patchingProgressByExecutorsList, comp);
            return new PatchingProgressResponse(assetGroup,
                    targetType.toString(), totalAssets, year, quarter,
                    patchingProgressByExecutorsList);
        } else {
            throw new ServiceException(
                    NOT_ELIGIBLE_PROJECTIONS);
        }

    }

    /**
     * Gets the directors or executors patching progress.
     *
     * @param type the type
     * @param quarterScope the quarter scope
     * @param resourceType the resource type
     * @param assetsByApplicationMap the assets by application map
     * @param appsDetails the apps details
     * @param unPatchedCountByApplicationMap the un patched count by application map
     * @param directorOrExeceutorListMap the director or execeutor list map
     * @param patchingProgressByDirectorList the patching progress by director list
     * @return the directors or executors patching progress
     */
    @SuppressWarnings("unused")
    private Map<String, Map<String, Object>> getDirectorsOrExecutorsPatchingProgress(
            String type, String quarterScope, String resourceType,
            Map<String, Long> assetsByApplicationMap,
            List<Map<String, Object>> appsDetails,
            Map<String, Long> unPatchedCountByApplicationMap,
            Map<String, Map<String, Object>> directorOrExeceutorListMap,
            List<Map<String, Object>> patchingProgressByDirectorList) {
        Map<String, Object> applicationByDirectorOrExecutor = appsDetails
                .parallelStream()
                .filter(apps -> apps.get(type) != null)
                .collect(
                        Collectors.toMap(apps -> apps.get("appTag").toString(),
                                apps -> apps.get(type),
                                (oldValue, newValue) -> newValue));
        for (Entry<String, Long> assetDetails : assetsByApplicationMap
                .entrySet()) {

            Map<String, Object> patchingProgressByDirectorOrExecutor = new HashMap<>();
            Long unPatched = 0l;
            long assetCount = 0l;
            long patched = 0l;
            String name;
            double patchPercentage = 0.0D;
            if (assetDetails.getKey() != null
                    && !"".equals(assetDetails.getKey())) {
                if (null != applicationByDirectorOrExecutor.get(assetDetails
                        .getKey())
                        && !("".equals(applicationByDirectorOrExecutor
                                .get(assetDetails.getKey())))) {
                    name = applicationByDirectorOrExecutor.get(
                            assetDetails.getKey()).toString();
                } else {
                    name = "unknown";
                }
                // assetCount
                if (null != assetDetails.getValue()) {
                    assetCount = assetDetails.getValue();
                }
                // unpatchedCount
                if (null != unPatchedCountByApplicationMap.get(assetDetails
                        .getKey())) {
                    unPatched = unPatchedCountByApplicationMap.get(assetDetails
                            .getKey());
                }
                if (!directorOrExeceutorListMap.isEmpty()
                        && null != directorOrExeceutorListMap.get(name)) {
                    Map<String, Object> exisitngPatProgByDir;
                    exisitngPatProgByDir = directorOrExeceutorListMap.get(name);

                    assetCount += Long.parseLong(exisitngPatProgByDir.get(
                            quarterScope).toString());
                    unPatched += Long.parseLong(exisitngPatProgByDir.get(
                            "unpatched").toString());
                }
                if (unPatched > assetCount) {
                    unPatched = assetCount;
                }
                if (assetCount > 0 && assetCount >= unPatched) {
                    patched = assetCount - unPatched;
                    patchPercentage = (patched) * HUNDRED / (assetCount);
                    patchPercentage = Math.floor(patchPercentage);
                }
                if (DIRECTOR.equals(type)) {
                    patchingProgressByDirectorOrExecutor.put(DIRECTOR, name);
                } else {
                    patchingProgressByDirectorOrExecutor.put(EXCUTIVE_SPONSOR,
                            name);
                }
                patchingProgressByDirectorOrExecutor.put(quarterScope,
                        assetCount);
                patchingProgressByDirectorOrExecutor.put("patched", patched);
                patchingProgressByDirectorOrExecutor.put("%patched",
                        patchPercentage);
                patchingProgressByDirectorOrExecutor
                        .put("unpatched", unPatched);
                if (null != patchingProgressByDirectorOrExecutor) {
                    directorOrExeceutorListMap.put(name,
                            patchingProgressByDirectorOrExecutor);
                }
            }

        }
        return directorOrExeceutorListMap;

    }

}
