package com.mxt.price.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mxt.price.modal.common.BaseData;
import com.mxt.price.modal.mongo.DistrictDataMongo;
import com.mxt.price.modal.mongo.HousePriceMongo;
import com.mxt.price.modal.redis.HousePriceRedis;
import com.mxt.price.service.HousePriceMongoService;
import com.mxt.price.service.HousePriceRedisService;
import com.mxt.price.utils.BigDecimalUtils;
import com.mxt.price.utils.CommonUtils;
import com.mxt.price.utils.DateUtils;
import com.mxt.price.utils.HttpRequestUtils;

/**
 * 数据初始化controller
 * @author maoxiaotai
 * @data 2017年10月31日 上午10:07:10
 */
@Controller
@RequestMapping("/init")
public class InitDataController extends BaseController{

	private static Logger logger = LoggerFactory.getLogger(InitDataController.class);
	
	@Resource
	private HousePriceMongoService housePriceMongoService;
	
	@Resource
	private HousePriceRedisService housePriceRedisService;
	
	@Value("${access_signature}")
	private String assign = null;
	
	
	@RequestMapping("/initData")
	@ResponseBody
	public Map<String,Object> initDataFromHttp(String startDate , String endDate , String province , String city){
		StringBuffer str = new StringBuffer();
		//url组装，调用云房接口，形如http://www.fangyun.com/price/avgPrice?time_stamp=1509610841808&access_signature=***&province_name=%E6%B9%96%E5%8C%97&city_name=%E6%AD%A6%E6%B1%89&date=2016-10
		str.append("http://www.fangyun.com/price/avgPrice?time_stamp=");
		Long timestamp = Calendar.getInstance().getTimeInMillis();
		str.append(timestamp).append("&access_signature=").append(assign);
		str.append("&province_name=").append(province).append("&city_name=").append(city);
		List<String> dateList = DateUtils.getMonthBetween(startDate, endDate);
		for(String date : dateList){
			String url = str.toString() + "&date=" + date;
			JSONObject jsonResult = HttpRequestUtils.httpGet(url);
			if(jsonResult.getIntValue("code") == 200){//接口调用成功，返回正确结果
				HousePriceMongo housePriceMongo = new HousePriceMongo();
				housePriceMongo.setCity(city);
				housePriceMongo.setDate(date);
				housePriceMongo.setProvince(province);
				List<DistrictDataMongo> distMongoList = new ArrayList<DistrictDataMongo>();
				JSONObject result = jsonResult.getJSONObject("result");//实际数据
				JSONArray districts = result.getJSONArray("districts");//省数据
				if(districts != null && districts.size() > 0){
					for(int index=0 ;index < districts.size() ;index++){
						JSONObject district = districts.getJSONObject(index);
						String avgPrice = district.getString("district_avg_price");
						String distName = district.getString("district_name");
						DistrictDataMongo distMongo = new DistrictDataMongo();
						BaseData baseData = new BaseData();
						baseData.setAvgPrice(new BigDecimal(avgPrice));
						distMongo.setDistrict(distName);
						distMongo.setBaseData(baseData);
						distMongoList.add(distMongo);
					}
				}
				housePriceMongo.setDistricts(distMongoList);
				housePriceMongoService.updateInser(housePriceMongo);
			}
		}
		return successResult();
	}
	
