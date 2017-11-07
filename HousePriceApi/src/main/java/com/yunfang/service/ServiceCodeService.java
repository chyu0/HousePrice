package com.yunfang.service;

import com.yunfang.modal.ServiceCode;

/**
 * serviceCode 主要用来获取code对应的接口方法
 * @author maoxiaotai
 * @data 2017年11月7日 下午5:46:12
 * @Description TODO
 */
public interface ServiceCodeService {

	public void addNewServiceCode(String code ,String serviceName ,String methodName);
	
	public ServiceCode getServiceByCode(String code);
}
