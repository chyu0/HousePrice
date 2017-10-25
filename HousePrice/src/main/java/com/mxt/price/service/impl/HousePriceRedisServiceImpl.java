package com.mxt.price.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mxt.price.dao.redis.HousePriceRedisDao;
import com.mxt.price.modal.BaseData;
import com.mxt.price.modal.CityData;
import com.mxt.price.modal.DistrictData;
import com.mxt.price.modal.HousePrice2;
import com.mxt.price.modal.PrivinceData;
import com.mxt.price.service.HousePriceRedisService;

@Service
public class HousePriceRedisServiceImpl implements HousePriceRedisService {

	@Resource
	private HousePriceRedisDao housePriceRedisDao;
	
	@Override
	public void save() {
		HousePrice2 housePrice2 = new HousePrice2();
		BaseData baseData = new BaseData();
		baseData.setMaxPrice(new BigDecimal(30000.0d));
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
		
		housePriceRedisDao.put(housePrice2.getClass().getSimpleName(), housePrice2);
	}

	@Override
	public Long lpush() {
		HousePrice2 housePrice2 = new HousePrice2();
		BaseData baseData = new BaseData();
		baseData.setMaxPrice(new BigDecimal(30000.0d));
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
		
		return housePriceRedisDao.lPush("housePriceList", housePrice2);
	}

	@Override
	public HousePrice2 lpop() {
		return housePriceRedisDao.lPop("housePriceList");
	}

	@Override
	public Long lRem(long count) {
		HousePrice2 housePrice2 = new HousePrice2();
		BaseData baseData = new BaseData();
		baseData.setMaxPrice(new BigDecimal(30000.0d));
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
		return housePriceRedisDao.lRem("housePriceList", count, housePrice2);
	}
	
}
