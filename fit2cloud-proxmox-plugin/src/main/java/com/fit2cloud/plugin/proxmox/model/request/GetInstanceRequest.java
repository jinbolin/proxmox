package com.fit2cloud.plugin.proxmox.model.request;

public class GetInstanceRequest extends ProxmoxBaseRequest {
	private int instanceId;

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

}