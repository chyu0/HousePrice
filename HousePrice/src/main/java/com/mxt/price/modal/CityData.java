package com.mxt.price.modal;

import java.io.Serializable;
import java.util.List;

public class CityData implements Serializable {

	private static final long serialVersionUID = -7302493288844961102L;

	private String city;
	
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
