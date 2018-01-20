package com.yunfang.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yunfang.enums.ResultCode;
import com.yunfang.modal.DistrictDataMongo;
import com.yunfang.modal.HousePriceMongo;
import com.yunfang.service.HousePriceApiService;
import com.yunfang.service.HousePriceMongoService;

@Service("housePriceApiService")
public class HousePriceApiServiceImpl implements HousePriceApiService {
	
	private static Logger logger = LoggerFactory.getLogger(HousePriceApiServiceImpl.class);
	
	@Resource
	private HousePriceMongoService housePriceMongoService;

	@Override
	public Map<String , Object> avgPrice(String province_name, String city_name, String date) {
			Map<String , Object> map = new HashMap<String, Object>();
			
			if(StringUtils.isBlank(province_name) || StringUtils.isBlank(city_name) || StringUtils.isBlank(date)){
				map.put("code", ResultCode.REQUESTPARAMSERROR.getCode());
				map.put("msg", "请求参数不能为空");
				return map;
			}
			
			HousePriceMongo mongo = housePriceMongoService.findHousePricesByDateAndCity(date, province_name, city_name);
			
			logger.info("date:" + date + "province_name:" + province_name + "city_name:" + city_name + "mongo:" + mongo);
			
			if(mongo == null || mongo.getDistricts() == null || mongo.getDistricts().size() <= 0){
				map.put("code", ResultCode.NULLRESULT.getCode());
				map.put("msg", "未获取到相关数据");
				return map;
			}
			
			map.put("date", mongo.getDate());
			map.put("city_name", city_name);
			map.put("province_name", province_name);
			List<DistrictDataMongo> dists = mongo.getDistricts();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for(DistrictDataMongo dist : dists){
				Map<String,Object> d = new HashMap<String,Object>();
				d.put("district_name", dist.getDistrict());
				d.put("district_avg_price", dist.getBaseData().getAvgPrice());
				list.add(d);
			}
			map.put("districts", list);
			return map;
	}

	@Override
	public Map<String , Object> maxMinPrice(String province_name, String city_name, String date) {
		Map<String , Object> map = new HashMap<String, Object>();
		
		if(StringUtils.isBlank(province_name) || StringUtils.isBlank(city_name) || StringUtils.isBlank(date)){
			map.put("code", ResultCode.REQUESTPARAMSERROR.getCode());
			map.put("msg", "请求参数不能为空");
			return map;
		}
		
		HousePriceMongo mongo = housePriceMongoService.findHousePricesByDateAndCity(date, province_name, city_name);
		
		if(mongo == null || mongo.getDistricts() == null || mongo.getDistricts().size() <= 0){
			map.put("code", ResultCode.NULLRESULT.getCode());
			map.put("msg", "为获取到相关数据");
			return map;
		}
		
		map.put("date", mongo.getDate());
		map.put("city_name", city_name);
		map.put("province_name", province_name);
		List<DistrictDataMongo> dists = mongo.getDistricts();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(DistrictDataMongo dist : dists){
			Map<String,Object> d = new HashMap<String,Object>();
			d.put("district_name", dist.getDistrict());
			d.put("district_max_price", dist.getBaseData().getMaxPrice());
			d.put("district_min_price", dist.getBaseData().getMinPrice());
			list.add(d);
		}
		map.put("districts", list);
		return map;
	}

}
