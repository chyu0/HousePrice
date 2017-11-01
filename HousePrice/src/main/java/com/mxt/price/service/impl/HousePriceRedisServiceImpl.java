package com.mxt.price.service.impl;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mxt.price.dao.redis.HousePriceRedisDao;
import com.mxt.price.dao.redis.StringRedisDao;
import com.mxt.price.modal.redis.HousePriceRedis;
import com.mxt.price.service.HousePriceRedisService;

/**
 * @author maoxiaotai
 * @data 2017年10月26日 下午5:46:30
 * @Description HousePriceRedisService实现层
 */
@Service
public class HousePriceRedisServiceImpl implements HousePriceRedisService {

	@Resource
	private HousePriceRedisDao housePriceRedisDao;
	
	@Resource
	private StringRedisDao stringRedisDao;

	@Override
	public void addAvgRankByDate(HousePriceRedis housePrice) {
		housePriceRedisDao.addAvgRankByDate(housePrice);
	}

	@Override
	public List<HousePriceRedis> getAvgRankByDate(String date) {
		return housePriceRedisDao.getAvgRankByDate(date);
	}

	@Override
	public void addAvgRankByDist(HousePriceRedis housePrice) {
		housePriceRedisDao.addAvgRankByDist(housePrice);
	}

	@Override
	public List<HousePriceRedis> getAvgRankByDist(String province, String city, String district) {
		return housePriceRedisDao.getAvgRankByDistrict(province, city, district);
	}

	@Override
	public void addPriceRiseRankByDate(HousePriceRedis price) {
		stringRedisDao.addPriceRiseRankByDate(price);
	}

	@Override
	public List<String> getPriceRiseRankByDate(String province, String city ,String date) {
		return stringRedisDao.getPriceRiseRankByDate(province, city, date);
	}
	
	@Override
	public List<Map<String,Object>> getPriceRiseRankByCityAndDate(String province, String city ,List<String> dateList) {
		return stringRedisDao.getPriceRiseByCityAndDate(province, city, dateList);
	}

	@Override
	public void addPriceRiseRankByDist(HousePriceRedis price) {
		housePriceRedisDao.addPriceRiseRankByDist(price);
	}

	@Override
	public List<HousePriceRedis> getPriceRiseRankByDist(String province, String city, String district) {
		return housePriceRedisDao.getPriceRiseRankByDist(province, city, district);
	}
	
}
