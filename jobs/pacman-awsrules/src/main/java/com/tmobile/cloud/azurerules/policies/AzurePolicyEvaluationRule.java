package com.tmobile.cloud.azurerules.policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * Possible network Just In Time (JIT) access will be monitored by Azure
 * Security Center as recommendations
 */

@PacmanRule(key = "check-for-azure-policy-evaluation-results", desc = "Azure policy evaluation results for different target types", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class AzurePolicyEvaluationRule extends BaseRule {

	private static final Logger logger = LoggerFactory.getLogger(AzurePolicyEvaluationRule.class);

	
	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("======== Azure Policy Evaluation Rule started =========");

		MDC.put("executionId", ruleParam.get("executionId"));
		MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID));

		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
		String category = ruleParam.get(PacmanRuleConstants.CATEGORY);

		String resourceId = resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID).toLowerCase();
		String pacmanHost = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
		String policyDefinitionName = ruleParam.get("policyDefinitionName");
		String azurePolicyEvaluationResultsURl = ruleParam.get("azurePolicyEvaluationResults");

		Map<String, Object> policyEvaluationResultsMap = new HashMap<>();
		try {

			policyEvaluationResultsMap = PacmanUtils.getAzurePolicyEvaluationResults(
					pacmanHost + azurePolicyEvaluationResultsURl, resourceId, policyDefinitionName);
			if (!policyEvaluationResultsMap.isEmpty()) {
				boolean isCompliant = (boolean) policyEvaluationResultsMap.get("isCompliant");
				if (!isCompliant == true) {
					List<LinkedHashMap<String, Object>> issueList = new ArrayList<>();
					LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
					Annotation annotation = null;
					annotation = Annotation.buildAnnotation(ruleParam, Annotation.Type.ISSUE);
					annotation.put(PacmanSdkConstants.DESCRIPTION,
							policyEvaluationResultsMap.get("policyDescription").toString());
					annotation.put(PacmanRuleConstants.SEVERITY, severity);
					annotation.put(PacmanRuleConstants.CATEGORY, category);
					annotation.put(PacmanRuleConstants.AZURE_SUBSCRIPTION, resourceAttributes.get(PacmanRuleConstants.AZURE_SUBSCRIPTION));
					annotation.put(PacmanRuleConstants.AZURE_SUBSCRIPTION_NAME, resourceAttributes.get(PacmanRuleConstants.AZURE_SUBSCRIPTION_NAME));
					issue.put("resourceId", resourceId);
					issue.put("policyDescription", policyEvaluationResultsMap.get("policyDescription").toString());
					issue.put("policyName", policyEvaluationResultsMap.get("policyName").toString());
					issueList.add(issue);
					annotation.put(PacmanRuleConstants.ISSUE_DETAILS, issueList.toString());
					logger.debug(
							"======== Azure Policy Evaluation Rule ended with annotation {} : =========",
							annotation);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE,
							annotation);

				}
			}

		} catch (Exception exception) {
			logger.error("error: ", exception);
			throw new RuleExecutionFailedExeption(exception.getMessage());
		}

		logger.debug("======== Azure Policy Evaluation Rule ended=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	@Override
	public String getHelpText() {
		return "Azure Policy Evaluation Rule ";
	}

}
