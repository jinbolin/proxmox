package com.fit2cloud.plugin.proxmox.model;

public class ProxmoxCluster {
	private String name;
	private String description;
	private String info;

	public ProxmoxCluster() {
	}

	public ProxmoxCluster(String name) {
		this.name = name;
	}

	public ProxmoxCluster(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public ProxmoxCluster(String name, String description, String info) {
		super();
		this.name = name;
		this.description = description;
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
