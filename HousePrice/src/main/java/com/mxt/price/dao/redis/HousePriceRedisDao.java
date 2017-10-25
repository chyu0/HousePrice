package com.mxt.price.dao.redis;

import org.springframework.stereotype.Component;

import com.mxt.price.modal.HousePrice2;
import com.mxt.price.template.RedisGeneratorTemplate;

/**
 * @author maoxiaotai
 * @data 2017年10月24日 下午6:39:57
 * @Description TODO
 */
@Component
public class HousePriceRedisDao extends RedisGeneratorTemplate<HousePrice2>{
	
	public void put(String key , HousePrice2 housePrice){
		super.putCache(key , housePrice);
	}
	
	public Long lPush(String key , HousePrice2 housePriceList){
		return super.lPush(key , housePriceList);
	}
	
	public HousePrice2 lPop(String key){
		return super.lPop(key);
	}
	
	public Long lRem(String key , long count ,HousePrice2 value){
		return super.lRem(key, count, value);
	}
	
}
