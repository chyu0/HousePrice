package com.mxt.price.modal;

import java.io.Serializable;
import java.util.List;

/**
 * @author maoxiaotai
 * @data 2017年10月22日 下午9:34:55
 * @Description 市级实体类
 */
public class CityData implements Serializable {

	private static final long serialVersionUID = -7302493288844961102L;

	/**
	 * 市名
	 */
	private String city;
	
	/**
	 * 区县列表
	 */
	private List<DistrictData> districts;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<DistrictData> getDistricts() {
		return districts;
	}

	public void setDistricts(List<DistrictData> districts) {
		this.districts = districts;
	}
	
}
