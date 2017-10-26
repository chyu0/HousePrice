package com.mxt.price.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mxt.price.dao.mongo.HousePriceMongoDao;
import com.mxt.price.modal.mongo.HousePriceMongo;
import com.mxt.price.service.HousePriceMongoService;
import com.mxt.price.utils.DateUtils;

/**
 * HousePriceMongoServiceImpl
 * @author maoxiaotai
 * @data 2017年10月20日 下午4:10:36
 * @Description HousePriceMongoService实现
 */
@Service
public class HousePriceMongoServiceImpl implements HousePriceMongoService {
	
	@Resource
	private HousePriceMongoDao housePriceMongoDao;
	
	@Override
	public void save(HousePriceMongo housePrice) {
		housePriceMongoDao.save(housePrice);
	}

	@Override
	public List<HousePriceMongo> findHousePricesByCityAndDate(String city, String startTime , String endTime) {
		List<String> timeList = DateUtils.getMonthBetween(startTime, endTime);
		//Criteria criteria = Criteria.where("privinces").elemMatch(Criteria.where("citys").elemMatch(Criteria.where("city").is(city))).and("date").in(timeList.toArray());
		Criteria criteria = Criteria.where("city").is(city).and("date").in(timeList.toArray());
		Query query = new Query();
		query.addCriteria(criteria);
		return housePriceMongoDao.queryList(query);
	}

	@Override
	public void updateInser(HousePriceMongo housePrice) {
		//通过privince，city，date查询记录
		Criteria criteria = Criteria.where("privince").is(housePrice.getProvince()).and("city").is(housePrice.getCity()).and("date").in(housePrice.getDate());
		Query query = new Query();
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("districts", housePrice.getDistricts());
		housePriceMongoDao.upset(query, update);		
	}

	@Override
	public List<HousePriceMongo> findHousePricesByDate(String date) {
		Criteria criteria = Criteria.where("date").is(date);
		Query query = new Query();
		query.addCriteria(criteria);
		return  housePriceMongoDao.queryList(query);
	}

	@Override
	public List<HousePriceMongo> findHousePricesByDist(String district) {
		Criteria criteria = Criteria.where("districts").elemMatch(Criteria.where("district").is(district));
		Query query = new Query();
		query.addCriteria(criteria);
		return  housePriceMongoDao.queryList(query);
	}
}
