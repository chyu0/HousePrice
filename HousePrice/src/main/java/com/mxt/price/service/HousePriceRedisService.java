package com.mxt.price.service;

import com.mxt.price.modal.HousePrice2;

/**
 * @author maoxiaotai
 * @data 2017年10月24日 下午6:44:37
 * @Description TODO
 */
public interface HousePriceRedisService {

	public void save();
	
	public Long lpush();
	
	public HousePrice2 lpop();
	
	public Long lRem(long count);
}
