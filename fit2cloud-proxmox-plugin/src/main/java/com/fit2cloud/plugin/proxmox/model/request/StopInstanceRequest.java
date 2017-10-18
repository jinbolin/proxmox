package com.fit2cloud.plugin.proxmox.model.request;

public class StopInstanceRequest extends ProxmoxBaseRequest {
	private String instanceId;

	public StopInstanceRequest() {
	}

	public StopInstanceRequest(com.fit2cloud.sdk.model.StopInstanceRequest req) {
		if (req != null) {
			setCredential(req.getCredential());
			instanceId = req.getInstanceId();
		}
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
}