package com.tmobile.cloud.awsrules.securitycenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.amazonaws.util.StringUtils;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;
import com.tmobile.pacman.commons.utils.CommonUtils;

@PacmanRule(key = "check-for-azure-security-rule", desc = "checks virtualmachines for network access control", severity = PacmanSdkConstants.SEV_HIGH, category = "networking")
public class AzureSecurityCenterRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(AzureSecurityCenterRule.class);
	
	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		
		logger.debug("========AzureSecurityCenterRule started=========");
		String entityId = ruleParam.get(PacmanSdkConstants.RESOURCE_ID);
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
		String targetType = ruleParam.get(PacmanRuleConstants.TARGET_TYPE); // need to specify the index type
		String policyName = ruleParam.get("policyName").replaceAll("@", " ");
		
		MDC.put("executionId", ruleParam.get("executionId")); // this is the logback Mapped Diagnostic Contex
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); // this is the logback Mapped Diagnostic Contex

		if (!PacmanUtils.doesAllHaveValue(severity, category, targetType)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION);
		}

		String esUrl = null;
		String url = CommonUtils.getEnvVariableValue(PacmanSdkConstants.ES_URI_ENV_VAR_NAME);
		if (!StringUtils.isNullOrEmpty(url)) {
			esUrl = url + "/azure_securitycenter/securitycenter/_search";
		}
	
		if (entityId != null && !entityId.isEmpty()) {
			Map<String, Object> securityCenterData = new HashMap<>();
			try {
				Map<String, Object> mustFilter = new HashMap<>();
				mustFilter.put(PacmanUtils.convertAttributetoKeyword(PacmanRuleConstants.POLICYNAME), policyName);
				mustFilter.put(PacmanUtils.convertAttributetoKeyword(PacmanRuleConstants.AZURERESOURCEID), entityId.toLowerCase());
				mustFilter.put(PacmanRuleConstants.LATEST, true);
				securityCenterData = PacmanUtils.checkResourceIdBypolicyName(esUrl, mustFilter);
			} catch (Exception e) {
				logger.error("unable to determine", e);
				throw new RuleExecutionFailedExeption("unable to determine" + e);
			}
			if (!securityCenterData.isEmpty()) {
				List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
		        LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
				Annotation annotation = null;
				annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
				annotation.put(PacmanSdkConstants.DESCRIPTION, policyName);
				annotation.put(PacmanRuleConstants.SEVERITY, severity);
				annotation.put(PacmanRuleConstants.CATEGORY, category);
				issue.put(PacmanRuleConstants.VIOLATION_REASON, policyName+" Found!");
				issueList.add(issue);
				annotation.put(PacmanRuleConstants.ISSUE_DETAILS, issueList.toString());
				logger.debug("========AzureSecurityCenterRule ended with annotation {} : =========", annotation);
				return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,	annotation);
			}
		}
		logger.debug("========AzureSecurityCenterRule Completed==========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
	}
	@Override
	public String getHelpText() {
		return "This rule checks the security center rules";
	}

}
