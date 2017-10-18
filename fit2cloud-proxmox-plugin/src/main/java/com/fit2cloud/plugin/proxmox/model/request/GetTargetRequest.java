package com.fit2cloud.plugin.proxmox.model.request;

/**
 * Created by linjinbo on 2017/8/17.
 */
public class GetTargetRequest extends ProxmoxBaseRequest{
    private String full;

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }
}
