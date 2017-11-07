package com.yunfang.modal;

import java.io.Serializable;

/**
 * 服务码
 * @author maoxiaotai
 * @data 2017年11月7日 下午5:51:24
 * @Description TODO
 */
public class ServiceCode implements Serializable {

	private static final long serialVersionUID = 1068663440685801682L;
	
	/**
	 * service bean 名称
	 */
	private String serviceName;
	
	/**
	 * service 方法名称
	 */
	private String methodName;
	
	/**
	 * service 编码
	 */
	private String code;
	

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
