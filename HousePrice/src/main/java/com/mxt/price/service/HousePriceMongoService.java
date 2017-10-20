package com.mxt.price.service;

import java.util.List;

import com.mxt.price.modal.HousePrice2;

/**
 * @author maoxiaotai
 * @data 2017年10月20日 下午4:11:22
 * @Description HousePriceMongoService接口层
 */
public interface HousePriceMongoService {

	public void save();
	
	public List<HousePrice2> findHousePrice();
}
