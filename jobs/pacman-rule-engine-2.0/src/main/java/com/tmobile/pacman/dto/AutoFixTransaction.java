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
package com.tmobile.pacman.dto;

import java.util.Map;

import com.tmobile.pacman.common.AutoFixAction;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.util.CommonUtils;

public class AutoFixTransaction {
	
	 /** The allocation Id. */
   private String allocationId;
   
   /** The parent doc id. */
   private String parentDocId;
   
   public String getParentDocId() {
	return parentDocId;
}

public void setParentDocId(String parentDocId) {
	this.parentDocId = parentDocId;
}


private String attachedSg;
   private String detachedSg;
	
	 public String getAttachedSg() {
		return attachedSg;
	}

	public void setAttachedSg(String attachedSg) {
		this.attachedSg = attachedSg;
	}

	public String getDetachedSg() {
		return detachedSg;
	}

	public void setDetachedSg(String detachedSg) {
		this.detachedSg = detachedSg;
	}

	public String getAllocationId() {
		return allocationId;
	}

	public void setAllocationId(String allocationId) {
		this.allocationId = allocationId;
	}


	/** The group name. */
   private String groupName;

   /** The transation time. */
   private String transationTime;
   
   /** The action. */
   private AutoFixAction action;
   
   /** The resource id. */
   private String resourceId;
   
   /** The execution id. */
   private String executionId;
   
   /** The transaction id. */
   private String transactionId;
   
   /** The desc. */
   private String desc;
   
   /**  ruleId. */
   private String ruleId;
   
   /** The account id. */
   private String accountId;
   
   /** The region. */
   private String region;
   
   /** The application tag. */
   private String applicationTag;
   
   /** The resource type */
   private String type;
   
   /** The issue Id. */
   private String issueId;
   
   /** additional information about this transaction */
   private String additionalInfo;
   
   /** inline policy*/
   private String inlinePolicy;
   
   public String getInlinePolicy() {
		return inlinePolicy;
	}

	public void setInlinePolicy(String inlinePolicy) {
		this.inlinePolicy = inlinePolicy;
	}

	public String getManagedPolicy() {
		return managedPolicy;
	}

	public void setManagedPolicy(String managedPolicy) {
		this.managedPolicy = managedPolicy;
	}


	/** managed policy*/
   private String managedPolicy;
   
   /** Elasticsearch policy*/
   private String policy;
   
   
   
   public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	/**
    * Instantiates a new auto fix transaction.
    *
    * @param resourceId the resource id
    * @param ruleId the rule id
    * @param accountId the account id
    * @param region the region
    * @param applicationTag the application tag
    */
   public AutoFixTransaction(String resourceId,
           String ruleId, String accountId, String region,String applicationTag,String type,String issueId) {
       super();
       this.resourceId = resourceId;
       this.ruleId = ruleId;
       this.accountId = accountId;
       this.region = region;
       this.applicationTag = applicationTag;
       this.type=type;
       this.issueId=issueId;
   }

   /**
    * Gets the account id.
    *
    * @return the account id
    */
   public String getAccountId() {
       return accountId;
   }

   /**
    * Sets the account id.
    *
    * @param accountId the new account id
    */
   public void setAccountId(String accountId) {
       this.accountId = accountId;
   }

   /**
    * Gets the region.
    *
    * @return the region
    */
   public String getRegion() {
       return region;
   }

   /**
    * Sets the region.
    *
    * @param region the new region
    */
   public void setRegion(String region) {
       this.region = region;
   }

   /**
    * Gets the application tag.
    *
    * @return the application tag
    */
   public String getApplicationTag() {
       return applicationTag;
   }

   /**
    * Sets the application tag.
    *
    * @param applicationTag the new application tag
    */
   public void setApplicationTag(String applicationTag) {
       this.applicationTag = applicationTag;
   }

   /**
    * Instantiates a new auto fix transaction.
    */
   public AutoFixTransaction() {
   }

