package com.fit2cloud.plugin.proxmox.utils;

public class ProxmoxCredential {
	private String pve_hostname;
	private String pve_username;
	private String pve_realm;
	private String pve_password;

	public ProxmoxCredential() {
	}

	public ProxmoxCredential(String pve_hostname, String pve_username, String pve_realm, String pve_password) {
		this.pve_hostname = pve_hostname;
		this.pve_username = pve_username;
		this.pve_realm = pve_realm;
		this.pve_password = pve_password;
	}

	public String getPve_hostname() {
		return pve_hostname;
	}

	public void setPve_hostname(String pve_hostname) {
		this.pve_hostname = pve_hostname;
	}

	public String getPve_username() {
		return pve_username;
	}

	public void setPve_username(String pve_username) {
		this.pve_username = pve_username;
	}

	public String getPve_realm() {
		return pve_realm;
	}

	public void setPve_realm(String pve_realm) {
		this.pve_realm = pve_realm;
	}

	public String getPve_password() {
		return pve_password;
	}

	public void setPve_password(String pve_password) {
		this.pve_password = pve_password;
	}

}