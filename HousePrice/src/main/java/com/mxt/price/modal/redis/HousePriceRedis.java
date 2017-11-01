package com.mxt.price.modal.redis;

import java.io.Serializable;

import com.mxt.price.modal.common.BaseData;

public class HousePriceRedis implements Serializable {

	private static final long serialVersionUID = -7458788831943199121L;
	
	/**
	 * 日期
	 */
	private String date;
	
	/**
	 * 省
	 */
	private String province;
	
	/**
	 * 市
	 */
	private String city;
	
	/**
	 * 区
	 */
	private String district;
	
	/**
	 * 基础数据
	 */
	private BaseData baseData;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

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
