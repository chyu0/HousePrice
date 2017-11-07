package com.yunfang.modal;

import java.io.Serializable;
import java.util.List;

/**
 * 密匙映射
 * @author 毛小苔
 * @data 2017年11月2日 上午10:19:03
 * @Description TODO
 */
public class PublicKey implements Serializable{

	private static final long serialVersionUID = 9219036208954664499L;

	private String publicKey;
	
	private List<String> serviceCodes;

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public List<String> getServiceCodes() {
		return serviceCodes;
	}

	public void setServiceCodes(List<String> serviceCodes) {
		this.serviceCodes = serviceCodes;
	}

}
