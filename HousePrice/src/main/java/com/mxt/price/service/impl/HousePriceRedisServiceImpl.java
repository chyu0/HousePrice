package com.mxt.price.service.impl;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mxt.price.dao.redis.HousePriceRedisDao;
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
	public void addPriceRiskRankByDate(HousePriceRedis price) {
		housePriceRedisDao.addPriceRiskRankByDate(price);
	}

	@Override
	public List<HousePriceRedis> getPriceRiskRankByDate(String date) {
		return housePriceRedisDao.getPriceRiskRankByDate(date);
	}

	@Override
	public void addPriceRiskRankByDist(HousePriceRedis price) {
		housePriceRedisDao.addPriceRiskRankByDist(price);
	}

	@Override
	public List<HousePriceRedis> getPriceRiskRankByDist(String province, String city, String district) {
		return housePriceRedisDao.getPriceRiskRankByDist(province, city, district);
	}
	
}
