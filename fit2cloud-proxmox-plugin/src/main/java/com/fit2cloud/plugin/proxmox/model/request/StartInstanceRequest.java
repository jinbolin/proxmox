package com.fit2cloud.plugin.proxmox.model.request;

public class StartInstanceRequest extends ProxmoxBaseRequest {
	private String instanceId;

	public StartInstanceRequest() {
	}

	public StartInstanceRequest(com.fit2cloud.sdk.model.StartInstanceRequest req) {
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