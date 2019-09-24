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

package com.tmobile.pacman.autofix.s3;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.SetPublicAccessBlockRequest;
import com.amazonaws.util.CollectionUtils;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.tmobile.pacman.common.PacmanSdkConstants;
import com.tmobile.pacman.common.exception.AutoFixException;
import com.tmobile.pacman.common.exception.RuleEngineRunTimeException;
import com.tmobile.pacman.commons.autofix.BaseFix;
import com.tmobile.pacman.commons.autofix.FixResult;
import com.tmobile.pacman.commons.autofix.PacmanFix;

// TODO: Auto-generated Javadoc
/**
 * The Class S3GlobalAccessAutoFix.
 */
@PacmanFix(key = "s3-global-access-fix", desc = "fixes the global access issue")
public class S3GlobalAccessAutoFix extends BaseFix {

    /** The Constant BUCKET_ACL. */
    private static final String BUCKET_ACL = "bucketACL";
    
    /** The Constant BUCKET_POLICY. */
    private static final String BUCKET_POLICY = "bucketPolicy";
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(S3GlobalAccessAutoFix.class);

    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.BaseFix#executeFix(java.util.Map, java.util.Map, java.util.Map)
     */
    @Override
    public FixResult executeFix(Map<String, String> issue, Map<String, Object> clientMap,
            Map<String, String> ruleParams) {

        AmazonS3Client awsS3Client = null;
        awsS3Client = (AmazonS3Client) clientMap.get(PacmanSdkConstants.CLIENT);
        String s3BucketName = issue.get(PacmanSdkConstants.RESOURCE_ID);
        try{
        	LOGGER.info("block all public permissions");
        	blockAllPublicAcces(awsS3Client, s3BucketName);
        }catch(Exception e){
        	LOGGER.debug("Error while blocking all public permissions {} ",e);
        	LOGGER.info("revoking all ACL permissions");
            revokeACLPublicPermission(awsS3Client, s3BucketName);
            LOGGER.info("revking all Bucket Policy");
            revokePublicBucketPolicy(awsS3Client, s3BucketName);
        }
        return new FixResult(PacmanSdkConstants.STATUS_SUCCESS_CODE, "the s3 bucket is now fixed");
    }

    /* (non-Javadoc)
     * @see com.tmobile.pacman.commons.autofix.BaseFix#backupExistingConfigForResource(java.lang.String, java.lang.String, java.util.Map, java.util.Map)
     */
    @Override
    public boolean backupExistingConfigForResource(final String resourceId, final String resourceType,
            Map<String, Object> clientMap, Map<String, String> ruleParams,Map<String, String> issue) throws AutoFixException {
        LOGGER.debug(String.format("backing up the config for %s" , resourceId));
        AmazonS3 client = (AmazonS3) clientMap.get("client");
        Gson gson = new Gson();
        AccessControlList bucketAcl = client.getBucketAcl(resourceId);
        List<Grant> grants = bucketAcl.getGrantsAsList();
        String oldConfig = gson.toJson(grants);
        backupOldConfig(resourceId, BUCKET_ACL, oldConfig);
        BucketPolicy bucketPolicy = client.getBucketPolicy(resourceId);
        if (!Strings.isNullOrEmpty(bucketPolicy.getPolicyText())) {
            backupOldConfig(resourceId, BUCKET_POLICY, bucketPolicy.getPolicyText());
        }
        LOGGER.debug("backup complete for " + resourceId);
        return true;
    }

    /**
     * revokes all ACL permissions.
     *
     * @param awsS3Client the aws S 3 client
     * @param s3BucketName the s 3 bucket name
     */
    private void revokeACLPublicPermission(AmazonS3Client awsS3Client, String s3BucketName) {
        AccessControlList bucketAcl;
        try {
            bucketAcl = awsS3Client.getBucketAcl(s3BucketName);
            List<Grant> grants = bucketAcl.getGrantsAsList();
            if (!CollectionUtils.isNullOrEmpty(grants)) {
                for (Grant grant : grants) {
                    if ((PacmanSdkConstants.ANY_S3_AUTHENTICATED_USER_URI
                            .equalsIgnoreCase(grant.getGrantee().getIdentifier())
                            || PacmanSdkConstants.ALL_S3_USER_URI.equalsIgnoreCase(grant.getGrantee().getIdentifier()))

                            &&

                            (grant.getPermission().toString().equalsIgnoreCase(PacmanSdkConstants.READ_ACCESS) || (grant
                                    .getPermission().toString().equalsIgnoreCase(PacmanSdkConstants.WRITE_ACCESS)
                                    || (grant.getPermission().toString()
                                            .equalsIgnoreCase(PacmanSdkConstants.READ_ACP_ACCESS)
                                            || (grant.getPermission().toString()
                                                    .equalsIgnoreCase(PacmanSdkConstants.WRITE_ACP_ACCESS)
                                                    || grant.getPermission().toString()
                                                            .equalsIgnoreCase(PacmanSdkConstants.FULL_CONTROL)))))) {
                        bucketAcl.revokeAllPermissions(grant.getGrantee());
                    }
                }
                awsS3Client.setBucketAcl(s3BucketName, bucketAcl);
            }

        } catch (AmazonS3Exception s3Exception) {
            LOGGER.error(String.format("AmazonS3Exception in revokeACLPublicPermission: %s", s3Exception.getMessage()));
            throw new RuleEngineRunTimeException(s3Exception);
        }
    }

    /**
     * Revoke public bucket policy.
     *
     * @param awsS3Client the aws S 3 client
     * @param s3BucketName the s 3 bucket name
     */
    private void revokePublicBucketPolicy(AmazonS3Client awsS3Client, String s3BucketName) {
        BucketPolicy bucketPolicy = awsS3Client.getBucketPolicy(s3BucketName);
        if (bucketPolicy.getPolicyText() != null && !bucketPolicy.getPolicyText().equals(PacmanSdkConstants.EMPTY)) {
            awsS3Client.deleteBucketPolicy(s3BucketName);
        }
    }
    
    private void blockAllPublicAcces(AmazonS3Client awsS3Client, String s3BucketName) {
    	Boolean globalFlag = Boolean.parseBoolean(PacmanSdkConstants.BOOLEAN_TRUE);
         
 		PublicAccessBlockConfiguration accessBlockConfiguration = new PublicAccessBlockConfiguration();
 		accessBlockConfiguration.setBlockPublicAcls(globalFlag);
 		accessBlockConfiguration.setBlockPublicPolicy(globalFlag);
 		accessBlockConfiguration.setIgnorePublicAcls(globalFlag);
 		accessBlockConfiguration.setRestrictPublicBuckets(globalFlag);
 		SetPublicAccessBlockRequest setPublicAccessBlockRequest = new SetPublicAccessBlockRequest();
 		setPublicAccessBlockRequest.setBucketName(s3BucketName);
 		setPublicAccessBlockRequest.setPublicAccessBlockConfiguration(accessBlockConfiguration);
 		
 		awsS3Client.setPublicAccessBlock(setPublicAccessBlockRequest);
    }
}
