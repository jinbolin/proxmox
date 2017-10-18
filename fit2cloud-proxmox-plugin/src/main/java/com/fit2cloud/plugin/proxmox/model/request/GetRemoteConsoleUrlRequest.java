package com.fit2cloud.plugin.proxmox.model.request;

/**
 * Created by linjinbo on 2017/8/21.
 */
public class GetRemoteConsoleUrlRequest extends ProxmoxBaseRequest{
    private String resourceId;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
