package com.fit2cloud.plugin.proxmox;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import javax.security.auth.login.LoginException;
import com.fit2cloud.plugin.proxmox.model.request.*;
import com.fit2cloud.plugin.proxmox.model.request.StartInstanceRequest;
import com.fit2cloud.plugin.proxmox.model.request.StopInstanceRequest;
import com.fit2cloud.plugin.proxmox.model.request.TerminateInstanceRequest;
import com.fit2cloud.sdk.constants.F2CPricePolicy;
import com.fit2cloud.sdk.model.*;
import com.fit2cloud.sdk.model.GetRemoteConsoleRequest;
import net.elbandi.pve2api.data.DataStore;
import net.elbandi.pve2api.data.Node;
import net.elbandi.pve2api.data.VmMonitor;
import org.json.JSONException;
import com.fit2cloud.plugin.proxmox.model.PromoxMetricCounters;
import com.fit2cloud.plugin.proxmox.utils.ProxmoxUtil;
import com.fit2cloud.sdk.AbstractCloudProvider;
import com.fit2cloud.sdk.F2CPlugin;
import com.fit2cloud.sdk.PluginException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.elbandi.pve2api.Pve2Api;
import net.elbandi.pve2api.data.VmQemu;
import org.json.JSONObject;

@F2CPlugin
public class ProxmoxCloudprovider extends AbstractCloudProvider {

    private static String name = "fit2cloud-proxmox-plugin";
    private static final long MB = 1024 * 1024;
    private static final long GB = MB * 1024;

    private static Map<String, PromoxMetricCounters> credentialMetricCountersMap = new HashMap<String, PromoxMetricCounters>();

    private static boolean isCountersGet = false;

    public String getName() {
        return name;
    }

    public boolean isSupportLaunchInstance() {
        return true;
    }

    public boolean isSupportUserData() {
        return false;
    }

    public boolean isSupportCostAnalytics() {
        return false;
    }

    public boolean isSupportKey() {
        return false;
    }

    public boolean isSupportHost() {
        return true;
    }

    public boolean isSupportSubAccount() {
        return false;
    }

    public boolean isSupportCreateImage() {
        return true;
    }

    public boolean isSupportDataStore() {
        return true;
    }

    public List<F2CDataStore> getF2CDataStores(Request getF2CDataStoresRequest) throws PluginException {
        List<F2CDataStore> datastores = new ArrayList<F2CDataStore>();
        try {
            ProxmoxBaseRequest req = new ProxmoxBaseRequest(getF2CDataStoresRequest);
            Pve2Api client = req.getProxmoxClient();
            List<DataStore> dataStoreList = client.getStorageList();
            if (dataStoreList != null && dataStoreList.size() > 0) {
                for (DataStore s : dataStoreList) {
                    F2CDataStore f2CDataStore = new F2CDataStore();
                    f2CDataStore.setCapacity(s.getCapacity() / (1024 * 1024 * 1024));
                    f2CDataStore.setDataCenterName(s.getDataCenterName());
                    f2CDataStore.setDataCenterId(s.getDataCenterId());
                    f2CDataStore.setDataStoreId(s.getDataStoreId());
                    f2CDataStore.setDataStoreName(s.getDataStoreId());
                    f2CDataStore.setFreeSpace(s.getFreeSpace() / (1024 * 1024 * 1024));
                    f2CDataStore.setStatus(s.getStatus());
                    f2CDataStore.setType(s.getType());
                    datastores.add(f2CDataStore);
                }
            }
            return datastores;
        } catch (Exception e) {
            if (e instanceof PluginException) {
                throw (PluginException) e;
            }
            throw new PluginException(e.getMessage(), e);
        }
    }

    public List<F2CDataStore> getDataStores(String getDataStoreRequest){
        List<F2CDataStore> datastores = new ArrayList<F2CDataStore>();
        try {
            GetDataStoreRequest request = new Gson().fromJson(getDataStoreRequest, GetDataStoreRequest.class);
            String full = request.getFull();
            String node = request.getNode();
            String target = request.getTarget();
            Pve2Api client = request.getProxmoxClient();
            if (full.equals("false")){
                List<DataStore> dataStoreList = client.listDatastores(node);
                for (DataStore dataStore:dataStoreList){
                    String status = dataStore.getStatus();
                    if(!status.equals("0")){
                        F2CDataStore f2CDataStore = new F2CDataStore();
                        f2CDataStore.setDataStoreName(dataStore.getDataStoreName());
                        datastores.add(f2CDataStore);
                    }
                }
                return datastores;
            }
            if (full.equals("true")){
                if (target !=null){
                    List<DataStore> dataStoreList = client.listDatastores(target);
                    for (DataStore dataStore:dataStoreList){
                        String status = dataStore.getStatus();
                        if(!status.equals("0")){
                            F2CDataStore f2CDataStore = new F2CDataStore();
                            f2CDataStore.setDataStoreName(dataStore.getDataStoreName());
                            datastores.add(f2CDataStore);
                        }
                    }
                    return datastores;
                }
            }
        } catch (Exception e) {
        }
        return datastores;
    }

