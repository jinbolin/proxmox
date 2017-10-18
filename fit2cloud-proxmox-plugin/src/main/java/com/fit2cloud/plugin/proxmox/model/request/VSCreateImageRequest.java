package com.fit2cloud.plugin.proxmox.model.request;

import com.fit2cloud.sdk.model.CreateImageRequest;

public class VSCreateImageRequest extends ProxmoxBaseRequest {
	private String imageDescription;
	private String imageName;
	private String instanceId;

	public VSCreateImageRequest() {
	}

	public VSCreateImageRequest(CreateImageRequest req) {
		if (req != null) {
			setCredential(req.getCredential());
			imageDescription = req.getImageDescription();
			imageName = req.getImageName();
			instanceId = req.getInstanceId();
		}
	}

	public String getImageDescription() {
		return imageDescription;
	}

	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
}