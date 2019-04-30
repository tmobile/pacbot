package com.tmobile.pacman.api.admin.repository.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cloudwatchevents.model.DisableRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.EnableRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.ListRulesRequest;
import com.amazonaws.services.cloudwatchevents.model.RuleState;
import com.tmobile.pacman.api.admin.config.PacmanConfiguration;
import com.tmobile.pacman.api.admin.repository.JobExecutionManagerRepository;
import com.tmobile.pacman.api.admin.repository.RuleRepository;
import com.tmobile.pacman.api.admin.repository.model.Rule;
import com.tmobile.pacman.api.admin.service.AmazonClientBuilderService;

@Service
public class AdminService {
	
	private static final Logger log = LoggerFactory.getLogger(AdminService.class);
	
	@Autowired
	private RuleRepository ruleRepository;
	
	@Autowired
	private JobExecutionManagerRepository jobRepository;
	
	@Autowired
	private AmazonClientBuilderService amazonClient;
	
	@Autowired
	private PacmanConfiguration config;
	
	public void shutDownAlloperations(String operation, String job) {
		if(operation.equals("enable")) {
			if(job.equals("rule")) {
				enableRules();
			} else if(job.equals("job")) {
				enableJobs();
			} else {
				enableRules();
				enableJobs();
			}
			
		} else {
			if(job.equals("rule")) {
				disableRules();
			} else if(job.equals("job")) {
				disableJobs();
			} else {
				disableRules();
				disableJobs();
			}
		}
	}
	
	private boolean disableRules() {
		List<Rule> ruleIds = ruleRepository.findAll();
		List<String> rules = amazonClient.getAmazonCloudWatchEvents(config.getRule().getLambda().getRegion())
			.listRules(new ListRulesRequest()).getRules().parallelStream().map(rule->rule.getName()).collect(Collectors.toList());
		try {
			for(Rule rule : ruleIds) {
				if(rules.contains(rule.getRuleUUID())) {
					amazonClient.getAmazonCloudWatchEvents(config.getRule().getLambda().getRegion())
						.disableRule(new DisableRuleRequest().withName(rule.getRuleUUID()));
					rule.setStatus(RuleState.DISABLED.name());
					ruleRepository.save(rule);
				}
			}
			return true;
		} catch(Exception e) {
			log.error("Error in disable rules",e);
			return false;
		}
		
	}
	
	private boolean disableJobs() {
		Collection<String> jobIds = jobRepository.getAllJobIds();
		System.out.println(jobIds);
		return true;
	}
	
	private boolean enableRules() {
		List<Rule> ruleIds = ruleRepository.findAll();
		List<String> rules = amazonClient.getAmazonCloudWatchEvents(config.getRule().getLambda().getRegion())
				.listRules(new ListRulesRequest()).getRules().parallelStream().map(rule->rule.getName()).collect(Collectors.toList());
		try {
			for(Rule rule : ruleIds) {
				if(rules.contains(rule.getRuleUUID())) {
					amazonClient.getAmazonCloudWatchEvents(config.getRule().getLambda().getRegion())
							.enableRule(new EnableRuleRequest().withName(rule.getRuleUUID()));
					rule.setStatus(RuleState.ENABLED.name());
					ruleRepository.save(rule);
				}
			}
			return true;
		} catch(Exception e) {
			log.error("Error in enable rules",e);
			return false;
		}
	}
	
	private boolean enableJobs() {
		
		return true;
	}

}
