package com.yunfang.service;

import java.util.Map;

/**
 * 房价查询api接口，对外
 * @author maoxiaotai
 * @data 2017年11月7日 下午5:33:18
 * @Description TODO
 */
public interface HousePriceApiService {

	/**
	 * 获取均价
	 * @param province_name
	 * @param city_name
	 * @param date
	 * @return
	 */
	public Map<String , Object> avgPrice(String province_name, String city_name, String date);
	
	/**
	 * 获取最高价和最低价
	 * @param province_name
	 * @param city_name
	 * @param date
	 * @return
	 */
	public Map<String , Object> maxMinPrice(String province_name, String city_name, String date);
	
}
