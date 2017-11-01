package com.mxt.price.service;

import java.util.List;
import java.util.Map;

import com.mxt.price.modal.redis.HousePriceRedis;

/**
 * @author maoxiaotai
 * @data 2017年10月24日 下午6:44:37
 * @Description TODO
 */
public interface HousePriceRedisService {
	
	/**
	 * 按照日期添加元素，分值为均价，集合名如zAvgDateRank:2017-01
	 * @param housePrice
	 */
	public void addAvgRankByDate(HousePriceRedis housePrice);
	
	
	/**
	 * 通过日期获取均价排名，数量从配置中读取zavg_price_date_limit
	 * @param date
	 * @return
	 */
	public List<HousePriceRedis> getAvgRankByDate(String date);
	
	/**
	 * 按照省市区县添加元素，分值为均价，集合名如zAvgDistRank:湖北省:武汉市:江夏区
	 * @param housePrice
	 */
	public void addAvgRankByDist(HousePriceRedis housePrice);
	
	/**
	 * 通过省，市，区县获取均价排名，数量从配置中读取zavg_price_dist_limit
	 * @param date
	 * @return
	 */
	public List<HousePriceRedis> getAvgRankByDist(String privince, String city ,String district);
	
	/**
	 * 按照日期添加元素，分值为涨幅，集合名如zPriceRiskDateRank:湖北省:武汉市:2017-01
	 * @param price
	 */
	public void addPriceRiseRankByDate(HousePriceRedis price);
	
	/**
	 * 通过日期列表获取涨幅排名，数量从配置中读取zprice_rise_date_name
	 * @param date
	 * @return
	 */
	public List<Map<String,Object>> getPriceRiseRankByCityAndDate(String province, String city ,List<String> dateList);
	
	/**
	 * 通过日期获取涨幅排名，数量从配置中读取zprice_rise_date_name
	 * @param province
	 * @param city
	 * @param date
	 * @return
	 */
	public List<String> getPriceRiseRankByDate(String province, String city ,String date);
	
	/**
	 * 按照省市区县添加元素，分值为均价，集合名如zPriceRiseDistRank:湖北省:武汉市:江夏区
	 * @param housePrice
	 */
	public void addPriceRiseRankByDist(HousePriceRedis price);
	
	/**
	 * 通过省，市，区县获取均价排名，数量从配置中读取zprice_rise_dist_limit
	 * @param date
	 * @return
	 */
	public List<HousePriceRedis> getPriceRiseRankByDist(String province ,String city ,String district);
	
	
}
