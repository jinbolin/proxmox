package com.fit2cloud.plugin.proxmox.model;

public class PromoxMetricCounters {
	private int metricIdCpuUsageAverage;
	private int metricIdCpuUsagemhzAverage;
	private int metricIdMemUsageAverage;
	private int metricIdMemGrantedAverage;
	private int metricIdDiskUsedLatest;
	private int metricIdDiskCapacityProvisionedAverage;
	private int metricIdNetworkBytesRx;
	private int metricIdNetworkBytesTx;

	public int getMetricIdCpuUsageAverage() {
		return metricIdCpuUsageAverage;
	}

	public void setMetricIdCpuUsageAverage(int metricIdCpuUsageAverage) {
		this.metricIdCpuUsageAverage = metricIdCpuUsageAverage;
	}

	public int getMetricIdCpuUsagemhzAverage() {
		return metricIdCpuUsagemhzAverage;
	}

	public void setMetricIdCpuUsagemhzAverage(int metricIdCpuUsagemhzAverage) {
		this.metricIdCpuUsagemhzAverage = metricIdCpuUsagemhzAverage;
	}

	public int getMetricIdMemUsageAverage() {
		return metricIdMemUsageAverage;
	}

	public void setMetricIdMemUsageAverage(int metricIdMemUsageAverage) {
		this.metricIdMemUsageAverage = metricIdMemUsageAverage;
	}

	public int getMetricIdMemGrantedAverage() {
		return metricIdMemGrantedAverage;
	}

	public void setMetricIdMemGrantedAverage(int metricIdMemGrantedAverage) {
		this.metricIdMemGrantedAverage = metricIdMemGrantedAverage;
	}

	public int getMetricIdDiskUsedLatest() {
		return metricIdDiskUsedLatest;
	}

	public void setMetricIdDiskUsedLatest(int metricIdDiskUsedLatest) {
		this.metricIdDiskUsedLatest = metricIdDiskUsedLatest;
	}

	public int getMetricIdDiskCapacityProvisionedAverage() {
		return metricIdDiskCapacityProvisionedAverage;
	}

	public void setMetricIdDiskCapacityProvisionedAverage(int metricIdDiskCapacityProvisionedAverage) {
		this.metricIdDiskCapacityProvisionedAverage = metricIdDiskCapacityProvisionedAverage;
	}

	public int getMetricIdNetworkBytesRx() {
		return metricIdNetworkBytesRx;
	}

	public void setMetricIdNetworkBytesRx(int metricIdNetworkBytesRx) {
		this.metricIdNetworkBytesRx = metricIdNetworkBytesRx;
	}

	public int getMetricIdNetworkBytesTx() {
		return metricIdNetworkBytesTx;
	}

	public void setMetricIdNetworkBytesTx(int metricIdNetworkBytesTx) {
		this.metricIdNetworkBytesTx = metricIdNetworkBytesTx;
	}
}
