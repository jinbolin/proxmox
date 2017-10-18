package com.fit2cloud.plugin.proxmox.model.request;

import com.fit2cloud.plugin.proxmox.utils.ProxmoxCredential;
import com.fit2cloud.sdk.PluginException;
import com.fit2cloud.sdk.model.Request;
import com.google.gson.Gson;

import net.elbandi.pve2api.Pve2Api;

public class ProxmoxBaseRequest extends Request {
	private ProxmoxCredential proxmoxCredential;
	private String node;
	public ProxmoxBaseRequest() {
	}

	public ProxmoxBaseRequest(Request req) {
		setCredential(req.getCredential());
		setRegionId(req.getRegionId());
	}

	public ProxmoxCredential getProxmoxCredential() {
		if (proxmoxCredential == null) {
			proxmoxCredential = new Gson().fromJson(getCredential(), ProxmoxCredential.class);
		}
		return proxmoxCredential;
	}

	public Pve2Api getProxmoxClient() throws PluginException {
		ProxmoxCredential vCredential = getProxmoxCredential();
		if (vCredential != null) {
			return new Pve2Api(vCredential.getPve_hostname(), vCredential.getPve_username(), vCredential.getPve_realm(),
					vCredential.getPve_password());
		}
		return null;
	}

	public String getPveUsername() {
		proxmoxCredential = getProxmoxCredential();
		if (proxmoxCredential != null) {
			return proxmoxCredential.getPve_username();
		}
		return null;
	}

	public String getPvePassword() {
		proxmoxCredential = getProxmoxCredential();
		if (proxmoxCredential != null) {
			return proxmoxCredential.getPve_password();
		}
		return null;
	}

	public String getPveHostname() {
		proxmoxCredential = getProxmoxCredential();
		if (proxmoxCredential != null) {
			return proxmoxCredential.getPve_hostname();
		}
		return null;
	}

	public String getPveRealm() {
		proxmoxCredential = getProxmoxCredential();
		if (proxmoxCredential != null) {
			return proxmoxCredential.getPve_realm();
		}
		return null;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

}