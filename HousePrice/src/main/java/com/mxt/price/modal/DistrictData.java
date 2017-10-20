package com.mxt.price.modal;

import java.io.Serializable;

public class DistrictData implements Serializable {

	private static final long serialVersionUID = 3386162519294372890L;
	
	private String district;
	
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
