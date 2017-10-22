package com.mxt.price.modal;

import java.io.Serializable;

/**
 * @author maoxiaotai
 * @data 2017年10月22日 下午9:36:27
 * @Description 区县实体类
 */
public class DistrictData implements Serializable {

	private static final long serialVersionUID = 3386162519294372890L;
	
	/**
	 * 区县名
	 */
	private String district;
	
	/**
	 * 基础数据，包括平均值等基本数据
	 */
	private BaseData baseData;

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public BaseData getBaseData() {
		return baseData;
	}

	public void setBaseData(BaseData baseData) {
		this.baseData = baseData;
	}
	
	
	
}