	/**
	 * 更新某时间段所有城市的平均房价的涨幅
	 * readExcel之后最好进行一次updateRise初始化涨幅数据
	 * @param model
	 * @param city
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping("/updateRise")
	@ResponseBody
	public Map<String,Object> updateAvgPriceRise(Model model ,String startTime ,String endTime){
		try{
			List<HousePriceMongo> housePriceList = housePriceMongoService.findHousePricesByStartTimeAndEndTime(startTime, endTime);
			Calendar cal = Calendar.getInstance();
			for(HousePriceMongo housePrice : housePriceList){
				cal.setTime(DateUtils.parse(housePrice.getDate(), DateUtils.DATE_TO_STRING_MONTH_PATTERN));
				cal.add(Calendar.MONTH, -1);
				String lastMonth = DateUtils.format(cal.getTime(), DateUtils.DATE_TO_STRING_MONTH_PATTERN);
				HousePriceMongo lastMonthPrice = housePriceMongoService.findHousePricesByDateAndCity(lastMonth, housePrice.getProvince(), housePrice.getCity());
				if(lastMonthPrice != null){
					List<DistrictDataMongo> distList = housePrice.getDistricts();
					List<DistrictDataMongo> lastDistList = lastMonthPrice.getDistricts();
					for(DistrictDataMongo dist : distList){
						for(DistrictDataMongo lastDist : lastDistList){
							if(dist.getDistrict().equals(lastDist.getDistrict())){
								BaseData baseData = dist.getBaseData();
								baseData.setAvgPriceRise(BigDecimalUtils.rise(lastDist.getBaseData().getAvgPrice(), baseData.getAvgPrice(), 6));
								dist.setBaseData(baseData);
								break;
							}
						}
					}
				}
				housePriceMongoService.updateMulti(housePrice);
			}
			return this.successResult();
		}catch(Exception e){
			logger.error("从Excel保存数据至MongoDB异常：" + CommonUtils.exceptionToStr(e));
			return this.failResult("更新某时间段所有城市的平均房价的涨幅失败");
		}
	}
	
	/**
	 * 获取某月，所有区县平均房价的排名
	 * @param model
	 * @param date
	 * @return
	 */
	@RequestMapping("/avgRankByDate")
	@ResponseBody
	public List<HousePriceRedis> getAvgPricRankByDate(Model model , String date){
		if(StringUtils.isBlank(date)){
			return null;
		}
		//从redis缓存中获取数据
		List<HousePriceRedis> priceSet = housePriceRedisService.getAvgRankByDate(date);
		if(priceSet != null && priceSet.size() > 0){
			return priceSet;
		}
		//缓存数量不够时 为Mongodb查找到记录时进行新增
		List<HousePriceMongo> prices = housePriceMongoService.findHousePricesByDate(date);
		HousePriceRedis priceRedis = new HousePriceRedis();
		for(HousePriceMongo price : prices){
			priceRedis.setCity(price.getCity());
			priceRedis.setProvince(price.getProvince());
			priceRedis.setDate(price.getDate());
			List<DistrictDataMongo> districts = price.getDistricts();
			for(DistrictDataMongo district : districts){
				priceRedis.setDistrict(district.getDistrict());
				priceRedis.setBaseData(district.getBaseData());
				housePriceRedisService.addAvgRankByDate(priceRedis);
			}
		}
		return housePriceRedisService.getAvgRankByDate(date);
	}
	
	/**
	 * 获取某区，月份平均房价的排名
	 * @param model
	 * @param date
	 * @return
	 */
	@RequestMapping("/avgRankByDist")
	@ResponseBody
	public List<HousePriceRedis> getAvgPricRankByDist(Model model,String province, String city, String district){
		if(StringUtils.isBlank(district)){
			return null;
		}
		//从redis缓存中获取数据
		List<HousePriceRedis> priceSet = housePriceRedisService.getAvgRankByDist(province, city, district);
		if(priceSet != null && priceSet.size() > 0){
			return priceSet;
		}
		//缓存数量不够时 为Mongodb查找到记录时进行新增
		List<HousePriceMongo> prices = housePriceMongoService.findHousePricesByDist(city, district);
		HousePriceRedis priceRedis = new HousePriceRedis();
		for(HousePriceMongo price : prices){
			priceRedis.setCity(price.getCity());
			priceRedis.setProvince(price.getProvince());
			priceRedis.setDate(price.getDate());
			List<DistrictDataMongo> districts = price.getDistricts();
			for(DistrictDataMongo d : districts){
				if(district.equals(d.getDistrict())){
					priceRedis.setDistrict(d.getDistrict());
					priceRedis.setBaseData(d.getBaseData());
					housePriceRedisService.addAvgRankByDist(priceRedis);
				}
			}
		}
		return housePriceRedisService.getAvgRankByDist(province, city, district);
	}
	
	/**
	 * 获取某区，月份涨幅的排名
	 * @param model
	 * @param date
	 * @return
	 */
	@RequestMapping("/riseRankByDist")
	@ResponseBody
	public List<HousePriceRedis> getPriceRiseRankByDist(Model model,String province, String city, String district){
		if(StringUtils.isBlank(district)){
			return null;
		}
		//从redis缓存中获取数据
		List<HousePriceRedis> priceSet = housePriceRedisService.getPriceRiseRankByDist(province, city, district);
		if(priceSet != null && priceSet.size() > 0){
			return priceSet;
		}
		//缓存数量不够时 为Mongodb查找到记录时进行新增
		List<HousePriceMongo> prices = housePriceMongoService.findHousePricesByDist(city, district);
		HousePriceRedis priceRedis = new HousePriceRedis();
		for(HousePriceMongo price : prices){
			priceRedis.setCity(price.getCity());
			priceRedis.setProvince(price.getProvince());
			priceRedis.setDate(price.getDate());
			List<DistrictDataMongo> districts = price.getDistricts();
			for(DistrictDataMongo d : districts){
				if(district.equals(d.getDistrict())){
					priceRedis.setDistrict(d.getDistrict());
					priceRedis.setBaseData(d.getBaseData());
					housePriceRedisService.addPriceRiseRankByDist(priceRedis);
				}
			}
		}
		return housePriceRedisService.getPriceRiseRankByDist(province, city, district);
	}
}
