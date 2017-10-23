package com.mxt.price.service;

import java.util.List;

import com.mxt.price.modal.HousePrice2;

/**
 * @author maoxiaotai
 * @data 2017年10月20日 下午4:11:22
 * @Description HousePriceMongoService接口层
 */
public interface HousePriceMongoService {

	/**
	 * 保存数据测试
	 */
	public void save();
	
	/**
	 * 保存数据
	 * @param housePrice2
	 */
	public void save(HousePrice2 housePrice2);
	
	/**
	 * 数据查询测试
	 * @return 房价趋势列表
	 */
	public List<HousePrice2> findHousePrice();
	
	/**
	 * 
	 */
	public List<HousePrice2> findHousePriceByCityAndDate(String city , String startTime , String endTime);
}
