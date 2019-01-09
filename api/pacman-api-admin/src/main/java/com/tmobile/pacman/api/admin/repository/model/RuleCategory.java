package com.tmobile.pacman.api.admin.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The Class RuleCategory.
 */
@Entity
@Table(name = "pac_v2_ruleCategory_weightage", uniqueConstraints = @UniqueConstraint(columnNames = "ruleCategory"))
public class RuleCategory {

	@Id
	@Column(name = "ruleCategory", unique = true, nullable = false)
	private String ruleCategory;
	private String domain;
	private String weightage;
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getRuleCategory() {
		return ruleCategory;
	}
	public void setRuleCategory(String ruleCategory) {
		this.ruleCategory = ruleCategory;
	}
	public String getWeightage() {
		return weightage;
	}
	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}
}
