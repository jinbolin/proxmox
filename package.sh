#!/bin/bash
mkdir -p /opt/fit2cloud/plugins
cd fit2cloud-plugin-sdk
mvn clean install -q -Dmaven.test.skip=true

plugins=("fit2cloud-aws-plugin" "fit2cloud-azure-plugin" "fit2cloud-aliyun-plugin" "fit2cloud-openstackV1-plugin" "fit2cloud-openstackV2-plugin" "fit2cloud-qcloud-plugin" "fit2cloud-qingcloud-enterprise-plugin" "fit2cloud-qingcloud-plugin" "fit2cloud-ucloud-plugin" "fit2cloud-proxmox-plugin" "fit2cloud-vsphere55-plugin" "fit2cloud-vsphere60-plugin" "fit2cloud-vsphere-custom-plugin" "fit2cloud-vcloud-plugin" "fit2cloud-ksyun-plugin" "fit2cloud-fusioncompute-plugin" )
for s in ${plugins[@]}; do
	echo "start to build plugin " ${s}
	cd ../${s}
    mvn clean package -q -Dmaven.test.skip=true
    echo "start to copy plugin " ${s}
    if [ ${s} == "fit2cloud-azure-plugin" ];then
	cp -f target/${s}-0.2.jar /opt/fit2cloud/plugins/${s}-0.2-jar-with-dependencies.jar
   else
	cp -f target/${s}-0.2-jar-with-dependencies.jar /opt/fit2cloud/plugins/${s}-0.2-jar-with-dependencies.jar
    fi
done
