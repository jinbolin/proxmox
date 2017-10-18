package com.fit2cloud.plugin.proxmox.model.request;

public class TerminateInstanceRequest extends ProxmoxBaseRequest {
	private String instanceId;

	public TerminateInstanceRequest() {
	}

	public TerminateInstanceRequest(com.fit2cloud.sdk.model.TerminateInstanceRequest req) {
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