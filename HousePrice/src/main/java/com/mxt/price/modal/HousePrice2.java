package com.mxt.price.modal;

import java.io.Serializable;
import java.util.List;

/**
 * @author maoxiaotai
 * @data 2017年10月22日 下午9:37:31
 * @Description 房价趋势类
 */
public class HousePrice2 implements Serializable {

	private static final long serialVersionUID = 8493299969403414774L;
	
	/**
	 * 主键id，默认自动生成，也可自己定义，这里不能设置成整型
	 */
	private String id;
	
	/**
	 * 日期，String类型，如2017-01
	 */
	private String date;
	
	
	/**
	 * 省列表，方便拓展
	 */
	private List<PrivinceData> privinces;


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


	public List<PrivinceData> getPrivinces() {
		return privinces;
	}


	public void setPrivinces(List<PrivinceData> privinces) {
		this.privinces = privinces;
	}
}
