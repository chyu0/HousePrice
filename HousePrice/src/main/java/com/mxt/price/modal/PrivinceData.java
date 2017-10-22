package com.mxt.price.modal;

import java.io.Serializable;
import java.util.List;

/**
 * @author maoxiaotai
 * @data 2017年10月22日 下午9:37:50
 * @Description 省级实体类
 */
public class PrivinceData implements Serializable{

	private static final long serialVersionUID = -3762395269397227175L;
	
	/**
	 * 省份名称
	 */
	private String privince;
	
	/**
	 * 市列表
	 */
	private List<CityData> citys;

	public String getPrivince() {
		return privince;
	}

	public void setPrivince(String privince) {
		this.privince = privince;
	}

	public List<CityData> getCitys() {
		return citys;
	}

	public void setCitys(List<CityData> citys) {
		this.citys = citys;
	}
	
	

}
