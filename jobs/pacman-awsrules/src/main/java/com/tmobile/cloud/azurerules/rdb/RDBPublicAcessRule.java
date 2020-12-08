package com.tmobile.cloud.azurerules.rdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tmobile.cloud.azurerules.publicaccess.FirewallRuleDTO;
import com.tmobile.cloud.azurerules.publicaccess.FirewallRulePublicAccess;
import com.tmobile.cloud.constants.PacmanRuleConstants;
import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.rule.PacmanRule;
import com.tmobile.pacman.commons.rule.RuleResult;

/**
 * Azure rule for checking if the following databases - Sqlserver, Sqldatabase,
 * Postgresql, Mysql, MariaDB is open to internet outside the permissible IP
 * range
 */

@PacmanRule(key = "check-for-rdb-public-access", desc = "This rule checks whether an azure database is publicaly accessible, if yes then it creates an issue", severity = PacmanSdkConstants.SEV_HIGH, category = PacmanSdkConstants.SECURITY)
public class RDBPublicAcessRule extends FirewallRulePublicAccess {
		
	private static final String NAME = "name";
	private static final String PUBLICLY_ACCESSIBLE_RESOURCE_FOUND = "Database with public accessibility firewall rule found";
	
	@Override
	public RuleResult execute(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		return super.execute(ruleParam, resourceAttributes);
	}
	
	@Override
	public List<FirewallRuleDTO> getFirewallRuleDTOs(JsonArray firewallRulesJson) {
		List<FirewallRuleDTO> firewallRules = new ArrayList<>();
		for (JsonElement rule : firewallRulesJson) {
			JsonObject firewallRule = rule.getAsJsonObject();
			if (firewallRule.has(PacmanRuleConstants.START_IP_ADDR) &&
					firewallRule.has(PacmanRuleConstants.END_IP_ADDR)) {
				String startIP= firewallRule.get(PacmanRuleConstants.START_IP_ADDR).getAsString();
				String endIP = firewallRule.get(PacmanRuleConstants.END_IP_ADDR).getAsString();
				firewallRules.add(new FirewallRuleDTO(startIP, endIP));
			}
		}
		return firewallRules;
	}
	
	@Override
	public Map<String, String> additionalAnnotationDetails(Map<String, String> resourceAttributes) {
		Map<String, String> additionalParams= new HashMap<>();
		additionalParams.put(PacmanSdkConstants.SERVER_NAME, resourceAttributes.get(NAME));
		return additionalParams;
	}

	@Override
	public boolean isOpenToAllNetworks(Map<String, String> resourceAttributes, JsonArray firewallRulesJson) {
		return false;
	}

	@Override
	public boolean eligibleForFirewallCheck(Map<String, String> ruleParam, Map<String, String> resourceAttributes) {
		return true;
	}

	@Override
	public String getViolationReason() {
		return PUBLICLY_ACCESSIBLE_RESOURCE_FOUND;
	}
	
	
}