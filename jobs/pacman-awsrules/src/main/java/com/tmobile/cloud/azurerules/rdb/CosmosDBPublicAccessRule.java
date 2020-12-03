package com.tmobile.cloud.azurerules.rdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tmobile.cloud.awsrules.utils.PacmanUtils;
import com.tmobile.cloud.azurerules.utils.CommonUtils;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;
import com.tmobile.pacman.commons.exception.RuleExecutionFailedExeption;
import com.tmobile.pacman.commons.rule.Annotation;
import com.tmobile.pacman.commons.rule.BaseRule;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;
import com.tmobile.pacman.executor.RuleExecutor;

/**
 * Azure rule for checking if the cosmos db is open to internet outside the permissible IP range
 */

@PacmanRule(key = "check-for-cosmosdb-public-access", desc = "This rule checks for cosmosdb is publicaly accessible, if yes then it creates an issue", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class CosmosDBPublicAccessRule extends BaseRule {
	private static final String DEFAULT_SEPARATOR = ", ";
	private static final String NON_PERMISSIBLE_IP_RANGES = "NonPermissibleIPRanges";
	private static final String PUBLICLY_ACCESSIBLE_DB_FOUND = "Database with public accessibility firewall rule found";
	private static final Logger logger = LoggerFactory.getLogger(CosmosDBPublicAccessRule.class);

	/**
     * The method will get triggered from Rule Engine with following parameters
     * 
     * @param ruleParam
     * 
     * ************* Following are the Rule Parameters********* <br><br>
     * 
     * ruleKey : check-for-cosmosdb-public-access <br><br>
     * 
     * allowedCidrs : Permissible IP ranges <br><br>
     * 
     * esSubUri : ES index path to retrieve the firewall rules  <br><br>
     * 
     * esFields : ES fields with firewall rules <br><br>
     * 
     * @param resourceAttributes this is a resource in context which needs to be scanned this is provided by execution engine
     *
     */

	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		logger.debug("========CosmosDBPublicAccessRule started=========");

        MDC.put("executionId", ruleParam.get("executionId")); 
        MDC.put("ruleId", ruleParam.get(PacmanSdkConstants.RULE_ID)); 
        
        String resourceId = resourceAttributes.get(PacmanRuleConstants.RESOURCE_ID);
		String region = resourceAttributes.get(PacmanRuleConstants.REGION);
		String subscription = resourceAttributes.get(PacmanRuleConstants.SUBSCRIPTION);
        
		if(!isValidInputParams(ruleParam)) {
			logger.info(PacmanRuleConstants.MISSING_CONFIGURATION);
			throw new InvalidInputException(PacmanRuleConstants.MISSING_CONFIGURATION); 
		}
		
		String fieldNames = ruleParam.get(PacmanRuleConstants.FIREWALL_RULE_ES_FIELDS);
    	String firewallRulesES = ruleParam.get(PacmanRuleConstants.ES_FIREWALL_RULE);
    	String esHostUri = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
    	
    	List<String> fieldNamesList = Arrays.asList(fieldNames.split(","));
		try {
			List<String> allowedCidrList = PacmanUtils.getAllowedCidrsFromConfigProperty(ruleParam);
			JsonArray firewallRules = PacmanUtils.getFirewallRuleDetailsList(esHostUri.concat(firewallRulesES),
					resourceId, region, subscription, fieldNamesList);
			
			List<String> publicRanges = getPublicAccessibleIPRanges(firewallRules, allowedCidrList);
			if(!publicRanges.isEmpty()) {
				Annotation annotation = createAnnotation(ruleParam, resourceAttributes, publicRanges);
		        return new RuleResult(PacmanSdkConstants.STATUS_FAILURE, PacmanRuleConstants.FAILURE_MESSAGE, annotation);
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.debug("error while executing public access rule for resource id {}", resourceId);
			throw new RuleExecutionFailedExeption(exception.getMessage());
		}
		logger.debug("========CosmosDBPublicAccessRule ended successfull=========");
		return new RuleResult(PacmanSdkConstants.STATUS_SUCCESS, PacmanRuleConstants.SUCCESS_MESSAGE);
	}

	/**
	 * validate input params
	 * @param ruleParam
	 * @return
	 */
	private boolean isValidInputParams(Map<String, String> ruleParam) {
		String severity = ruleParam.get(PacmanRuleConstants.SEVERITY);
        String category = ruleParam.get(PacmanRuleConstants.CATEGORY);
        
        String fieldNames = ruleParam.get(PacmanRuleConstants.FIREWALL_RULE_ES_FIELDS);
    	String firewallRulesES = ruleParam.get(PacmanRuleConstants.ES_FIREWALL_RULE);
    	String esHostUri = PacmanUtils.getPacmanHost(PacmanRuleConstants.ES_URI);
    	String allowedCidrConfigProp = ruleParam.get(PacmanSdkConstants.PROP_NAME_ALLOWED_CIDRS);
		return PacmanUtils.doesAllHaveValue(firewallRulesES, fieldNames, esHostUri, severity,
				category, allowedCidrConfigProp);
		
	}

	/**
	 * checks if any of the firewall rules is publicly accessible outside the permissible public IP ranges
	 * @param firewallRules
	 * @param allowedCidrIps
	 * @return 
	 */
	private List<String> getPublicAccessibleIPRanges(JsonArray firewallRules, List<String> allowedCidrIps) {
		List<String> nonPermittedIpRanges = new ArrayList<>();
		if(firewallRules.size()==0) {
			nonPermittedIpRanges.add(PacmanRuleConstants.ALL);
		} else {
			for (JsonElement rule : firewallRules) {
				if (rule.isJsonPrimitive()) {
					String firewallRule = rule.getAsString();
					List<String> ipList = Arrays.asList(firewallRule.split(PacmanSdkConstants.COMMA));
					nonPermittedIpRanges.addAll(getNonPermittedIPs(allowedCidrIps, ipList));
				}
			}
		}
		return nonPermittedIpRanges;
	}

	/**
	 * Get non permitted IPs from given IP list
	 * @param allowedCidrIps
	 * @param ipList
	 * @return
	 */
	private List<String> getNonPermittedIPs(List<String> allowedCidrIps, List<String> ipList) {
		List<String> nonPermittedIpRanges = new ArrayList<>();
		for (String ip : ipList) {
			if (!PacmanUtils.isFirewallRuleInPermissibleRange(ip, null, allowedCidrIps)) {
				nonPermittedIpRanges.add(ip);
			}
		}
		return nonPermittedIpRanges;
	}
	
	/**
	 * create annotation for the given resource
	 * @param ruleParam
	 * @param resourceAttributes
	 * @param publicRanges
	 * @return
	 */
	private Annotation createAnnotation(Map<String, String> ruleParam, Map<String, String> resourceAttributes,
			List<String> publicRanges) {
		LinkedHashMap<String, Object> issue = new LinkedHashMap<>();
        issue.put(PacmanRuleConstants.VIOLATION_REASON, PUBLICLY_ACCESSIBLE_DB_FOUND);
		issue.put(NON_PERMISSIBLE_IP_RANGES, String.join(DEFAULT_SEPARATOR, publicRanges));
		return CommonUtils.buildAnnotationForAzure(ruleParam, resourceAttributes, PUBLICLY_ACCESSIBLE_DB_FOUND, issue, null);
	}	
	
	@Override
	public String getHelpText() {
		return "This rule checks cosmos database has public access";
	}
	
	public static void main(String[] args) {
		new RuleExecutor().main(args);
	}

}
