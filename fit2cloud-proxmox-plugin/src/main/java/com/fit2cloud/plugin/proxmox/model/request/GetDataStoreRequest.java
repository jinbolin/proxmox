package com.fit2cloud.plugin.proxmox.model.request;

/**
 * Created by linjinbo on 2017/8/13.
 */
public class GetDataStoreRequest extends ProxmoxBaseRequest{

    private String full;

    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }
}
