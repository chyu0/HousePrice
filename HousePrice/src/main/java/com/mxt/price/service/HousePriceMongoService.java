package com.mxt.price.service;

import java.util.List;

import com.mxt.price.modal.mongo.HousePriceMongo;

/**
 * @author maoxiaotai
 * @data 2017年10月20日 下午4:11:22
 * @Description HousePriceMongoService接口层
 */
public interface HousePriceMongoService {

	/**
	 * 保存数据
	 * @param housePrice2
	 */
	public void save(HousePriceMongo housePrice);
	
	/**
	 * 通过市和日期查询该市下所有房价数据的列表
	 * @param city
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<HousePriceMongo> findHousePricesByCityAndDate(String city , String startTime , String endTime);
	
	
	/**
	 * 由该条数据更新，没有该条数据时插入，通过日期，省，市查询
	 * @param housePrice
	 */
	public void updateInser(HousePriceMongo housePrice);
	
	
	/**
	 * 通过日期查询对应房价的列表
	 * @param date
	 * @return
	 */
	public List<HousePriceMongo> findHousePricesByDate(String date);
	
	/**
	 * 通过区县查询对应房价的列表
	 * @param date
	 * @return
	 */
	public List<HousePriceMongo> findHousePricesByDist(String distriction);
}
