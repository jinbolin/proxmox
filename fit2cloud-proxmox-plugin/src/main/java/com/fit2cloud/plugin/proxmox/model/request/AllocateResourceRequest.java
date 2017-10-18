package com.fit2cloud.plugin.proxmox.model.request;

/**
 * Created by linjinbo on 2017/9/26.
 */
public class AllocateResourceRequest extends ProxmoxBaseRequest{
    private long memory;
    private int cpuCount;
    private String instanceId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
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
}
