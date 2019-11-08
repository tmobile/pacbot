package com.tmobile.pacbot.azure.inventory.vo;

public class ElasticPoolVH {
	private String id;
	private String name;
	private String edition;
	private int size;
	private int storageCapacity;
	private int storageMB;
	private int dtu;

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

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getStorageCapacity() {
		return storageCapacity;
	}

	public void setStorageCapacity(int storageCapacity) {
		this.storageCapacity = storageCapacity;
	}

	public int getStorageMB() {
		return storageMB;
	}

	public void setStorageMB(int storageMB) {
		this.storageMB = storageMB;
	}

	public int getDtu() {
		return dtu;
	}

	public void setDtu(int dtu) {
		this.dtu = dtu;
	}

}
