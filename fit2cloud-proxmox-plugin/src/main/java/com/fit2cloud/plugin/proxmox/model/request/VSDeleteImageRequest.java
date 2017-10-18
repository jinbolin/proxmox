package com.fit2cloud.plugin.proxmox.model.request;

import com.fit2cloud.sdk.model.DeleteImageRequest;

public class VSDeleteImageRequest extends ProxmoxBaseRequest {
	private String imageId;

	public VSDeleteImageRequest() {
	}

	public VSDeleteImageRequest(DeleteImageRequest req) {
		if (req != null) {
			setCredential(req.getCredential());
			imageId = req.getImageId();
		}
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
}
