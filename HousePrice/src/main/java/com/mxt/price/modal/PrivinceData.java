package com.mxt.price.modal;

import java.io.Serializable;
import java.util.List;

public class PrivinceData implements Serializable{

	private static final long serialVersionUID = -3762395269397227175L;
	
	private String privince;
	
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
