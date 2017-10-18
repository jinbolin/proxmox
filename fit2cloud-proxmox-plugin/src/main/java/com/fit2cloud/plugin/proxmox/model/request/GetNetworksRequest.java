package com.fit2cloud.plugin.proxmox.model.request;

/**
 * Created by linjinbo on 2017/8/17.
 */
public class GetNetworksRequest extends ProxmoxBaseRequest{

    private String target;

    private String full;

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
