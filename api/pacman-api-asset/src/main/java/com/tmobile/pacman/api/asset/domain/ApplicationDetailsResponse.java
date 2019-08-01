package com.tmobile.pacman.api.asset.domain;

import java.util.List;

public class ApplicationDetailsResponse
{
	private List<ApplicationDetail> validApplications;
	private List<ApplicationDetail> invalidApplications;

	public List<ApplicationDetail> getValidApplications()
	{
		return validApplications;
	}

	public void setValidApplications(List<ApplicationDetail> validApplications)
	{
		this.validApplications = validApplications;
	}

	public List<ApplicationDetail> getInvalidApplications()
	{
		return invalidApplications;
	}

	public void setInvalidApplications(List<ApplicationDetail> invalidApplications)
	{
		this.invalidApplications = invalidApplications;
	}
}
