package com.tmobile.pacman.api.asset.domain;

import java.util.List;

public class ApplicationDetail
{
	private String name;
	private String description;
	private List<Organization> organization;
	private String assetGroupId;
	private Long totalResources;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<Organization> getOrganization()
	{
		return organization;
	}

	public void setOrganization(List<Organization> organization)
	{
		this.organization = organization;
	}

	public String getAssetGroupId()
	{
		return assetGroupId;
	}

	public void setAssetGroupId(String assetGroupId)
	{
		this.assetGroupId = assetGroupId;
	}

	public Long getTotalResources() 
	{
		return totalResources;
	}

	public void setTotalResources(Long totalResources) 
	{
		this.totalResources = totalResources;
	}
}
