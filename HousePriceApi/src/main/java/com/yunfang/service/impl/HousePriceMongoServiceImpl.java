package com.yunfang.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.yunfang.dao.HousePriceMongoDao;
import com.yunfang.modal.HousePriceMongo;
import com.yunfang.mq.HousePriceProductor;
import com.yunfang.service.HousePriceMongoService;
import com.yunfang.utils.DateUtils;

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
	
	@Resource
	private HousePriceProductor housePriceProductor;
	
	@Override
	public void save(HousePriceMongo housePrice) {
		housePriceMongoDao.save(housePrice);
		housePriceProductor.sendMessage(housePrice);
	}

	@Override
	public List<HousePriceMongo> findHousePricesByCityAndDate(String city, String startTime , String endTime) {
		List<String> timeList = DateUtils.getMonthBetween(startTime, endTime);
		//Criteria criteria = Criteria.where("provinces").elemMatch(Criteria.where("citys").elemMatch(Criteria.where("city").is(city))).and("date").in(timeList.toArray());
		Criteria criteria = Criteria.where("city").is(city).and("date").in(timeList.toArray());
		Query query = new Query();
		query.addCriteria(criteria);
		return housePriceMongoDao.queryList(query);
	}

	@Override
	public void updateInser(HousePriceMongo housePrice) {
		//通过province，city，date查询记录
		Criteria criteria = Criteria.where("province").is(housePrice.getProvince()).and("city").is(housePrice.getCity()).and("date").in(housePrice.getDate());
		Query query = new Query();
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("districts", housePrice.getDistricts());
		housePriceMongoDao.upset(query, update);	
		housePriceProductor.sendMessage(housePrice);
	}

	@Override
	public List<HousePriceMongo> findHousePricesByDate(String date) {
		Criteria criteria = Criteria.where("date").is(date);
		Query query = new Query();
		query.addCriteria(criteria);
		return  housePriceMongoDao.queryList(query);
	}

	@Override
	public List<HousePriceMongo> findHousePricesByDist(String city ,String district) {
		Criteria criteria = Criteria.where("districts").elemMatch(Criteria.where("district").is(district));
		Query query = new Query();
		query.addCriteria(criteria);
		return  housePriceMongoDao.queryList(query);
	}
	
	@Override
	public HousePriceMongo findHousePricesByDateAndDist(String date ,String province, String city , String district){
		Criteria criteria = Criteria.where("date").is(date).and("province").is(province).and("city").is(city).and("districts").elemMatch(Criteria.where("district").is(district));
		Query query = new Query();
		query.addCriteria(criteria);
		return  housePriceMongoDao.queryOne(query);
	}

	@Override
	public HousePriceMongo findHousePricesByDateAndCity(String date, String province, String city) {
		Criteria criteria = Criteria.where("date").is(date).and("province").is(province).and("city").is(city);
		Query query = new Query();
		query.addCriteria(criteria);
		return  housePriceMongoDao.queryOne(query);
	}

	@Override
	public List<HousePriceMongo> findHousePricesByStartTimeAndEndTime(String startTime, String endTime) {
		List<String> timeList = DateUtils.getMonthBetween(startTime, endTime);
		Criteria criteria = Criteria.where("date").in(timeList.toArray());
		Query query = new Query();
		query.addCriteria(criteria);
		return housePriceMongoDao.queryList(query);
	}

	@Override
	public void updateMulti(HousePriceMongo housePrice) {
		//通过province，city，date查询记录
		Criteria criteria = Criteria.where("province").is(housePrice.getProvince()).and("city").is(housePrice.getCity()).and("date").in(housePrice.getDate());
		Query query = new Query();
		query.addCriteria(criteria);
		Update update = new Update();
		update.set("districts", housePrice.getDistricts());
		housePriceMongoDao.updateMulti(query, update);
	}

	@Override
	public List<HousePriceMongo> findHousePricesByDateListAndDist(String startTime, String endTime, String city, String district) {
		List<String> dateList = DateUtils.getMonthBetween(startTime, endTime);
		Criteria criteria = Criteria.where("city").is(city).and("date").in(dateList).and("districts").elemMatch(Criteria.where("district").is(district));;
		Query query = new Query();
		query.addCriteria(criteria);
		return housePriceMongoDao.queryList(query);
	}
}
