package com.mxt.price.dao.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mxt.price.modal.redis.HousePriceRedis;
import com.mxt.price.template.RedisGeneratorTemplate;

/**
 * @author maoxiaotai
 * @data 2017年10月24日 下午6:39:57
 * @Description TODO
 */
@Component
public class HousePriceRedisDao extends RedisGeneratorTemplate<HousePriceRedis>{
	
	/**
	 * 存放某日期所有城市平均价的排名，从配置文件中读取
	 */
	@Value("${zavg_price_date_name}")
	private final String zAvgDateRank = null;
	
	/**
	 * 存放某区县所有月份平均价的排名，从配置文件中读取
	 */
	@Value("${zavg_price_dist_name}")
	private final String zAvgDistRank = null;
	
	/**
	 * 存放某区县所有月份涨幅的排名，从配置文件中读取
	 */
	@Value("${zprice_risk_dist_name}")
	private final String zPriceRiskDistRank = null;
	
	/**
	 * 存放某日期所有城市涨幅的排名，从配置文件中读取
	 */
	@Value("${zprice_risk_date_name}")
	private final String zPriceRiskDateRank = null;
	
	/**
	 * 查询集合结束，读取配置文件数据，100会无效
	 */
	@Value("${zavg_price_date_limit}")
	private final int avgPriceDateLimit = 100;
	
	/**
	 * 查询集合结束，读取配置文件数据，100会无效
	 */
	@Value("${zavg_price_dist_limit}")
	private final int avgPriceDistLimit = 100;
	
	/**
	 * 查询集合结束，读取配置文件数据，100会无效
	 */
	@Value("${zprice_risk_dist_limit}")
	private final int priceRiskDistLimit = 100;
	
	/**
	 * 查询集合结束，读取配置文件数据，100会无效
	 */
	@Value("${zprice_risk_date_limit}")
	private final int priceRiskDateLimit = 100;
	
	/**
	 * 添加元素至zAvgDateRank
	 * @param price
	 */
	public void addAvgRankByDate(HousePriceRedis price){
		String key = zAvgDateRank + ":" + price.getDate();
		super.zAddRemMinScore(key, avgPriceDateLimit, price, price.getBaseData().getAvgPrice().doubleValue());
	}
	
	/**
	 * 通过月份，获取所有区县均价的排名
	 * @param date
	 */
	public List<HousePriceRedis> getAvgRankByDate(String date){
		String key = zAvgDateRank + ":" + date;
		return super.zRevRange(key, 0, avgPriceDateLimit);
	}
	
	/**
	 * 添加元素至zAvgDistRank
	 * @param price
	 */
	public void addAvgRankByDist(HousePriceRedis price){
		String key = zAvgDistRank + ":" + price.getProvince() + ":" + price.getCity() + ":" + price.getDistrict();
		super.zAddRemMinScore(key, avgPriceDateLimit, price, price.getBaseData().getAvgPrice().doubleValue());
	}
	
	/**
	 * 通过省，市，区县名，获取所有月份中均价的排名
	 * @param province
	 * @param city
	 * @param district
	 * @return
	 */
	public List<HousePriceRedis> getAvgRankByDistrict(String province ,String city ,String district){
		String key = zAvgDistRank + ":" + province + ":" + city + ":" + district;
		return super.zRevRange(key, 0, avgPriceDistLimit);
	}
	
	
	/**
	 * 添加元素至zPriceRiskDateRank
	 * @param price
	 */
	public void addPriceRiskRankByDate(HousePriceRedis price){
		String key = zPriceRiskDateRank + ":" + price.getDate();
		super.zAddRemMinScore(key, priceRiskDateLimit, price, price.getBaseData().getAvgPriceRise().doubleValue());
	}
	
	/**
	 * 通过月份，获取所有区县涨幅的排名
	 * @param date
	 */
	public List<HousePriceRedis> getPriceRiskRankByDate(String date){
		String key = zPriceRiskDateRank + ":" + date;
		return super.zRevRange(key, 0, priceRiskDateLimit);
	}
	
	/**
	 * 添加元素至zAvgDistRank
	 * @param price
	 */
	public void addPriceRiskRankByDist(HousePriceRedis price){
		String key = zPriceRiskDistRank + ":" + price.getProvince() + ":" + price.getCity() + ":" + price.getDistrict();
		super.zAddRemMinScore(key, priceRiskDateLimit, price, price.getBaseData().getAvgPriceRise().doubleValue());
	}
	
	/**
	 * 通过省，市，区县名，获取所有月份中涨幅的排名
	 * @param province
	 * @param city
	 * @param district
	 * @return
	 */
	public List<HousePriceRedis> getPriceRiskRankByDist(String province ,String city ,String district){
		String key = zPriceRiskDistRank + ":" + province + ":" + city + ":" + district;
		return super.zRevRange(key, 0, priceRiskDateLimit);
	}
	
}
