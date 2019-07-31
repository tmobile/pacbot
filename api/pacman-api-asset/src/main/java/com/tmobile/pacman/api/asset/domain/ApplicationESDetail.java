package com.tmobile.pacman.api.asset.domain;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationESDetail {
	private String director;
	private String description;
	private List<Map<String, String>> _orgInfo;
	private String executiveSponsor;
	private String appTag;

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getDescription() {
		return StringUtils.chomp(description);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void set_orgInfo(List<Map<String, String>> _orgInfo) {
		this._orgInfo = _orgInfo;
	}

	public List<Map<String, String>> getOrgInfo() {
		return _orgInfo;
	}

	public String getExecutiveSponsor() {
		return executiveSponsor;
	}

	public void setExecutiveSponsor(String executiveSponsor) {
		this.executiveSponsor = executiveSponsor;
	}

	public String getAppTag() {
		return appTag;
	}

	public void setAppTag(String appTag) {
		this.appTag = appTag;
	}
}
