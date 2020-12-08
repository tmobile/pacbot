package com.tmobile.cloud.azurerules.publicaccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.JsonArray;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.azurerules.utils.CommonUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.RuleResult;

public abstract class FirewallRulePublicAccess extends BaseRule {
	private static final String DEFAULT_SEPARATOR = "; ";
	
	private static final String SELECTED_NETWORK = "Selected Network";
	private static final String ALL_NETWORK = "All Network";
	private static final String ALLOW_ACCESS = "Allow Access";
	private static final String NON_PERMISSIBLE_IP_RANGES = "Non Permissible IP Ranges";
	
	private static final Logger logger = LoggerFactory.getLogger(FirewallRulePublicAccess.class);
	
	/**
	 * The method will get triggered from Rule Engine with following parameters
	 * 
	 * @param ruleParam
	 * 
	 * @param resourceAttributes this is the resource in context which needs to be
	 *                           scanned this is provided by execution engine
	 *
	 */
	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		
		MDC.put(PacmanSdkConstants.EXECUTION_ID, ruleParam.get(PacmanSdkConstants.EXECUTION_ID));
		MDC.put(PacmanSdkConstants.RULE_ID, ruleParam.get(PacmanSdkConstants.RULE_ID));
		
		if(!isValidInputParams(ruleParam)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION); 
		}
		
		String fieldNames = ruleParam.get(PacmanRuleConstants.FIREWALL_RULE_ES_FIELDS);
    	String firewallRulesES = ruleParam.get(PacmanRuleConstants.ES_FIREWALL_RULE);
    	String esHostUri = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
    	
        String resourceId = resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID);
		String region = resourceAttributes.get(PacmanRuleConstants.REGION);
		String subscription = resourceAttributes.get(PacmanRuleConstants.SUBSCRIPTION);
		
		List<String> fieldNamesList = Arrays.asList(fieldNames.split(PacmanSdkConstants.COMMA));
		try {
			if (eligibleForFirewallCheck(ruleParam, resourceAttributes)) {
				
				List<String> publicRanges = new ArrayList<>();
				JsonArray firewallRulesJson = PacmanUtils.getFirewallRuleDetailsList(
						esHostUri.concat(firewallRulesES), resourceId, region, subscription, fieldNamesList);
				boolean openToAllNtw = isOpenToAllNetworks(resourceAttributes,firewallRulesJson);
				if (openToAllNtw) {
					publicRanges.add(PacmanRuleConstants.ALL);
				} else {
					List<String> allowedCidrList = PacmanUtils.getAllowedCidrsFromConfigProperty(ruleParam);
					List<FirewallRuleDTO> firewallRules = getFirewallRuleDTOs(firewallRulesJson);
					publicRanges = getPublicAccessibleIpRanges(firewallRules, allowedCidrList);
				}
				if(!publicRanges.isEmpty()) {
					Annotation annotation = createAnnotation(ruleParam, resourceAttributes, publicRanges, openToAllNtw);
					return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, annotation);
				}
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.debug("error while executing public access rule for resource id {}", resourceId);
			throw new RuleExecutionFailedExeption(exception.getMessage());
		}
		logger.debug("========Firewall PublicAcessRule ended successfull=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
	}


	/**
	 * Check whether the resource has networking enabled
	 * @param resourceAttributes
	 * @return
	 */
	public abstract boolean eligibleForFirewallCheck(Map<String, String> ruleParam, Map<String, String> resourceAttributes) throws Exception;
	

	/**
	 * Check whether the resource is open to all networks
	 * @param resourceAttributes
	 * @return
	 */
	public abstract boolean isOpenToAllNetworks(Map<String, String> resourceAttributes, JsonArray firewallRulesJson);



	/**
	 * format the Json objects to return a list of FW rule VOs
	 * 
	 * @param firewallRulesES
	 * @param fieldNamesList
	 * @param resourceAttributes
	 * @return
	 * @throws Exception
	 */
	public abstract List<FirewallRuleDTO> getFirewallRuleDTOs(JsonArray firewallRulesJson);

	
	/**
	 * checks if any of the firewall rules is publicly accessible outside the permissible public IP ranges
	 * @param firewallRules
	 * @param allowedCidrIps
	 * @return 
	 */
	public List<String> getPublicAccessibleIpRanges(List<FirewallRuleDTO> firewallRules, List<String> allowedCidrIps) {
		List<String> publicIpRanges = new ArrayList<>();
		for (FirewallRuleDTO rule : firewallRules) {
			String startIP= rule.getStartIP();
			String endIP = rule.getEndIP();
			if(!PacmanUtils.isFirewallRuleInPermissibleRange(startIP, endIP, allowedCidrIps)) {
				String publicIP = endIP==null?startIP:(startIP+PacmanSdkConstants.HYPHEN+endIP);
				publicIpRanges.add(publicIP);
			}
		}
		return publicIpRanges;
	}
	
	/**
	 * Get the array of firewall rules from given ES index and fields
	 * @param firewallRulesES
	 * @param fieldNamesList
	 * @param resourceAttributes
	 * @return firewall rules from ES
	 * @throws Exception
	 */
	public JsonArray getFirewallDetailsFromES(String firewallRulesES, List<String> fieldNamesList,
			Map<String, String> resourceAttributes) throws Exception {
		String esHostUri = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
    	
        String resourceId = resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID);
		String region = resourceAttributes.get(PacmanRuleConstants.REGION);
		String subscription = resourceAttributes.get(PacmanRuleConstants.SUBSCRIPTION);

		return PacmanUtils.getFirewallRuleDetailsList(
				esHostUri.concat(firewallRulesES), resourceId, region, subscription, fieldNamesList);
		
	}

	/**
	 * validate input params
	 * @param ruleParam
	 * @return
	 */
	public boolean isValidInputParams(Map<String, String> ruleParam) {
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        
        String fieldNames = ruleParam.get(PacmanRuleConstants.FIREWALL_RULE_ES_FIELDS);
    	String firewallRulesES = ruleParam.get(PacmanRuleConstants.ES_FIREWALL_RULE);
    	String esHostUri = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
    	
    	String allowedCidrConfigProp = ruleParam.get(PacmanSdkConstants.PROP_NAME_ALLOWED_CIDRS);
		
		return PacmanUtils.doesAllHaveValue(firewallRulesES, fieldNames, esHostUri, allowedCidrConfigProp, severity, category);
	}

	/**
	 * create annotation for the given resource
	 * @param ruleParam
	 * @param resourceAttributes
	 * @param publicRanges
	 * @return
	 */
	public Annotation createAnnotation(Map<String, String> ruleParam, Map<String, String> resourceAttributes,
			List<String> publicRanges, boolean openToAllNtw) {
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
		if (openToAllNtw) {
        	issue.put(ALLOW_ACCESS, ALL_NETWORK);
        } else {
        	issue.put(ALLOW_ACCESS, SELECTED_NETWORK);
    		issue.put(NON_PERMISSIBLE_IP_RANGES, String.join(DEFAULT_SEPARATOR, publicRanges));
        }
		
        issue.put(PacmanRuleConstants.VIOLATION_REASON, getViolationReason());
		
		Map<String, String> additionalParams = additionalAnnotationDetails(resourceAttributes);
		
		return CommonUtils.buildAnnotationForAzure(ruleParam, resourceAttributes, getViolationReason(), issue, additionalParams);
	}


	public abstract Map<String, String> additionalAnnotationDetails(Map<String, String> resourceAttributes);
		
	@Override
	public String getHelpText() {
		return "This rule checks whwther an azure resource has public access firewall rule configured";
	}


	public abstract String getViolationReason();
	

}