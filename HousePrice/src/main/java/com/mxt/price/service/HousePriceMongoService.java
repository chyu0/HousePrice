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
	 * 更新满足条件的所有记录
	 * @param housePrice
	 */
	public void updateMulti(HousePriceMongo housePrice);
	
	
	/**
	 * 通过日期查询对应房价的列表
	 * @param date
	 * @return
	 */
	public List<HousePriceMongo> findHousePricesByDate(String date);
	
	/**
	 * 查询时间段内对应房价的列表
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<HousePriceMongo> findHousePricesByStartTimeAndEndTime(String startTime, String endTime);
	
	/**
	 * 通过省，市，区县查询对应房价的列表，考虑到区同名情况，有city查询效率也更高
	 * @param date
	 * @return
	 */
	public List<HousePriceMongo> findHousePricesByDist(String city, String district);
	
	/**
	 * 通过省，市，区县和日期查询对应房价的列表，考虑到区同名情况，有province,city查询效率也更高
	 * @param date
	 * @param province
	 * @param city
	 * @param district
	 * @return
	 */
	public HousePriceMongo findHousePricesByDateAndDist(String date ,String province ,String city , String district);
	
	/**
	 * 通过省，市，和日期查询对应房价的列表，考虑到区同名情况，有province,city查询效率也更高
	 * @param date
	 * @param province
	 * @param city
	 * @return
	 */
	public HousePriceMongo findHousePricesByDateAndCity(String date ,String province ,String city);
}
