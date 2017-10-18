package com.fit2cloud.plugin.proxmox;

import com.fit2cloud.plugin.proxmox.model.request.*;
import com.fit2cloud.sdk.model.*;
import com.fit2cloud.sdk.model.StartInstanceRequest;
import com.fit2cloud.sdk.model.StopInstanceRequest;
import com.fit2cloud.sdk.model.TerminateInstanceRequest;
import com.google.gson.GsonBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.fit2cloud.plugin.proxmox.utils.ProxmoxCredential;
import com.fit2cloud.sdk.PluginException;
import com.google.gson.Gson;
import sun.misc.BASE64Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PromoxCloudproviderTest {

//	private String pve_hostname = "172.20.70.24";
//	private String pve_username = "root";
//	private String pve_realm = "pam";
//	private String pve_password = "wwwwww";
//	private ProxmoxCredential proxmoxCredential;
//	private ProxmoxCloudprovider provider;

//	private String pve_hostname = "10.86.20.110";
//	private String pve_username = "root";
//	private String pve_realm = "pam";
//	private String pve_password = "abcd@1234";
//	private ProxmoxCredential proxmoxCredential;
//	private ProxmoxCloudprovider provider;
	private String pve_hostname = "10.86.20.221";
	private String pve_username = "fit2cloud";
	private String pve_realm = "pve";
	private String pve_password = "User@2017";
	private ProxmoxCredential proxmoxCredential;
	private ProxmoxCloudprovider provider;

	@Before
	public void setUp() throws Exception {
//		this.pve_hostname = "10.86.20.110";
//		this.pve_username = "root";
//		this.pve_realm = "pam";
//		this.pve_password = "abcd@1234";
		this.pve_hostname = "10.86.20.221";
		this.pve_username = "fit2cloud";
		this.pve_realm = "pve";
		this.pve_password = "User@2017";
//		this.pve_hostname = "172.20.70.24";
//		this.pve_username = "root";
//		this.pve_realm = "pam";
//		this.pve_password = "wwwwww";
		this.proxmoxCredential = new ProxmoxCredential(pve_hostname, pve_username, pve_realm, pve_password);
		this.provider = new ProxmoxCloudprovider();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getF2CInstanceTest() {
		try {
			String node = "vmpssvr16";
			int instanceId = 117;
			GetInstanceRequest req = new GetInstanceRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			req.setNode(node);
			req.setInstanceId(instanceId);
			F2CInstance f2CInstance = provider.getF2CInstance(new Gson().toJson(req));
			System.out.println("___________________");
			System.out.println("upTime:"+f2CInstance.getCreated());
			System.out.println("cluster:"+f2CInstance.getCluster());
			System.out.println("cpu:"+f2CInstance.getCpu());
			System.out.println("cpuUsed:"+f2CInstance.getCpuUsed());
			System.out.println("create:"+f2CInstance.getCreated());//Date
			System.out.println("createTime:"+f2CInstance.getCreateTime());//Long
			System.out.println("customDate:"+f2CInstance.getCustomData());
			System.out.println("dataCenter:"+f2CInstance.getDataCenter());
			System.out.println("datastoreName:"+f2CInstance.getDatastoreName());
			System.out.println("datastoreType:"+f2CInstance.getDatastoreType());
			System.out.println("description:"+f2CInstance.getDescription());
			System.out.println("disk:"+f2CInstance.getDisk());
			System.out.println("diskUsed:"+f2CInstance.getDiskUsed());
			System.out.println("host:"+f2CInstance.getHost());
			System.out.println("hostname:"+f2CInstance.getHostname());
			System.out.println("imageId:"+f2CInstance.getImageId());
			System.out.println("instanceId:"+f2CInstance.getInstanceId());
			System.out.println("instanceStatus:"+f2CInstance.getInstanceStatus());
			System.out.println("instanceType:"+f2CInstance.getInstanceType());
			System.out.println("instanceTypeDescription:"+f2CInstance.getInstanceTypeDescription());
			System.out.println("instanceUUID"+f2CInstance.getInstanceUUID());
			System.out.println("ipArray:"+f2CInstance.getIpArray());
			System.out.println("keypasswordId:"+f2CInstance.getKeypasswordId());
			System.out.println("localIp:"+f2CInstance.getLocalIP());
			System.out.println("memory:"+f2CInstance.getMemory());
			System.out.println("name:"+f2CInstance.getName());
			System.out.println("memoryUsed:"+f2CInstance.getMemoryUsed());
			System.out.println("os:"+f2CInstance.getOs());
			System.out.println("region:"+f2CInstance.getRegion());
			System.out.println("remote:"+f2CInstance.getRemoteIP());
			System.out.println("zone:"+f2CInstance.getZone());
			System.out.println("resource:"+f2CInstance.getResourceId());
		} catch (PluginException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getF2CImagesTest(){
		try {
			Request req = new Request();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			List<F2CImage> f2CImageList = provider.getF2CImages(req);
			for (int i=0;i<f2CImageList.size();i++){
				F2CImage f2CImage = f2CImageList.get(i);
				System.out.println("instanceId="+f2CImage.getId());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getF2CInstancesTest(){
		try {
			GetInstanceRequest req = new GetInstanceRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			List<F2CInstance> f2CInstanceList = provider.getF2CInstances(new Gson().toJson(req));
			System.out.println("虚拟机总数："+f2CInstanceList.size());
			for(int i=0;i<f2CInstanceList.size();i++){
				F2CInstance f2CInstance = f2CInstanceList.get(i);
				System.out.println("___________________");
				System.out.println("instanceId:"+f2CInstance.getInstanceId());
				System.out.println("imageId:"+f2CInstance.getImageId());
				System.out.println("cluster:"+f2CInstance.getCluster());
				System.out.println("cpu:"+f2CInstance.getCpu());
				System.out.println("cpuUsed:"+f2CInstance.getCpuUsed());
				System.out.println("create:"+f2CInstance.getCreated());//Date
				System.out.println("createTime:"+f2CInstance.getCreateTime());//Long
				System.out.println("customDate:"+f2CInstance.getCustomData());
				System.out.println("dataCenter:"+f2CInstance.getDataCenter());
				System.out.println("datastoreName:"+f2CInstance.getDatastoreName());
				System.out.println("datastoreType:"+f2CInstance.getDatastoreType());
				System.out.println("description:"+f2CInstance.getDescription());
				System.out.println("disk:"+f2CInstance.getDisk());
				System.out.println("diskUsed:"+f2CInstance.getDiskUsed());
				System.out.println("host:"+f2CInstance.getHost());
				System.out.println("hostname:"+f2CInstance.getHostname());
				System.out.println("imageId:"+f2CInstance.getImageId());
				System.out.println("instanceId:"+f2CInstance.getInstanceId());
				System.out.println("instanceStatus:"+f2CInstance.getInstanceStatus());
				System.out.println("instanceType:"+f2CInstance.getInstanceType());
				System.out.println("instanceTypeDescription:"+f2CInstance.getInstanceTypeDescription());
				System.out.println("instanceUUID"+f2CInstance.getInstanceUUID());
				System.out.println("ipArray:"+f2CInstance.getIpArray());
				System.out.println("keypasswordId:"+f2CInstance.getKeypasswordId());
				System.out.println("localIp:"+f2CInstance.getLocalIP());
				System.out.println("memory:"+f2CInstance.getMemory());
				System.out.println("name:"+f2CInstance.getName());
				System.out.println("memoryUsed:"+f2CInstance.getMemoryUsed());
				System.out.println("os:"+f2CInstance.getOs());
				System.out.println("region:"+f2CInstance.getRegion());
				System.out.println("remote:"+f2CInstance.getRemoteIP());
				System.out.println("zone:"+f2CInstance.getZone());
				System.out.println("resource:"+f2CInstance.getResourceId());
			}
//			System.out.println(f2CInstanceList);
		}catch (PluginException e){
			e.printStackTrace();
		}
	}
	@Test
	public void getDatastoreListTest(){
		try {
			Request request = new Request();
			request.setCredential(new Gson().toJson(proxmoxCredential));
			List<F2CDataStore> list =provider.getF2CDataStores(request);
			System.out.println(new Gson().toJson(list));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void stopInstanceTest(){
		try {
			String instanceId = "900";
			StopInstanceRequest req = new StopInstanceRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			req.setInstanceId(instanceId);
			boolean result = provider.stopInstance(req);
			System.out.println(result);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void startInstanceTest(){
		try{
			StartInstanceRequest req = new StartInstanceRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			String instanceId = "115";
			req.setInstanceId(instanceId);
			F2CInstance mv = provider.startInstance(req);
			System.out.println(mv);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void terminateInstanceTest(){
		try {
			TerminateInstanceRequest req = new TerminateInstanceRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			String instanceId = "105";
			String node = "test1";
			req.setInstanceId(instanceId);
			req.setCustomData(node);
			boolean result = provider.terminateInstance(req);
			System.out.println(result);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void validateCredentialTest(){
		try {
			Request req = new Request();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			boolean result = provider.validateCredential(req);
			System.out.println(result);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getF2CHostsTest(){
		try {
			Request req = new Request();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			List<F2CHost> f2CHostList = provider.getF2CHosts(req);
			System.out.println(new Gson().toJson(f2CHostList));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getF2CDataStoresTest(){
		try {
			Request req = new Request();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			List<F2CDataStore> f2CDataStoreList = provider.getF2CDataStores(req);
			System.out.println(f2CDataStoreList);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getDataStoresTest(){
		try {
			LaunchInstanceRequest req = new LaunchInstanceRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			req.setFull("true");
			req.setNode("vmpssvr16");
			req.setTarget("vmpssvr23");
			List<F2CDataStore> list = provider.getDataStores(new Gson().toJson(req));
			System.out.println("list:"+list);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getImagesTest(){
		try {
			String node = "vmpssvr11";
//			String target = "vmpssvr08";
			String full = "false";
//			String storage = "nfs3";
			GetImageRequest req = new GetImageRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			req.setNode(node);
//			req.setTarget(target);
			req.setFull(full);
//			req.setStorage(storage);
			List<F2CImage> f2CImageList = provider.getImages(new Gson().toJson(req));
			System.out.println("success!"+f2CImageList.size());

		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void launchInstanceTest(){
		try {
			String node = "vmpssvr16";//在哪个node下面创建
			int vmid = 901;//模板id
			String name = "fit2cloud-test-new-03-full";//新建虚拟机的名称
			String full= "true";
			String storage ="NFS";
			String target ="vmpssvr20";
			int sockets =4;
			long memory = 2048;
			String vlan = "72";
			String model ="virtio";
			long size = 50;
			String bridge = "vmbr1";
			LaunchInstanceRequest request = new LaunchInstanceRequest();
			request.setDiskSize(size);
			request.setNode(node);
			request.setVmid(vmid);
			request.setName(name);
			request.setFull(full);
			request.setTarget(target);
			request.setModel(model);
			request.setVlan(vlan);
			request.setStorage(storage);
			request.setCpuCount(sockets);
			request.setNetwork(bridge);
			request.setMemory(memory);
			request.setCredential(new Gson().toJson(proxmoxCredential));
//			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			F2CInstance f2CInstance = provider.launchInstance(new Gson().toJson(request));
			System.out.println("cluster:"+f2CInstance.getCluster());
			System.out.println("success!");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getRemoteConsoleUrlTest(){
		try {
			String vmid = "107";
			GetRemoteConsoleUrlRequest req = new GetRemoteConsoleUrlRequest();
			req.setResourceId(vmid);
			req.setCredential(new Gson().toJson(proxmoxCredential));
			String s = provider.getRemoteConsoleUrl(new Gson().toJson(req));
			System.out.println("json:"+s);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getF2CPerfMetricListAtOnceTest(){
		try {
			GetMetricsRequest getMetricsRequest = new GetMetricsRequest();
			getMetricsRequest.setCredential(new Gson().toJson(proxmoxCredential));
			List<F2CEntityPerfMetric> list= provider.getF2CPerfMetricListAtOnce(getMetricsRequest);
			System.out.println(list);
			System.out.println("success!");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getF2CPerfMetricListTest(){
		try {
			GetMetricsRequest getMetricsRequest = new GetMetricsRequest();
			String[] host = new String[2];
			host[0] = "node/vmpssvr19";
			host[1] = "node/vmpssvr21";
			getMetricsRequest.setHosts(host);
			getMetricsRequest.setCredential(new Gson().toJson(proxmoxCredential));
			List<F2CEntityPerfMetric> list= provider.getF2CPerfMetricList(getMetricsRequest);
			for (F2CEntityPerfMetric s:list){
				if (s.getDatastoreUsages() !=null){
					System.out.println("长度=："+s.getDatastoreUsages().length);
				}
			}
			System.out.println(list);
			System.out.println("success!");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void getF2CDatastoreMetricsTest(){
		try {
			GetMetricsRequest req = new GetMetricsRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			List<F2CDatastoreMetric> list = provider.getF2CDatastoreMetrics(req);
			System.out.println(list);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void allocateIpTest(){
		try {
			AllocateIpRequest allocateIpRequest = new AllocateIpRequest();
			allocateIpRequest.setCredential(new Gson().toJson(proxmoxCredential));
			String resourceId = "i-e1d0d7ef";
			String networkDevice = "eth2";
			String networkType = "Ethernet";
			String sfLoginIp = "172.20.70.239";
			String mask = "255.255.255.0";
			String gateway = "172.20.70.1";
			allocateIpRequest.setResourceId(resourceId);
			allocateIpRequest.setNetworkDevice(networkDevice);
			allocateIpRequest.setNetworkType(networkType);
			allocateIpRequest.setSfLoginIp(sfLoginIp);
			allocateIpRequest.setMask(mask);
			allocateIpRequest.setGateway(gateway);
			provider.allocateIp(new Gson().toJson(allocateIpRequest));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void resetHostnameTest(){
		try {
			ResetHostnameRequest req = new ResetHostnameRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			String sfhostname = "lin-test";
			String resourceId = "wz-test";
			String sfLoginPassword ="***";
			String sfLoginUser = "****";
			req.setSfLoginPassword(sfLoginPassword);
			req.setSfLoginUser(sfLoginUser);
			req.setSfHostname(sfhostname);
			req.setResourceId(resourceId);
			provider.resetHostname(new Gson().toJson(req));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void resetPasswordTest(){
		try {
			ExecuteScriptRequest req = new ExecuteScriptRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			String resourceId = "f2ccdsvr001-c596010e";
			String sfLoginPassword ="Test@123";
			String sfLoginUser = "appadmin";
			req.setSfLoginPassword(sfLoginPassword);
			req.setSfLoginUser(sfLoginUser);
			req.setResourceId(resourceId);
			provider.resetPassword(new Gson().toJson(req));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void executeScriptTest(){
		try {
			ExecuteScriptRequest req = new ExecuteScriptRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			String resourceId = "wz-test";
			String exargs = "echo \"Hello Girl\" > /tmp/f2c_test.txt";
			BASE64Encoder base64Encoder = new BASE64Encoder();
			String ex = base64Encoder.encode(exargs.getBytes());
			req.setResourceId(resourceId);
			req.setScript(ex);
			provider.executeScript(new Gson().toJson(req));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void validateLaunchInstanceConfigurationTest(){
		try {
			LaunchInstanceRequest request = new LaunchInstanceRequest();
			request.setCredential(new Gson().toJson(proxmoxCredential));
			request.setDiskSize(60);
			request.setVmid(114);
			request.setRole("user");
			request.setCpuCount(2);
			request.setMemory(512);
			provider.validateLaunchInstanceConfiguration(new Gson().toJson(request));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	@Test
	public void allocateResourceTest(){
		try {
			AllocateResourceRequest req = new AllocateResourceRequest();
			req.setCredential(new Gson().toJson(proxmoxCredential));
			req.setInstanceId("100");
			req.setCpuCount(8);
			req.setMemory(1024);
			String type = "vm";
			provider.allocateResource(new Gson().toJson(req),type);
			System.out.println("success!");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}