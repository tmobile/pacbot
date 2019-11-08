package com.tmobile.pacbot.azure.inventory.vo;

public class FailoverGroupVH {
	private String id;
	private String name;
	private String replicationState;
	private String readOnlyEndpointPolicy;
	private String readWriteEndpointPolicy;
	private int size;
	private int gracePeriod;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReplicationState() {
		return replicationState;
	}

	public void setReplicationState(String replicationState) {
		this.replicationState = replicationState;
	}

	public String getReadOnlyEndpointPolicy() {
		return readOnlyEndpointPolicy;
	}

	public void setReadOnlyEndpointPolicy(String readOnlyEndpointPolicy) {
		this.readOnlyEndpointPolicy = readOnlyEndpointPolicy;
	}

	public String getReadWriteEndpointPolicy() {
		return readWriteEndpointPolicy;
	}

	public void setReadWriteEndpointPolicy(String readWriteEndpointPolicy) {
		this.readWriteEndpointPolicy = readWriteEndpointPolicy;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

}
