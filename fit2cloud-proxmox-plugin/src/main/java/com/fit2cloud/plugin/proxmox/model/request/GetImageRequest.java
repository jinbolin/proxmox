package com.fit2cloud.plugin.proxmox.model.request;

/**
 * Created by linjinbo on 2017/8/11.
 */
public class GetImageRequest extends ProxmoxBaseRequest{

    private String storage;

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

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }
}
