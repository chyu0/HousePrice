package com.mxt.price.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mxt.price.dao.HousePrice2MongoDao;
import com.mxt.price.dao.HousePriceMongoDao;
import com.mxt.price.modal.BaseData;
import com.mxt.price.modal.CityData;
import com.mxt.price.modal.DistrictData;
import com.mxt.price.modal.HousePrice2;
import com.mxt.price.modal.PrivinceData;
import com.mxt.price.service.HousePriceMongoService;
import com.mxt.price.utils.DateUtils;

/**
 * HousePriceMongoServiceImpl
 * @author maoxiaotai
 * @data 2017年10月20日 下午4:10:36
 * @Description service实现
 */
@Service
public class HousePriceMongoServiceImpl implements HousePriceMongoService {
	
	@Resource
	private HousePriceMongoDao housePriceMongoDao;
	
	@Resource
	private HousePrice2MongoDao housePrice2MongoDao;

	@Override
	public void save() {
		/*HousePrice housePrice = new HousePrice();
		housePrice.setDate("2017-01-10");
		housePrice.setProvince("湖北省");
		housePrice.setCity("武汉市");
		housePrice.setDistrict("江夏区");
		housePrice.setMaxPrice(new BigDecimal(20000.0d));
		housePrice.setMinPrice(new BigDecimal(10000.0d));
		housePrice.setAvgPrice(new BigDecimal(15000.0d));
		housePriceMongoDao.save(housePrice);*/
		
		HousePrice2 housePrice2 = new HousePrice2();
		BaseData baseData = new BaseData();
		baseData.setMaxPrice(new BigDecimal(20000.0d));
		baseData.setMinPrice(new BigDecimal(10000.0d));
		baseData.setAvgPrice(new BigDecimal(15000.0d));
		
		DistrictData districtData = new DistrictData();
		districtData.setDistrict("江夏区");
		districtData.setBaseData(baseData);
		
		CityData cityData = new CityData();
		cityData.setCity("武汉市");
		cityData.setDistricts(Arrays.asList(new DistrictData[]{districtData}));
		
		PrivinceData privinceData = new PrivinceData();
		privinceData.setPrivince("湖北省");
		privinceData.setCitys(Arrays.asList(new CityData[]{cityData}));
		
		housePrice2.setDate("2017-01");
		housePrice2.setPrivinces(Arrays.asList(new PrivinceData[]{privinceData}));
		
		housePrice2MongoDao.save(housePrice2);
	}

	@Override
	public List<HousePrice2> findHousePrice() {
		//查找所有2017-01或2017-01-04月份，武汉市的数据
		//db.getCollection('HousePrice2').find({privinces:{$elemMatch:{citys:{$elemMatch:{city:"武汉市"}}}}})
		//Criteria.where("district").is("江夏区");
		Criteria criteria = Criteria.where("privinces").elemMatch(Criteria.where("citys").elemMatch(Criteria.where("city").is("武汉市"))).and("date").in("2017-01","2017-04");
		Query query = new Query();
		query.addCriteria(criteria);
		List<HousePrice2> housePrices = housePrice2MongoDao.find(query);
		return housePrices;
	}

	@Override
	public void save(HousePrice2 housePrice2) {
		housePrice2MongoDao.save(housePrice2);
	}

	@Override
	public List<HousePrice2> findHousePriceByCityAndDate(String city,
			String startTime , String endTime) {
		List<String> timeList = DateUtils.getMonthBetween(startTime, endTime);
		Criteria criteria = Criteria.where("privinces").elemMatch(Criteria.where("citys").elemMatch(Criteria.where("city").is(city))).and("date").in(timeList.toArray());
		Query query = new Query();
		query.addCriteria(criteria);
		return housePrice2MongoDao.find(query);
	}

}
