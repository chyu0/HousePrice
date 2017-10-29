package com.mxt.price.dao.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.mxt.price.template.RedisGeneratorTemplate;

public class StringRedisDao extends RedisGeneratorTemplate<String>{
	
	/**
	 * 存放某日期所有城市平均价的排名，从配置文件中读取
	 */
	@Value("${zavg_price_dist_name}")
	private final String zAvgDistRank = null;
	
	/**
	 * 查询集合结束，读取配置文件数据，100会无效
	 */
	@Value("${zavg_price_dist_limit}")
	private final int avgPriceDistLimit = 100;
	
	/**
	 * 通过月份，获取所有区县均价的排名
	 * @param date
	 */
	public List<String> getAvgRankByDate(String district){
		String key = zAvgDistRank + ":" + district;
		return super.zRevRange(key, 0, avgPriceDistLimit);
	}
	
	

}
