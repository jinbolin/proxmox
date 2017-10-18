package com.fit2cloud.plugin.proxmox.model.request;

public class LaunchInstanceRequest extends ProxmoxBaseRequest {

	private int vmid;

	private int newid;

	private String name;

	private String pool;

	private String storage;

	private boolean isfull;

	private long memory;

	private int cpuCount;

	private String network;

	private String vlan;

	private String model;

	private String userdata;

	private String role;

	private String format;

	private String target;

	private String full;

	private String sfIp;

	private long diskSize;

	public long getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(long diskSize) {
		this.diskSize = diskSize;
	}

	public String getSfIp() {
		return sfIp;
	}

	public void setSfIp(String sfIp) {
		this.sfIp = sfIp;
	}

	public boolean isIsfull() {
		return isfull;
	}

	public void setIsfull(boolean isfull) {
		this.isfull = isfull;
	}

	public String getFull() {
		return full;
	}

	public void setFull(String full) {
		this.full = full;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getVlan() {
		return vlan;
	}

	public void setVlan(String vlan) {
		this.vlan = vlan;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public long getMemory() {
		return memory;
	}

	public void setMemory(long memory) {
		this.memory = memory;
	}

	public int getCpuCount() {
		return cpuCount;
	}

	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}

	public String getUserdata() {
		return userdata;
	}

	public void setUserdata(String userdata) {
		this.userdata = userdata;
	}

	public int getVmid() {
		return vmid;
	}

	public void setVmid(int vmid) {
		this.vmid = vmid;
	}

	public int getNewid() {
		return newid;
	}

	public void setNewid(int newid) {
		this.newid = newid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}
}