    public List<F2CHost> getF2CHosts(Request getHostsRequest) throws PluginException {
        List<F2CHost> f2CHostList = new ArrayList<F2CHost>();
        try {
            ProxmoxBaseRequest req = new ProxmoxBaseRequest(getHostsRequest);
            Pve2Api client = req.getProxmoxClient();
            List<String> nodeList = client.getNodeList();
            HashMap<String,String> map = client.getClusterStatus();
            String clusterId = map.get("clusterId");
            String clusterName = map.get("name");
            for (int i = 0; i < nodeList.size(); i++) {
                String node = nodeList.get(i);
                Node node1 = client.getNode(node);
                long uptime = node1.getUptime();
                Node.CpuInfo u = client.getNode(node).getCpuinfo();
                F2CHost f2CHost = new F2CHost();
                if (uptime > 0) {
                    f2CHost.setStatus("poweredOn");
                } else {
                    f2CHost.setStatus("poweredOff");
                }
                f2CHost.setClusterId(clusterId);
                f2CHost.setClusterName(clusterName);
                f2CHost.setDataCenterName("无限极中心机房");
                f2CHost.setVmTotal(ProxmoxUtil.getVmTotal(client, node));
                f2CHost.setVmRunning(ProxmoxUtil.getRunningVmNumber(client, node));
                f2CHost.setVmStopped(f2CHost.getVmTotal() - f2CHost.getVmRunning());
                f2CHost.setHostId("node"+"/"+node);
                f2CHost.setHostName(node);
                f2CHost.setCpuMHzTotal((long) (u.getMhz()));
                f2CHost.setCpuMHzAllocated((long) (client.getNode(node).getCpu() * u.getMhz()));
                f2CHost.setMemoryTotal(client.getNode(node).getMemory_total() / (1024 * 1024));
                f2CHost.setCpuModel(u.getModel());
                f2CHost.setVmCpuCores(u.getSockets());
                f2CHost.setMemoryAllocated((client.getNode(node).getMemory_total() - client.getNode(node).getMemory_free()) / (1024 * 1024));
                f2CHostList.add(f2CHost);
            }
            return f2CHostList;
        } catch (JsonSyntaxException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (LoginException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (JSONException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PluginException(e.getMessage(), e);
        }
    }

    public List<F2CHost> getHosts(String getHostsRequest) throws PluginException {
        List<F2CHost> f2CHostList = new ArrayList<F2CHost>();
        try {
            ProxmoxBaseRequest req = new Gson().fromJson(getHostsRequest, ProxmoxBaseRequest.class);
            Pve2Api client = req.getProxmoxClient();
            List<String> nodeList = client.getNodeList();
            if (nodeList.size() > 0) {
                for (int i = 0; i < nodeList.size(); i++) {
                    String node = nodeList.get(i);
                    F2CHost f2CHost = new F2CHost();
                    f2CHost.setHostName(node);
                    f2CHostList.add(f2CHost);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f2CHostList;
    }

    public F2CInstance getF2CInstance(String getInstanceRequest) throws PluginException {
        try {
            F2CInstance f2cInst = null;
            GetInstanceRequest req = new Gson().fromJson(getInstanceRequest, GetInstanceRequest.class);
            Pve2Api client = req.getProxmoxClient();
            VmQemu vm = client.getQemuVM(req.getNode(), req.getInstanceId());
            vm.setVmId(String.valueOf(req.getInstanceId()));
            client.getQemuConfig(req.getNode(), req.getInstanceId(), vm);
            if (vm != null) {
                f2cInst = ProxmoxUtil.toF2CInstance(vm, client, req.getNode());
            }
            return f2cInst;
        } catch (JsonSyntaxException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (LoginException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (JSONException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PluginException(e.getMessage(), e);
        }
    }

    public List<F2CEntityPerfMetric> getF2CPerfMetricList(GetMetricsRequest perfMetricsRequest) throws PluginException{
        List<F2CEntityPerfMetric> f2CEntityPerfMetrics = new ArrayList<F2CEntityPerfMetric>();
        try {
            String[] hosts = perfMetricsRequest.getHosts();
            if (hosts == null || hosts.length == 0) {
                return null;
            }
            ProxmoxBaseRequest req = new ProxmoxBaseRequest(perfMetricsRequest);
            Pve2Api client = req.getProxmoxClient();
            HashMap<String,String> clusterMap = client.getClusterStatus();
            String clusterName = clusterMap.get("name");

            for (int i=0;i<hosts.length;i++){
                String hostId = hosts[i];
                int index = hostId.indexOf("/");
                String nodeName = hostId.substring(index+1);
                Node node = client.getNode(nodeName);
                //宿主机监控
                F2CEntityPerfMetric f2CEntityPerfMetric = new F2CEntityPerfMetric();
                f2CEntityPerfMetric.setEntityType(F2CEntityType.HOSTSYSTEM);
                f2CEntityPerfMetric.setCloudUniqueId(hostId);
                f2CEntityPerfMetric.setClusterName(clusterName);
                f2CEntityPerfMetric.setHostName(nodeName);
                f2CEntityPerfMetric.setCpuUsage(node.getCpu()*10000);
                f2CEntityPerfMetric.setMemoryUsage((int)((node.getMemory_total()-node.getMemory_free())*10000/node.getMemory_total()));
                f2CEntityPerfMetrics.add(f2CEntityPerfMetric);
                //虚拟机监控
                List<HashMap<String, String>> vmMapList = client.getQeMus(nodeName);
                if (vmMapList !=null &&vmMapList.size()>0){
                    for (HashMap map:vmMapList){
                        for (Object vmid:map.keySet()){
                            long netin = 0;
                            long netout = 0;
                            //查询存储器
                            List<HashMap<String,String>> storageList = client.selectStorageUnderNode(nodeName);
                            F2CVmDatastoreUsage[] vmDatastoreUsage =new F2CVmDatastoreUsage[1];
                            if (storageList !=null &&storageList.size()>0){
                                for (HashMap hashMap:storageList){
                                    for (Object str:hashMap.keySet()){
                                        List<String> vmidList = client.getVmidUnderStorageMetric(nodeName,String.valueOf(str));
                                        if (vmidList.contains(vmid)){
                                            if (hashMap.get(str).equals("1")){
                                                F2CVmDatastoreUsage vmDatastoreUsage1 = new F2CVmDatastoreUsage();
                                                vmDatastoreUsage1.setDatastoreUniqueId(String.valueOf(str));
                                                vmDatastoreUsage1.setCommitedSize(0);
                                                vmDatastoreUsage1.setUncommitedSize(0);
                                                vmDatastoreUsage[0] = vmDatastoreUsage1;
                                            }else {
                                                F2CVmDatastoreUsage vmDatastoreUsage1 = new F2CVmDatastoreUsage();
                                                StringBuffer nodeString = new StringBuffer(nodeName);
                                                nodeString.append("/").append(String.valueOf(str));
                                                vmDatastoreUsage1.setDatastoreUniqueId(nodeString.toString());
                                                vmDatastoreUsage1.setCommitedSize(0);
                                                vmDatastoreUsage1.setUncommitedSize(0);
                                                vmDatastoreUsage[0] = vmDatastoreUsage1;
                                            }
                                        }
                                    }
                                }
                            }
                            List<VmMonitor> vmMonitorList = client.getVmMonitor(nodeName,Integer.parseInt(String.valueOf(vmid)));
                            if (vmMonitorList != null && vmMonitorList.size()>0){
                                for (VmMonitor v:vmMonitorList){
                                    netin = netin+v.getNetin();
                                    netout = netout+v.getNetout();
                                }
                                F2CEntityPerfMetric f2CEntityPerfMetricVM = new F2CEntityPerfMetric();
                                f2CEntityPerfMetricVM.setCloudUniqueId(String.valueOf(vmid)+"-"+String.valueOf(map.get(vmid)));
                                f2CEntityPerfMetricVM.setVirtualMachineName(String.valueOf(map.get(vmid)));
                                f2CEntityPerfMetricVM.setEntityType(F2CEntityType.VIRTUALMACHINE);
                                f2CEntityPerfMetricVM.setClusterName(clusterName);
                                f2CEntityPerfMetricVM.setMemoryUsage((int)(vmMonitorList.get(0).getMem()*10000/vmMonitorList.get(0).getMaxmem()));
                                f2CEntityPerfMetricVM.setCpuUsage(vmMonitorList.get(0).getCpu()*10000);
                                f2CEntityPerfMetricVM.setBytesReceivedPerSecond((int)(netin/vmMonitorList.size()));
                                f2CEntityPerfMetricVM.setBytesTransmittedPerSecond((int)(netout/vmMonitorList.size()));
                                f2CEntityPerfMetricVM.setDatastoreUsages(vmDatastoreUsage);
                                f2CEntityPerfMetrics.add(f2CEntityPerfMetricVM);
                            }
                            if (vmMonitorList != null && vmMonitorList.size()==0){
                                F2CEntityPerfMetric f2CEntityPerfMetricVM = new F2CEntityPerfMetric();
                                f2CEntityPerfMetricVM.setCloudUniqueId(String.valueOf(vmid)+"-"+String.valueOf(map.get(vmid)));
                                f2CEntityPerfMetricVM.setVirtualMachineName(String.valueOf(map.get(vmid)));
                                f2CEntityPerfMetricVM.setClusterName(clusterName);
                                f2CEntityPerfMetricVM.setEntityType(F2CEntityType.VIRTUALMACHINE);
                                f2CEntityPerfMetricVM.setMemoryUsage((int)0.0);
                                f2CEntityPerfMetricVM.setCpuUsage(0);
                                f2CEntityPerfMetricVM.setBytesReceivedPerSecond((int)0.0);
                                f2CEntityPerfMetricVM.setBytesTransmittedPerSecond((int)0.0);
                                f2CEntityPerfMetricVM.setDatastoreUsages(vmDatastoreUsage);
                                f2CEntityPerfMetrics.add(f2CEntityPerfMetricVM);
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            if (e instanceof PluginException) {
                throw (PluginException) e;
            }
            throw new PluginException(e.getMessage(), e);
        }
        return f2CEntityPerfMetrics;
    }

    //存储器监控
    public List<F2CDatastoreMetric> getF2CDatastoreMetrics(GetMetricsRequest getMetricsRequest) throws PluginException {
        List<F2CDatastoreMetric> f2CDatastoreMetrics = new ArrayList<F2CDatastoreMetric>();
        try {
            ProxmoxBaseRequest metricRequest = new ProxmoxBaseRequest(getMetricsRequest);
            Pve2Api client = metricRequest.getProxmoxClient();

            List<DataStore> dataStoreList = client.getStorageList();
            if (dataStoreList != null && dataStoreList.size() > 0) {
                for (DataStore s : dataStoreList) {
                    F2CDatastoreMetric f2CDatastoreMetric =new F2CDatastoreMetric();
                    f2CDatastoreMetric.setCloudUniqueId(s.getDataStoreId());
                    f2CDatastoreMetric.setDatastoreName(s.getDataStoreName());
                    f2CDatastoreMetric.setDataStoreTotalSpace(s.getCapacity()/(1024*1024*1024));
                    f2CDatastoreMetric.setDataStoreFreeSize(s.getFreeSpace()/(1024*1024*1024));
                    f2CDatastoreMetric.setDataStoreSpaceUsed(0);
                    f2CDatastoreMetric.setDataStoreProvisioned(0);
                    f2CDatastoreMetric.setDataStoreSwapFileUsed(0);
                    f2CDatastoreMetric.setDataStoreDeltaFileUsed(0);
                    f2CDatastoreMetric.setDataStoreOtherVMFileUsed(0);
                    f2CDatastoreMetric.setDataStoreDiskFileUsed(0);
                    f2CDatastoreMetrics.add(f2CDatastoreMetric);
                }
            }
        } catch (Exception e) {
            if (e instanceof PluginException) {
                throw (PluginException) e;
            }
            throw new PluginException(e.getMessage(), e);
        }

        return f2CDatastoreMetrics;
    }

    public List<F2CInstance> getF2CInstances(String getInstancesRequest) throws PluginException {
        List<F2CInstance> list = new ArrayList<F2CInstance>();
        try {
            ProxmoxBaseRequest req = new Gson().fromJson(getInstancesRequest, ProxmoxBaseRequest.class);
            Pve2Api client = req.getProxmoxClient();
            List<String> nodeList = client.getNodeList();
            for (String node : nodeList) {
                List<VmQemu> vmList = client.getQemuVMs(node);
                if (vmList != null && vmList.size() > 0) {
                    for (VmQemu vm : vmList) {
                        client.getQemuConfig(node, Integer.parseInt(vm.getVmId()), vm);
                        F2CInstance instance = ProxmoxUtil.toF2CInstance(vm, client, node);
                        if (instance != null) {
                            list.add(instance);
                        }
                    }
                }
            }
            return list;
        } catch (JsonSyntaxException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (LoginException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (JSONException e) {
            throw new PluginException(e.getMessage(), e);
        }
    }

    public F2CInstance launchInstance(String launchInstanceRequest) throws PluginException {
        F2CInstance f2cInstance = null;
        try {
            LaunchInstanceRequest req = new Gson().fromJson(launchInstanceRequest,LaunchInstanceRequest.class);
            Pve2Api client = req.getProxmoxClient();
            String serverName = req.getName();
            if (serverName == null || serverName.trim().length() == 0) {
                req.setName("i-" + UUID.randomUUID().toString().substring(0, 8));
            }
            int newid = client.getNextId();
            req.setNewid(newid);
            String full = req.getFull();
            if (full.equals("true")){
                req.setIsfull(true);
            }
            if (full.equals("false")){
                req.setIsfull(false);
            }
            if (req.isIsfull()){
                String vmid = String.valueOf(req.getVmid());
                List<String> stringList = client.getQemuVMIds(req.getNode());
                if (!stringList.contains(vmid)){
                    List<String> nodeNameList = client.getNodeList();
                    for (String nodeName:nodeNameList){
                        List<String> idList = client.getQemuVMIds(nodeName);
                        if (idList.contains(vmid)){
                            req.setNode(nodeName);
                        }
                    }
                }
                client.createQemu(req.getNode(), req.getVmid(), req.getNewid(), req.getName(), req.isIsfull(), req.getStorage(),req.getTarget());
            }else {
                client.createQemu(req.getNode(), req.getVmid(), req.getNewid(), req.getName());
            }
            VmQemu vm1 = null;
            String vmName = "";
            try {
                String model = req.getModel();
                String vlan = req.getVlan();
                String tag = "tag=" + vlan;
                String bridge = req.getNetwork();
                if (bridge == null) {
                    String network = model+","+tag;
                    req.setNetwork(network);
                }else{
                    String network = model + "," + "bridge=" + bridge + "," + tag;
                    req.setNetwork(network);
                }
                do {
                    vm1 = client.getQemuVM(req.getNode(), req.getNewid());
                    vmName = vm1.getName().substring(0, 2);
                } while (vmName.equals("VM"));
            } catch (Exception e) {
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
            if (req.isIsfull()){
                try {
                    String size = req.getDiskSize()+"G";
                    client.resizeDisk(req.getTarget(),req.getNewid(),size);
                }catch (Exception e){
                    client.deleteQemu(req.getTarget(),req.getNewid());
                    throw new PluginException(e.getMessage());
                }
                client.setQemuConfig(req.getTarget(), req.getNewid(), req.getCpuCount(), Integer.parseInt(String.valueOf(req.getMemory())), req.getNetwork());
                VmQemu vm = client.getQemuVM(req.getTarget(), req.getNewid());
                vm.setVmId(String.valueOf(newid));
                client.getQemuConfig(req.getTarget(), req.getNewid(), vm);
                if (vm != null) {
                    String sfIp = req.getSfIp();
                    if (sfIp !=null && sfIp.trim().length()>0 && "dhcp".equalsIgnoreCase(req.getSfIp())){
                    }
                    client.startQemu(req.getTarget(), Integer.parseInt(vm.getVmId()));
                    for (int i=0;i<10;i++){
                        VmQemu vm2 = client.getQemuVM(req.getTarget(), req.getNewid());
                        if (!vm2.getStatus().equals("running")){
                            try {
                                Thread.sleep(6000);
                            } catch (InterruptedException e) {
                            }
                        }else {
                            break;
                        }
                    }
                    VmQemu vm3 = client.getQemuVM(req.getTarget(), req.getNewid());
                    vm3.setVmId(String.valueOf(newid));
                    vm3.setName(req.getName());
                    if (!vm3.getStatus().equals("running")){
                        System.out.println("failed to start vm :"+vm.getName());
                        throw new PluginException("成功创建虚拟机，启动失败！");
                    }
                    client.getQemuConfig(req.getTarget(), Integer.parseInt(vm3.getVmId()), vm3);
                    f2cInstance = ProxmoxUtil.toF2CInstance(vm3, client, req.getTarget());
                }
            }else {
                try {
                    String size = req.getDiskSize()+"G";
                    client.resizeDisk(req.getNode(),req.getNewid(),size);
                }catch (Exception e){
                    client.deleteQemu(req.getNode(),req.getNewid());
                    throw new PluginException(e.getMessage());
                }
                client.setQemuConfig(req.getNode(), req.getNewid(), req.getCpuCount(), Integer.parseInt(String.valueOf(req.getMemory())), req.getNetwork());
                VmQemu vm = client.getQemuVM(req.getNode(), req.getNewid());
                vm.setVmId(String.valueOf(newid));
                client.getQemuConfig(req.getNode(), req.getNewid(), vm);
                if (vm != null) {
                    String sfIp = req.getSfIp();
                    if (sfIp !=null && sfIp.trim().length()>0 && "dhcp".equalsIgnoreCase(req.getSfIp())){
                    }
                    client.startQemu(req.getNode(), Integer.parseInt(vm.getVmId()));
                    for (int i=0;i<10;i++){
                        VmQemu vm2 = client.getQemuVM(req.getNode(), req.getNewid());
                        if (!vm2.getStatus().equals("running")){
                            try {
                                Thread.sleep(6000);
                            } catch (InterruptedException e) {
                            }
                        }else {
                            break;
                        }

                    }
                    VmQemu vm3 = client.getQemuVM(req.getNode(), req.getNewid());
                    vm3.setVmId(String.valueOf(newid));
                    vm3.setName(req.getName());
                    System.out.println("status:"+vm3.getStatus());
                    if (!vm3.getStatus().equals("running")){
                        System.out.println("failed to start vm :"+vm.getName());
                        throw new PluginException("成功创建虚拟机，启动失败！");
                    }
                    client.getQemuConfig(req.getNode(), Integer.parseInt(vm3.getVmId()), vm3);
                    f2cInstance = ProxmoxUtil.toF2CInstance(vm3, client, req.getNode());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f2cInstance;
    }

    public List<F2CImage> getF2CImages(Request cloudCredential) throws PluginException {
        try {
            List<F2CImage> templates = new ArrayList<F2CImage>();
            ProxmoxBaseRequest req = new ProxmoxBaseRequest(cloudCredential);
            Pve2Api client = req.getProxmoxClient();
            List<String> nodeList = client.getNodeList();
            for (String node : nodeList) {
                List<VmQemu> vmList = client.getQemuVMs(node);
                if (vmList != null && vmList.size() > 0) {
                    for (VmQemu vm : vmList) {
                        client.getQemuConfig(node, Integer.parseInt(vm.getVmId()), vm);
                        F2CImage f2CImage = ProxmoxUtil.toF2CImage(client, vm, node);
                        if (f2CImage != null) {
                            templates.add(f2CImage);
                        }
                    }
                }
            }
            return templates;
        } catch (Exception e) {
            if (e instanceof PluginException) {
                throw (PluginException) e;
            }
            throw new PluginException(e.getMessage(), e);
        }
    }

    public List<F2CImage> getImages(String getImagesRequest) {
        List<F2CImage> templates = new ArrayList<F2CImage>();
        List<F2CImage> templateExp = new ArrayList<F2CImage>();
        try {
            GetImageRequest req = new Gson().fromJson(getImagesRequest, GetImageRequest.class);
            String node = req.getNode();
            Pve2Api client = req.getProxmoxClient();
            String full = req.getFull();
            List<VmQemu> vmList = client.getQemuVMs(node);
            //模板主机下所有模板
            if (vmList != null && vmList.size() > 0) {
                for (VmQemu vm : vmList) {
                    F2CImage f2CImage = ProxmoxUtil.toF2CImage(vm);
                    if (f2CImage != null) {
                        templates.add(f2CImage);
                    }
                }
            }
            //存放模板id
            List<String> stringList4 = new ArrayList<String>();
            for (F2CImage f2CImage1:templates){
                stringList4.add(f2CImage1.getId());
            }

            List<DataStore> dataStoreList = client.listDatastores(node);
            for (DataStore dataStore:dataStoreList){
                String status = dataStore.getStatus();
                String shared = dataStore.getShared();
                if (shared !=null && status !=null){
                    if (status.equals("1") && shared.equals("1")){
                        String storageExp = dataStore.getDataStoreName();
                        //共享模版上的模板id
                        List<String> stringList = client.getVmidUnderStorage(node,storageExp);
                        //所有主机的nodeName
                        List<String> stringList1 = client.getNodeList();
                        for (String s:stringList1){
                            //一台主机下的所有虚拟机id
                            List<String> stringList2 = client.getQemuVMIds(s);
                            for (String s1:stringList2){
                                if (stringList.contains(s1)){
                                    VmQemu vmQemu = client.getQemuVM(s,Integer.parseInt(s1));
                                    vmQemu.setVmId(s1);
                                    F2CImage f2CImage = ProxmoxUtil.toF2CImage(vmQemu);
                                    if (f2CImage != null) {
                                        //筛除相同模版
                                        if (stringList4 !=null){
                                            if (!stringList4.contains(f2CImage.getId())){
                                                if (full.equals("false")){
                                                    templates.add(f2CImage);
                                                    return templates;
                                                }
                                                if (full.equals("true")){
                                                    templateExp.add(f2CImage);
                                                    return templateExp;
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
        }
        return templates;
    }
    public List<Map<String,String>> getTarget(String getTargetRequest){
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        try {
            GetTargetRequest req = new Gson().fromJson(getTargetRequest, GetTargetRequest.class);
            Pve2Api client = req.getProxmoxClient();
            String full = req.getFull();
            if (full.equals("true")){
                List<String> nodeNameList = client.getNodeList();
                for (String nodeName:nodeNameList){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name",nodeName);
                    mapList.add(map);
                }
            }
            if (full.equals("false")){
                return mapList;

            }
        }catch (Exception e){}
        return mapList;
    }
    public List<Map<String, String>> getPools(String getPoolsRequest) throws PluginException {
        try {
            List<Map<String, String>> poolList = new ArrayList<Map<String, String>>();
            Map<String, String> poolMap = new HashMap<String, String>();
            ProxmoxBaseRequest req = new Gson().fromJson(getPoolsRequest, ProxmoxBaseRequest.class);
            Pve2Api client = req.getProxmoxClient();
            List<String> poolsId = client.getPools();
            for (int i = 0; i < poolsId.size(); i++) {
                String pool = poolsId.get(i);
                poolMap.put("poolName", pool);
                poolList.add(poolMap);
            }
            return poolList;
        } catch (Exception e) {
            if (e instanceof PluginException) {
                throw (PluginException) e;
            }
            throw new PluginException(e.getMessage(), e);
        }
    }

    public List<Map<String, String>> getNetworks(String getNetworksRequest){
        List<Map<String, String>> networksList = new ArrayList<Map<String, String>>();
        try {
            LaunchInstanceRequest req = new Gson().fromJson(getNetworksRequest, LaunchInstanceRequest.class);
            Pve2Api client = req.getProxmoxClient();
            String node = req.getNode();
            String full = req.getFull();
            String target = req.getTarget();
            if (full !=null){
                if (full.equals("false")){
                    List<String> stringList = client.getNodeNetwork(node);
                    if (stringList !=null && stringList.size()>0){
                        for (String s : stringList) {
                            Map<String, String> netWorksMap = new HashMap<String, String>();
                            netWorksMap.put("key", s);
                            netWorksMap.put("value", s);
                            networksList.add(netWorksMap);
                        }
                    }
                }
                if (full.equals("true")){
                    if (target != null){
                        List<String> stringList = client.getNodeNetwork(target);
                        if (stringList !=null && stringList.size()>0){
                            for (String s : stringList) {
                                Map<String, String> netWorksMap = new HashMap<String, String>();
                                netWorksMap.put("key", s);
                                netWorksMap.put("value", s);
                                networksList.add(netWorksMap);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {}
        return networksList;
    }

    public List<Map<String, String>> getNetworkModel(String getNetworkModelRequest) throws PluginException {
        try {
            List<Map<String, String>> networksList = new ArrayList<Map<String, String>>();
            Map<String, String> modelMap1 = new HashMap<String, String>();
            modelMap1.put("key", "intel E1000");
            modelMap1.put("name", "e1000");
            networksList.add(modelMap1);
            Map<String, String> modelMap2 = new HashMap<String, String>();
            modelMap2.put("key", "VirtIO(paravitualized)");
            modelMap2.put("name", "virtio");
            networksList.add(modelMap2);
            Map<String, String> modelMap3 = new HashMap<String, String>();
            modelMap3.put("key", "Realtek RTL8139");
            modelMap3.put("name", "rtl8139");
            networksList.add(modelMap3);
            Map<String, String> modelMap4 = new HashMap<String, String>();
            modelMap4.put("key", "VMware vmxnet3");
            modelMap4.put("name", "vmxnet3");
            networksList.add(modelMap4);
            return networksList;
        } catch (Exception e) {
            if (e instanceof PluginException) {
                throw (PluginException) e;
            }
            throw new PluginException(e.getMessage(), e);
        }
    }

    public List<Map<String, String>> getFull(String request) {
        List<Map<String, String>> fullList = new ArrayList<Map<String, String>>();
        Map<String, String> noFullMap = new HashMap<String, String>();
        noFullMap.put("name", "Linked Clone");
        noFullMap.put("value", "false");
        fullList.add(noFullMap);
        Map<String, String> isFullMap = new HashMap<String, String>();
        isFullMap.put("name", "Full Clone");
        isFullMap.put("value", "true");
        fullList.add(isFullMap);
        return fullList;
    }

    public boolean isSupportPassword() {
        return true;
    }

    public List<F2CLoadBalancer> getF2CLoadBalancers(String arg0) throws PluginException {
        return null;
    }

    public boolean stopInstance(com.fit2cloud.sdk.model.StopInstanceRequest stopInstanceRequest)
            throws PluginException {
        try {
            StopInstanceRequest req = new StopInstanceRequest(stopInstanceRequest);
            Pve2Api client = req.getProxmoxClient();
            List<String> nodeList = client.getNodeList();
            for (String node : nodeList) {
                List<VmQemu> vmList = client.getQemuVMs(node);
                for (int i = 0; i < vmList.size(); i++) {
                    String vmid = vmList.get(i).getVmId();
                    if (vmid.equals(req.getInstanceId())) {
                        req.setNode(node);
                    }
                }
            }
            client.stopQemu(req.getNode(), Integer.valueOf(stopInstanceRequest.getInstanceId()));
            return true;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (LoginException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (JSONException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PluginException(e.getMessage(), e);
        }
        return false;
    }

    public boolean validateCredential(Request cloudCredential) throws PluginException {
        try {
            ProxmoxBaseRequest req = new ProxmoxBaseRequest(cloudCredential);
            Pve2Api client = req.getProxmoxClient();
            client.login();
            return true;
        } catch (Exception e) {
            if (e instanceof PluginException) {
                String errMsg = e.getMessage();
                if (errMsg != null && errMsg.contains("帐号验证失败")) {
                    return false;
                }
                throw (PluginException) e;
            }
            e.printStackTrace();
            throw new PluginException("验证账号发生错误！");
        }
    }

    public void validateLaunchInstanceConfiguration(String launchInstanceRequest) throws PluginException {
        try {
            LaunchInstanceRequest req = new Gson().fromJson(launchInstanceRequest, LaunchInstanceRequest.class);
            Pve2Api client = req.getProxmoxClient();
            int cpu = req.getCpuCount();
            long mem = req.getMemory();
            long diskSize = req.getDiskSize();
            boolean isUser = true;
            if ("admin".equalsIgnoreCase(req.getRole())) {
                isUser = false;
            }
            if (isUser) {
                if (diskSize < 5) {
                    throw new PluginException("磁盘大小不能小于5GB");
                }
                if (cpu <= 0) {
                    throw new PluginException("cpu核数不能小于1");
                }
                if (mem < 512 || mem % 4 != 0) {
                    throw new PluginException("内存大小不能小于512M, 且需要是4的倍数");
                }
                String template = String.valueOf(req.getVmid());
                VmQemu vm = null;
                List<String> nodeNameList = client.getNodeList();
                for (String nodeName:nodeNameList){
                    List<String> vmidUnderNodeList =  client.getTemplateIdList(nodeName);
                    if (vmidUnderNodeList.contains(template)){
                        vm = client.getQemuVM(nodeName,req.getVmid());
                        long tmpDiskSize = Math.round(vm.getMaxdisk()/(1024*1024*1024));
                        if (tmpDiskSize > diskSize) {
                            throw new PluginException("指定的硬盘大小不能小于模板中设定的硬盘大小, 模板设定的硬盘大小为 " + tmpDiskSize + " G");
                        }
                        break;
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new PluginException("请检查虚机创建配置");
        }catch (LoginException e){
            throw new PluginException(e.getMessage());
        }
        catch (IOException e){
            throw new PluginException(e.getMessage());
        }
    }

    public F2CInstance startInstance(com.fit2cloud.sdk.model.StartInstanceRequest startInstanceRequest)
            throws PluginException {
        try {
            StartInstanceRequest req = new StartInstanceRequest(startInstanceRequest);
            Pve2Api client = req.getProxmoxClient();
            List<String> nodeList = client.getNodeList();
            for (String node : nodeList) {
                List<VmQemu> vmList = client.getQemuVMs(node);
                for (int i = 0; i < vmList.size(); i++) {
                    String vmid = vmList.get(i).getVmId();
                    if (vmid.equals(req.getInstanceId())) {
                        req.setNode(node);
                    }
                }
            }
            client.startQemu(req.getNode(), Integer.valueOf(req.getInstanceId()));
            VmQemu vm = client.getQemuVM(req.getNode(), Integer.valueOf(req.getInstanceId()));
            return ProxmoxUtil.toF2CInstance(vm, client, req.getNode());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (LoginException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (JSONException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PluginException(e.getMessage(), e);
        }
        return null;
    }

    public boolean terminateInstance(com.fit2cloud.sdk.model.TerminateInstanceRequest terminateInstanceRequest)
            throws PluginException {
        try {
            TerminateInstanceRequest req = new TerminateInstanceRequest(terminateInstanceRequest);
            Pve2Api client = req.getProxmoxClient();
            List<String> nodeList = client.getNodeList();
            for (String node : nodeList) {
                List<VmQemu> vmList = client.getQemuVMs(node);
                for (int i = 0; i < vmList.size(); i++) {
                    String vmid = vmList.get(i).getVmId();
                    if (vmid.equals(req.getInstanceId())) {
                        req.setNode(node);
                    }
                }
            }
            VmQemu vm = client.getQemuVM(req.getNode(), Integer.valueOf(req.getInstanceId()));
            if (vm != null) {
                String status = vm.getStatus();
                if (status.equals("running")){
                    client.stopQemu(req.getNode(),Integer.valueOf(req.getInstanceId()));
                }
                while (true){
                    VmQemu VM = client.getQemuVM(req.getNode(), Integer.valueOf(req.getInstanceId()));
                    if (VM.getStatus().equals("stopped")){
                        client.deleteQemu(req.getNode(), Integer.valueOf(req.getInstanceId()));
                        break;
                    }
                }
            }
            return true;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (LoginException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (JSONException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PluginException(e.getMessage(), e);
        }
        return false;
    }

    public F2CImage createImage(CreateImageRequest createImageRequest) throws PluginException {
        VSCreateImageRequest req = new VSCreateImageRequest(createImageRequest);
        Pve2Api client = req.getProxmoxClient();
        // TODO Auto-generated method stub
        return null;
    }

    public boolean deleteImage(DeleteImageRequest deleteImageRequest) throws PluginException {
        VSDeleteImageRequest req = new VSDeleteImageRequest(deleteImageRequest);
        Pve2Api client = req.getProxmoxClient();
        int vmid = Integer.parseInt(req.getImageId());
        String node = req.getNode();
        try {
            client.deleteImage(node, vmid);
        } catch (JsonSyntaxException e) {
        } catch (NumberFormatException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (LoginException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (JSONException e) {
            throw new PluginException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PluginException(e.getMessage(), e);
        }
        return true;
    }
    public String getRemoteConsoleUrl(String getRemoteConsoleRequest) throws PluginException{
        //https://10.86.20.110:8006/?console=kvm&novnc=1&vmid=105&vmname=rpmtsvr01&node=vmpssvr15
        try {
            GetRemoteConsoleRequest greq = new Gson().fromJson(getRemoteConsoleRequest, GetRemoteConsoleRequest.class);
            ProxmoxBaseRequest req = new ProxmoxBaseRequest(greq);
            Pve2Api client = req.getProxmoxClient();
            JSONObject j = new JSONObject();
            String ticket = client.getTicket();
            String hostName = client.getHostName();
            String node =null;
            List<String> nodeNameList = client.getNodeList();
            for (String nodeName:nodeNameList){
                List<String> idList = client.getQemuVMIds(nodeName);
                if (idList.contains(greq.getResourceId())){
                    node = nodeName;
                    break;
                }
            }
            if (node != null){
                VmQemu vm = client.getQemuVM(node,Integer.parseInt(greq.getResourceId()));
                String name = vm.getName();
                String url = "https://"+hostName+"/?console=kvm&novnc=1&vmid="+greq.getResourceId()+
                        "&vmname="+name+"node="+node;
                j.put("ticket",ticket);
                j.put("url",url);
                return j.toString();
            }else {
                throw new PluginException("未找到虚拟机: " + greq.getResourceId());
            }
        }catch (Exception e) {
            throw new PluginException(e.toString());
        }
    }

    @Override
    public void allocateIp(String allocateIpRequest) throws PluginException {
        AllocateIpRequest req;
        try {
            req = new Gson().fromJson(allocateIpRequest, AllocateIpRequest.class);
            ProxmoxBaseRequest baseRequest = new ProxmoxBaseRequest(req);
            Pve2Api client = baseRequest.getProxmoxClient();
            String vmName = req.getResourceId();
            List<String> nodeList = client.getUpNodeList();
            if (nodeList !=null){
                for (String node:nodeList){
                    List<VmQemu> VmList = client.getQemuVMs(node);
                    for (VmQemu vmQemu:VmList){
                        if (vmName.equals(vmQemu.getName())){
                            if (req.getNetworkDevice() !=null && req.getNetworkType() !=null && req.getSfLoginIp()!=null && req.getMask()!=null && req.getGateway()!=null){
                                for (int i=0;i<30;i++){
                                    try{
                                        Thread.sleep(10000);
                                        client.getAgentStatus(node,Integer.parseInt(vmQemu.getVmId()),"get-time");
                                        break;
                                    }catch (Exception e){
                                        continue;
                                    }
                                }
                                String exargs = req.getNetworkDevice()+":"+req.getNetworkType()+":"+req.getSfLoginIp()+":"+req.getMask()+":"+req.getGateway();
                                String command = "guestos-setnet";
                                String result = client.setIp(node,Integer.parseInt(vmQemu.getVmId()),command,exargs);
                                if (result.equals("failed")){
                                    throw new PluginException("设置IP失败！!");
                                }
                            }else {
                                throw new PluginException("网卡名，网络类型，IP地址，掩码，网关不能为空!");
                            }
                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (LoginException e){
            e.printStackTrace();
        }
    }
    @Override
    public void resetPassword(String resetPasswordRequest) throws PluginException {
        ExecuteScriptRequest req;
        try {
            req = new Gson().fromJson(resetPasswordRequest, ExecuteScriptRequest.class);
            ProxmoxBaseRequest baseReq = new ProxmoxBaseRequest(req);
            Pve2Api client = baseReq.getProxmoxClient();
            String vmName = req.getResourceId();
            List<String> nodeList = client.getUpNodeList();
            if (nodeList !=null){
                for (String node:nodeList){
                    List<VmQemu> VmList = client.getQemuVMs(node);
                    for (VmQemu vmQemu:VmList){
                        if (vmName.equals(vmQemu.getName())){
                            if (req.getSfLoginPassword() !=null && req.getSfLoginUser() !=null){
                                for (int i=0;i<30;i++){
                                    try{
                                        Thread.sleep(10000);
                                        client.getAgentStatus(node,Integer.parseInt(vmQemu.getVmId()),"get-time");
                                        break;
                                    }catch (Exception e){
                                        continue;
                                    }
                                }
                                String exargs = req.getSfLoginPassword()+":"+req.getSfLoginUser();
                                String command = "guestos-setpwd";
                                String result = client.resetPassword(node,Integer.parseInt(vmQemu.getVmId()),command,exargs);
                                if (result.equals("failed")){
                                    throw new PluginException("设置密码失败！!");
                                }
                            }else {
                                throw new PluginException("登录密码和登录用户名不能为空！");
                            }

                        }
                    }
                }
            }
        }catch (IOException e){
            throw new PluginException(e.getMessage());
        }catch (LoginException e){
            throw new PluginException(e.getMessage());
        }
    }

    @Override
    public void resetHostname(String resetHostnameRequest) throws PluginException {
        ResetHostnameRequest req;
        try {
            req = new Gson().fromJson(resetHostnameRequest, ResetHostnameRequest.class);
            ProxmoxBaseRequest baseReq = new ProxmoxBaseRequest(req);
            Pve2Api client = baseReq.getProxmoxClient();
            String vmName = req.getResourceId();
            List<String> nodeList = client.getUpNodeList();
            if (nodeList !=null){
                for (String node:nodeList){
                    List<VmQemu> VmList = client.getQemuVMs(node);
                    for (VmQemu vmQemu:VmList){
                        if (vmName.equals(vmQemu.getName())){
                            if (req.getSfHostname() !=null){
                                for (int i=0;i<30;i++){
                                    try{
                                        Thread.sleep(10000);
                                        client.getAgentStatus(node,Integer.parseInt(vmQemu.getVmId()),"get-time");
                                        break;
                                    }catch (Exception e){
                                        continue;
                                    }
                                }
                                String exargs = req.getSfHostname();
                                String command = "guestos-sethostname";
                                String result = client.resetHostname(node,Integer.parseInt(vmQemu.getVmId()),command,exargs);
                                if (result.equals("failed")){
                                    throw new PluginException("重置hostname失败！!");
                                }
                            }else {
                                throw new PluginException("请检查请求参数!");
                            }

                        }
                    }
                }
            }
        } catch (IOException e){
            throw new PluginException(e.getMessage());
        }catch (LoginException e){
            throw new PluginException(e.getMessage());
        }
    }
    @Override
    public void executeScript(String executeScriptRequest) throws PluginException {
        try {
            ExecuteScriptRequest req = new Gson().fromJson(executeScriptRequest, ExecuteScriptRequest.class);
            ProxmoxBaseRequest request = new ProxmoxBaseRequest(req);
            Pve2Api client = request.getProxmoxClient();
            String vmName = req.getResourceId();
            List<String> nodeList = client.getUpNodeList();
            if (nodeList !=null){
                for (String node:nodeList){
                    List<VmQemu> VmList = client.getQemuVMs(node);
                    for (VmQemu vmQemu:VmList){
                        if (vmName.equals(vmQemu.getName())){
                            if (req.getScript() !=null){
                                for (int i=0;i<30;i++){
                                    try{
                                        Thread.sleep(10000);
                                        client.getAgentStatus(node,Integer.parseInt(vmQemu.getVmId()),"get-time");
                                        break;
                                    }catch (Exception e){
                                        continue;
                                    }
                                }
                                String script = req.getScript();
                                String command = "guestos-custom";
                                String result = client.excuteScript(node,Integer.parseInt(vmQemu.getVmId()),command,script);
                                if (result.equals("failed")){
                                    throw new PluginException("执行自定义脚本失败！!");
                                }
                            }else {
                                throw new PluginException("请检查脚本内容!");
                            }

                        }
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (LoginException e){
            e.printStackTrace();
        }
    }
    @Override
    public F2CInstance allocateResource(String allocateResourceRequest, String type) throws PluginException {
        AllocateResourceRequest req;
        try {
            req = new Gson().fromJson(allocateResourceRequest, AllocateResourceRequest.class);
        } catch (JsonSyntaxException e) {
            throw new PluginException("请检查请求参数!");
        }
        try {
            Pve2Api client = req.getProxmoxClient();
            if ("vm".equals(type)) {
                int vmid = Integer.parseInt(req.getInstanceId());
                List<String> nodeList = client.getNodeList();
                for (String node:nodeList){
                    List<String> vmidList = client.getVMIdList(node);
                    if (vmidList.contains(String.valueOf(vmid))){
                        int cpuCount = req.getCpuCount();
                        int memory = Integer.parseInt(String.valueOf(req.getMemory()));
                        VmQemu vmQemu = client.getQemuVM(node,vmid);
                        if (vmQemu.getStatus().equals("running")){
                            client.stopQemu(node,vmid);
                        }
                        for (int i=0;i<8;i++){
                            VmQemu vm2 = client.getQemuVM(node, vmid);
                            if (!vm2.getStatus().equals("stopped")){
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                }
                            }else {
                                break;
                            }
                        }
                        client.setQemuConfig(node,vmid,cpuCount,memory);
                        client.startQemu(node,vmid);
                        for (int i=0;i<8;i++){
                            VmQemu vm3 = client.getQemuVM(node, vmid);
                            if (!vm3.getStatus().equals("running")){
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                }
                            }else {
                                break;
                            }
                        }
                        VmQemu vm4 = client.getQemuVM(node, vmid);
                        return ProxmoxUtil.toF2CInstance(vm4,client,node);
                    }
                }
            }
        } catch (PluginException e) {
            throw e;
        } catch (Exception e) {
            throw new PluginException(e.toString());
        }
        return null;
    }

    @Override
    public F2CInstanceSize getInstanceSizeInfo(String launchInstanceRequest) throws PluginException{
        F2CInstanceSize result = new F2CInstanceSize();
        try {
            LaunchInstanceRequest req = new Gson().fromJson(launchInstanceRequest, LaunchInstanceRequest.class);
            result.setDisk(req.getDiskSize());
            result.setCpu(req.getCpuCount());
            result.setMem(req.getMemory()/1024);
        }catch(Exception e){
            log.error(e);
        }
        return result;
    }
    @Override
    public String getF2CPricePolicy() {
        return F2CPricePolicy.RESOURCE_CONFIGURATION;
    }

}
