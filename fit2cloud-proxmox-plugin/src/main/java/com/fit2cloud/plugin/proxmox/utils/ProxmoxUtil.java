package com.fit2cloud.plugin.proxmox.utils;

import com.fit2cloud.sdk.model.F2CImage;
import com.fit2cloud.sdk.model.F2CInstance;

import net.elbandi.pve2api.Pve2Api;
import net.elbandi.pve2api.data.VmQemu;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProxmoxUtil {
	public static F2CInstance toF2CInstance(VmQemu vm, Pve2Api client,String node) {
		if (vm == null) {
			return null;
		}
		if(vm != null){
			if(vm.getImageId().equals("1")){
				return null;
			}
		}
		int cpuUserd = 0;
		String description ="";
		String localIp =" ";
		try {
			description = client.getQemuConfig(node,Integer.parseInt(vm.getVmId()));
		}catch (Exception e){}
		try {
			float mhz = client.getNode(node).getCpuinfo().getMhz();
			cpuUserd = Math.round(vm.getCpu()*vm.getCpus()*mhz);
			localIp = client.getLocalIP(node,Integer.parseInt(vm.getVmId()),"network-get-interfaces");
		}catch (Exception e){
		}
		F2CInstance instance = new F2CInstance();
		java.util.Date dt = new java.util.Date();
		long time = dt.getTime();
		long d = vm.getUptime();
		instance.setCreateTime(time-d);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeExp = dateFormat.format(dt);
		try {
			java.util.Date date = dateFormat.parse(timeExp);
			instance.setCreated(date);
			System.out.println("data:"+date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String status = vm.getStatus();
		if(status.equals("running")){
			instance.setInstanceStatus("Running");
		}else if(status.equals("stopped")){
			instance.setInstanceStatus("Stopped");
		}
		try {
			int vmid = Integer.parseInt(vm.getVmId());
			String bootDisk = client.getBootDisk(node,vmid);
			String storage = client.getIdeoStorage(node,vmid,bootDisk);
			instance.setDatastoreName(storage);
			String type = client.getStorageInfo(node,storage).getType();
			instance.setDatastoreType(type);
		}catch (Exception e){
			e.printStackTrace();
		}
		instance.setInstanceUUID(vm.getVmId()+"-"+vm.getName());
		instance.setLocalIP(localIp);
		instance.setDescription(description);
		instance.setInstanceId(vm.getVmId());
		instance.setHost(node);
		instance.setCpu(vm.getCores());
		String instanceTypeDescription = "unknown";
		if(Math.round(vm.getMaxmem()/(1024*1024*1024))<1){
			instanceTypeDescription = vm.getCpus() + "核 " + (int)Math.round(vm.getMaxmem()/(1024*1024)) + "M";
		}else {
			instanceTypeDescription = vm.getCpus() + "核 " + (int)Math.rint(vm.getMaxmem()/(1024*1024*1024)) + "G";
		}
		try {
			HashMap<String,String> map = client.getClusterStatus();
			String clusterName = map.get("name");
			instance.setCluster(clusterName);
		}catch (Exception e){
			e.printStackTrace();
		}
		instance.setInstanceTypeDescription(instanceTypeDescription);
		instance.setInstanceType(instanceTypeDescription);
		instance.setDisk(vm.getMaxdisk());
		instance.setCpuUsed(cpuUserd);
		instance.setDataCenter("无限极中心机房");
		instance.setMemoryUsed((int)(vm.getMem()/(1024*1024)));
		instance.setMemory((int)Math.rint(vm.getMemory()/(1024)));
		instance.setDisk(Math.round(vm.getDisk()));
		instance.setDiskUsed((int) vm.getDisk());
		String name = vm.getName();
		if (name == null || name.trim().length() == 0) {
			name = "";
		}
		if (name.length() > 64) {
			name = name.substring(0, 61) + "...";
		}
		instance.setName(name);
		instance.setOs(vm.getOstype());
		instance.setResourceId(vm.getName());
		return instance;
	}

	public static int getRunningVmNumber(Pve2Api client,String node){
		int running = 0;
		try {
			List<VmQemu> vmQemuList = client.getQemuVMs(node);
			for(int j=0;j<vmQemuList.size();j++){
				VmQemu vmQemu = vmQemuList.get(j);
				String status = vmQemu.getStatus();
				if(status.equals("running")){
					running++;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return running;
	}

	public static int getVmTotal(Pve2Api client,String node){
		int total =0;
		try {
			List<VmQemu> vmList = client.getQemuVMs(node);
			if (vmList != null && vmList.size() > 0){
				for (VmQemu vm:vmList){
					client.getQemuConfig(node,Integer.parseInt(vm.getVmId()),vm);
					F2CInstance instance = ProxmoxUtil.toF2CInstance(vm,client,node);
					if(instance != null){
						total++;
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return total;
	}

	public static F2CImage toF2CImage(Pve2Api client,VmQemu vm,String node) {
		if (vm == null) {
			return null;
		}
		if (!vm.getImageId().equals("1")){
			return null;
		}
		String description ="";
		F2CImage f2CImage = new F2CImage();
		f2CImage.setId(vm.getVmId());
		String name = vm.getName();
		if (name == null || name.trim().length() == 0) {
			name = "";
		}
		if (name.length() > 64) {
			name = name.substring(0, 61) + "...";
		}
		try {
			description = client.getQemuConfig(node,Integer.parseInt(vm.getVmId()));
		}catch (Exception e){
		}
		f2CImage.setDescription(description);
		f2CImage.setName(name);
		f2CImage.setOs(vm.getOstype());
		return f2CImage;
	}

	public static F2CImage toF2CImage(VmQemu vm) {
		if (vm == null) {
			return null;
		}
		if (!vm.getImageId().equals("1")){
			return null;
		}
		F2CImage f2CImage = new F2CImage();
		f2CImage.setId(vm.getVmId());
		String name = vm.getName();
		if (name == null || name.trim().length() == 0) {
			name = "";
		}
		if (name.length() > 64) {
			name = name.substring(0, 61) + "...";
		}
		f2CImage.setName(name);
		f2CImage.setOs(vm.getOstype());
		return f2CImage;
	}

//	public static String getStatus(String powerState) {
//		if (powerState != null) {
//			if ("poweredOff".equals(powerState) || "suspended".equals(powerState)) {
//				return "Stopped";
//			} else if ("poweredOn".equals(powerState)) {
//				return "Running";
//			}
//		}
//		return "Unknown";
//	}
}
