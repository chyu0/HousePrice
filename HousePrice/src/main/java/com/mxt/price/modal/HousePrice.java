package com.mxt.price.modal;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * HousePrice实体类
 * @author maoxiaotai
 * @data 2017年10月20日 下午3:42:04
 * @Description TODO
 */
public class HousePrice implements Serializable{

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
	 * 区
	 */
	private String district;
	
	/**
	 * 最高价，BigDecimal类型，方便计算
	 */
	private BigDecimal maxPrice;
	
	/**
	 * 最低价，BigDecimal类型，方便计算
	 */
	private BigDecimal minPrice;
	
	/**
	 * 平均价，BigDecimal类型，方便计算
	 */
	private BigDecimal avgPrice;
	

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

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	public BigDecimal getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	public BigDecimal getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(BigDecimal avgPrice) {
		this.avgPrice = avgPrice;
	}
	
}