   /**
    * 
    * @param action
    * @param resourceId
    * @param ruleId
    * @param executionId
    * @param transactionId
    * @param desc
    * @param type
    * @param targetType
    * @param issueId
    * @param accountId
    * @param region
    */
   public AutoFixTransaction(AutoFixAction action, String resourceId, String ruleId, String executionId, String transactionId,
           String desc,String type,String targetType,String issueId,String accountId,String region,String parentDocId) {
       super();
       this.transationTime = CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
               PacmanSdkConstants.DATE_FORMAT);
       this.action = action;
       this.resourceId = resourceId;
       this.ruleId=ruleId;
       this.executionId = executionId;
       this.transactionId = transactionId;
       this.desc = desc;
       this.type=type;
       this.targetType=targetType;
       this.issueId=issueId;
       this.accountId=accountId;
       this.region=region;
       this.parentDocId=parentDocId;
   }
   
   /**
    * 
    * @param action
    * @param resourceId
    * @param ruleId
    * @param executionId
    * @param transactionId
    * @param desc
    * @param type
    * @param targetType
    * @param issueId
    */
   public AutoFixTransaction(AutoFixAction action, String resourceId, String ruleId, String executionId, String transactionId,
           String desc,String type,String targetType,String issueId) {
       super();
       this.transationTime = CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
               PacmanSdkConstants.DATE_FORMAT);
       this.action = action;
       this.resourceId = resourceId;
       this.ruleId=ruleId;
       this.executionId = executionId;
       this.transactionId = transactionId;
       this.desc = desc;
       this.type=type;
       this.targetType=targetType;
       this.issueId=issueId;
   }

   /**
    * Gets the transation time.
    *
    * @return the transation time
    */
   public String getTransationTime() {
       return transationTime;
   }

   /**
    * Sets the transation time.
    *
    * @param transationTime the new transation time
    */
   public void setTransationTime(String transationTime) {
       this.transationTime = transationTime;
   }

   /**
    * Gets the action.
    *
    * @return the action
    */
   public AutoFixAction getAction() {
       return action;
   }

   /**
    * Sets the action.
    *
    * @param action the new action
    */
   public void setAction(AutoFixAction action) {
       this.action = action;
   }

   /**
    * Gets the resource id.
    *
    * @return the resource id
    */
   public String getResourceId() {
       return resourceId;
   }

   /**
    * Sets the resource id.
    *
    * @param resourceId the new resource id
    */
   public void setResourceId(String resourceId) {
       this.resourceId = resourceId;
   }

   /**
    * Gets the execution id.
    *
    * @return the execution id
    */
   public String getExecutionId() {
       return executionId;
   }

   /**
    * Sets the execution id.
    *
    * @param executionId the new execution id
    */
   public void setExecutionId(String executionId) {
       this.executionId = executionId;
   }

   /**
    * Gets the transaction id.
    *
    * @return the transaction id
    */
   public String getTransactionId() {
       return transactionId;
   }

   /**
    * Sets the transaction id.
    *
    * @param transactionId the new transaction id
    */
   public void setTransactionId(String transactionId) {
       this.transactionId = transactionId;
   }

   /**
    * Gets the desc.
    *
    * @return the desc
    */
   public String getDesc() {
       return desc;
   }

   /**
    * Sets the desc.
    *
    * @param desc the new desc
    */
   public void setDesc(String desc) {
       this.desc = desc;
   }

   /**
    * Gets the rule id.
    *
    * @return the rule id
    */
   public String getRuleId() {
       return ruleId;
   }

   /**
    * Sets the rule id.
    *
    * @param ruleId the new rule id
    */
   public void setRuleId(String ruleId) {
       this.ruleId = ruleId;
   }
   
   
   /**
    * Instantiates a new auto fix transaction.
    *
    * @param resourceId the resource id
    * @param ruleId the rule id
    * @param accountId the account id
    * @param region the region
    * 
    */
   public AutoFixTransaction(String resourceId,
           String ruleId, String accountId, String region) {
       super();
       this.resourceId = resourceId;
       this.ruleId = ruleId;
       this.accountId = accountId;
       this.region = region;
   }

   public String getType() {
       return type;
   }

   public void setType(String type) {
       this.type = type;
   }

   public String getIssueId() {
       return issueId;
   }

   public void setIssueId(String issueId) {
       this.issueId = issueId;
   }

   public String getAdditionalInfo() {
       return additionalInfo;
   }

   public void setAdditionalInfo(String additionalInfo) {
       this.additionalInfo = additionalInfo;
   }
   
   
   public AutoFixTransaction(AutoFixAction action,Map<String,String> transactionParams) {
       super();
       
       if(null!=action){
       this.action = action;
       }
       for(Map.Entry<String, String> str:transactionParams.entrySet()){
			if ("ruleId".equals(str.getKey()) && null!=str.getValue()) {
				this.ruleId = str.getValue();
			}else if("resourceId".equals(str.getKey()) && null!=str.getValue()){
				this.resourceId = str.getValue();
			}else if("accountId".equals(str.getKey())  && null!=str.getValue()){
				this.accountId = str.getValue();
			}else if("region".equals(str.getKey()) && null!=str.getValue()){
				this.region = str.getValue();
			}else if("applicationTag".equals(str.getKey())  && null!=str.getValue()){
				this.applicationTag = str.getValue();
			}else if("transationTime".equals(str.getKey()) && null!=str.getValue()){
			this.transationTime = CommonUtils.getCurrentDateStringWithFormat(PacmanSdkConstants.PAC_TIME_ZONE,
	                PacmanSdkConstants.DATE_FORMAT);
			}else if("transactionId".equals(str.getKey()) && null!=str.getValue()){
				this.transactionId = str.getValue();
			}else if("desc".equals(str.getKey()) && null!=str.getValue()){
				this.desc = str.getValue();
			}else if("additionalInfo".equals(str.getKey()) && null!=str.getValue()){
				this.additionalInfo = str.getValue();
			}else if("issueId".equals(str.getKey()) && null!=str.getValue()){
				this.issueId = str.getValue();
			}else if("type".equals(str.getKey()) && null!=str.getValue()){
				this.type = str.getValue();
			}else if("inlinePolicy".equals(str.getKey()) && null!=str.getValue()){
				this.inlinePolicy = str.getValue();
			}else if("managedPolicy".equals(str.getKey()) && null!=str.getValue()){
				this.managedPolicy = str.getValue();
			}else if("executionId".equals(str.getKey()) && null!=str.getValue()){
				this.executionId = str.getValue();
			}else if("groupName".equals(str.getKey()) && null!=str.getValue()){
				this.groupName = str.getValue();
			}else if("allocationId".equals(str.getKey()) && null!=str.getValue()){
				this.allocationId = str.getValue();
			}else if("attachedSg".equals(str.getKey()) && null!=str.getValue()){
				this.attachedSg = str.getValue();
			}else if("detachedSg".equals(str.getKey()) && null!=str.getValue()){
				this.detachedSg = str.getValue();
			}else if("targetType".equals(str.getKey()) && null!=str.getValue()){
				this.targetType = str.getValue();
			}else if("policy".equals(str.getKey()) && null!=str.getValue()){
				this.policy = str.getValue();
			}
			
			
       }
   }

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	/** The targetType  */
   private String targetType;

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

}
