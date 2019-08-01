package com.tmobile.pacman.api.asset.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDetailsESResponse {

	private ApplicationESDetail _source;

	public void set_source(ApplicationESDetail _source) {
		this._source = _source;
	}

	public ApplicationESDetail getApplicationDetail() {
		return _source;
	}
}

