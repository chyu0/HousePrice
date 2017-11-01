package com.mxt.price.dao.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.stereotype.Component;

import com.mxt.price.modal.redis.HousePriceRedis;
import com.mxt.price.template.RedisGeneratorTemplate;
import com.mxt.price.utils.ProtoStuffSerializerUtils;

@Component
public class StringRedisDao extends RedisGeneratorTemplate<String>{
	
	/**
	 * 存放某日期所有城市平均价的排名，从配置文件中读取
	 */
	@Value("${zprice_rise_date_name}")
	private final String zPriceRiseDateRank = null;
	
	/**
	 * 查询集合结束，读取配置文件数据，100会无效
	 */
	@Value("${zprice_rise_date_limit}")
	private final int priceRiseDateLimit = 100;
	
	/**
	 * 添加元素至zPriceRiseDateRank
	 * @param price
	 */
	public void addPriceRiseRankByDate(HousePriceRedis price){
		String key = zPriceRiseDateRank + ":" + price.getProvince() + ":" + price.getCity() + ":" + price.getDate();
		super.zAddRemMinScore(key, priceRiseDateLimit, price.getDistrict(), price.getBaseData().getAvgPriceRise().doubleValue());
		super.setExpire(key , super.CACHEDAY);//只保存一天
	}
	
	/**
	 * 通过月份，获取市所有区县涨幅的排名
	 * @param date
	 */
	public List<String> getPriceRiseRankByDate(String province , String city ,String date){
		String key = zPriceRiseDateRank + ":" + province + ":" + city + ":" + date;
		return super.zRevRange(key, 0, priceRiseDateLimit);
	}
	
	/**
	 * 通过日期列表，求某市区所有区县涨幅的并集
	 * @param dates
	 * @return
	 */
	public List<Map<String,Object>> getPriceRiseByCityAndDate(String province ,String city ,List<String> dates){
		String key = "zPriceRiseUnion:" + province + city + dates.toString() ;
		List<String> keys = new ArrayList<String>();
		for(String date : dates){
			keys.add(zPriceRiseDateRank + ":" + province + ":" + city + ":" + date);
		}
		super.zUnionStore(key, keys);//分值相加求并集
		Set<Tuple> set = super.zRangeWithScores(key, 0, -1);
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(Tuple t : set){
			Map<String,Object> tuple = new HashMap<String,Object>();
			tuple.put("score", t.getScore());
			tuple.put("value", ProtoStuffSerializerUtils.deserialize(t.getValue(), entityClass));
			result.add(tuple);
		}
		super.setExpire(key, 0);//删除
		return result;
	}

}
