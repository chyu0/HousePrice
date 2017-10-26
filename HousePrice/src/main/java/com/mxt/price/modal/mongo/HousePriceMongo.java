package com.mxt.price.modal.mongo;

import java.io.Serializable;
import java.util.List;

/**
 * HousePrice实体类
 * @author maoxiaotai
 * @data 2017年10月20日 下午3:42:04
 * @Description TODO
 */
public class HousePriceMongo implements Serializable{

	private static final long serialVersionUID = 8086185709396153934L;
	
	/**
	 * 主键id，默认自动生成，也可自己定义，这里不能设置成整型
	 */
	private String id;
	
	/**
	 * 日期，String类型，如2017-01
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
	 * 区县数据列表
	 */
	List<DistrictDataMongo> districts;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public List<DistrictDataMongo> getDistricts() {
		return districts;
	}

	public void setDistricts(List<DistrictDataMongo> districts) {
		this.districts = districts;
	}
}
