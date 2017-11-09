package com.mxt.price.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
	
	@RequestMapping("initAvg")
	public String initAvg(){
		return "init/initAvg";
	}
	
	private String avgCode = "S0079";//均价服务码
	private String maxMinCode = "S0080";//最高最低服务码
	
	@RequestMapping("/initData")
	@ResponseBody
	public Map<String,Object> initDataFromHttp(String startDate , String endDate , String province , String city){
		StringBuffer str = new StringBuffer();
		//url组装，调用云房接口，形如http://open.fangjia.com/property/transaction?time=1509610841808&serviceCode=S0079&token=***&province_name=%E6%B9%96%E5%8C%97&city_name=%E6%AD%A6%E6%B1%89&date=2016-10
		str.append("http://open.fangjia.com/property/transaction?time=");
		Long timestamp = Calendar.getInstance().getTimeInMillis();
		str.append(timestamp).append("&token=").append(assign);
		str.append("&province_name=").append(province).append("&city_name=").append(city);
		List<String> dateList = DateUtils.getMonthBetween(startDate, endDate);
		List<Map<String,Object>> failResult = new ArrayList<Map<String,Object>>();
		for(String date : dateList){
			Map<String,Object> returnResult = new HashMap<String,Object>();
			try{
				String avgUrl = str.toString() + "&serviceCode=" + avgCode + "&date=" + date;
				String maxMinUrl = str.toString() + "&serviceCode=" + maxMinCode + "&date=" + date;
				JSONObject avgJsonResult = HttpRequestUtils.httpGet(avgUrl);
				JSONObject maxMinJsonResult = HttpRequestUtils.httpGet(maxMinUrl);
				if(avgJsonResult == null){//avg接口条用异常
					returnResult.put("date", date);
					returnResult.put("code", 500);
					returnResult.put("message", "请检查"+avgCode+"网络是否通畅");
					failResult.add(returnResult);
					continue;
				}
				if(maxMinJsonResult == null){//最高最低价接口调用异常
					returnResult.put("date", date);
					returnResult.put("code", 500);
					returnResult.put("message", "请检查"+maxMinCode+"网络是否通畅");
					failResult.add(returnResult);
					continue;
				}
				Map<String, DistrictDataMongo> distMap = new HashMap<String, DistrictDataMongo>();
				HousePriceMongo housePriceMongo = new HousePriceMongo();
				if(avgJsonResult.getIntValue("code") == 200){//接口调用成功，返回正确结果
					housePriceMongo.setCity(city);
					housePriceMongo.setDate(date);
					housePriceMongo.setProvince(province);
					JSONObject result = avgJsonResult.getJSONObject("result");//实际数据
					if(result == null || result.get("code") != null){
						returnResult.put("date", date);
						returnResult.put("code", result.getIntValue("code"));
						returnResult.put("message", result.getString("msg"));
						failResult.add(returnResult);
						continue;
					}
					JSONArray districts = result.getJSONArray("districts");//省数据
					if(districts != null && districts.size() > 0){
						for(int index=0 ;index < districts.size() ;index++){
							JSONObject district = districts.getJSONObject(index);
							String distName = district.getString("district_name");
							DistrictDataMongo distMongo = new DistrictDataMongo();
							BaseData baseData = new BaseData();
							baseData.setAvgPrice(new BigDecimal(district.getString("district_avg_price")));
							distMongo.setDistrict(distName);
							distMongo.setBaseData(baseData);
							distMap.put(distName, distMongo);
						}
					}
				}else{
					returnResult.put("date", date);
					returnResult.put("code", avgJsonResult.getIntValue("code"));
					returnResult.put("message", "获取均价接口："+avgJsonResult.get("message"));
					failResult.add(returnResult);
					continue;
				}
				if(maxMinJsonResult.getIntValue("code") == 200){//接口调用成功，返回正确结果
					JSONObject result = maxMinJsonResult.getJSONObject("result");//实际数据
					if(result == null || result.get("code") != null){
						returnResult.put("date", date);
						returnResult.put("code", result.getIntValue("code"));
						returnResult.put("message", result.getString("msg"));
						failResult.add(returnResult);
						continue;
					}
					JSONArray districts = result.getJSONArray("districts");//省数据
					List<DistrictDataMongo> distMongoList = new ArrayList<DistrictDataMongo>();
					if(districts != null && districts.size() > 0){
						for(int index=0 ;index < districts.size() ;index++){
							JSONObject dist = districts.getJSONObject(index);
							String distName = dist.getString("district_name");
							DistrictDataMongo distMongo = distMap.get(distName);
							JSONObject district = districts.getJSONObject(index);
							BaseData baseData = distMongo.getBaseData();
							baseData.setMaxPrice(new BigDecimal(district.getString("district_max_price")));
							baseData.setMinPrice(new BigDecimal(district.getString("district_min_price")));
							distMongo.setBaseData(baseData);
							distMongoList.add(distMongo);
						}
						housePriceMongo.setDistricts(distMongoList);
						housePriceMongoService.updateInser(housePriceMongo);
					}
				}else{
					returnResult.put("date", date);
					returnResult.put("code", maxMinJsonResult.getIntValue("code"));
					returnResult.put("message", "获取最高最低价接口" + maxMinJsonResult.get("message"));
					failResult.add(returnResult);
				}
			}catch(Exception e){
				logger.error("外部接口数据获取异常：" + CommonUtils.exceptionToStr(e));
				returnResult.put("date", date);
				returnResult.put("code", "500");
				returnResult.put("message", "接口调用异常，请检查网络是否通畅，" + e);
				failResult.add(returnResult);
			}
		}
		return successResult(failResult);
	}
	
	@RequestMapping("initRise")
	public String initRise(){
		return "init/initRise";
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
		List<HousePriceMongo> housePriceList = housePriceMongoService.findHousePricesByStartTimeAndEndTime(startTime, endTime);
		if(housePriceList == null || housePriceList.size() <= 0){
			return failResult("抱歉，为获取到当前时间段的数据");
		}
		List<String> dateList = DateUtils.getMonthBetween(startTime, endTime);
		List<String> dealList = new ArrayList<String>();
		List<Map<String, Object>> failResult = new ArrayList<Map<String, Object>>();
		for (HousePriceMongo housePrice : housePriceList) {
			Map<String,Object> returnResult = new HashMap<String,Object>();
			returnResult.put("date", housePrice.getDate());
			returnResult.put("province", housePrice.getProvince());
			returnResult.put("city", housePrice.getCity());
			dealList.add(housePrice.getDate());
			try{
				Calendar cal = Calendar.getInstance();
				cal.setTime(DateUtils.parse(housePrice.getDate(),DateUtils.DATE_TO_STRING_MONTH_PATTERN));
				cal.add(Calendar.MONTH, -1);
				String lastMonth = DateUtils.format(cal.getTime(),DateUtils.DATE_TO_STRING_MONTH_PATTERN);
				HousePriceMongo lastMonthPrice = housePriceMongoService.findHousePricesByDateAndCity(lastMonth,housePrice.getProvince(), housePrice.getCity());
				if (lastMonthPrice != null && lastMonthPrice.getDistricts()!=null) {
					List<DistrictDataMongo> distList = housePrice.getDistricts();
					List<DistrictDataMongo> lastDistList = lastMonthPrice.getDistricts();
					for (DistrictDataMongo dist : distList) {
						for (DistrictDataMongo lastDist : lastDistList) {
							if (dist.getDistrict().equals(lastDist.getDistrict())) {
								BaseData baseData = dist.getBaseData();
								baseData.setAvgPriceRise(BigDecimalUtils.rise(lastDist.getBaseData().getAvgPrice(),baseData.getAvgPrice(), 6));
								dist.setBaseData(baseData);
								break;
							}
						}
					}
					housePriceMongoService.updateMulti(housePrice);
				}else{
					returnResult.put("code", "300");
					returnResult.put("message", "为获取到"+lastMonth+"的数据");
					failResult.add(returnResult);
				}
			}catch(Exception e){
				logger.error("均价数据更新异常：" + CommonUtils.exceptionToStr(e));
				returnResult.put("code", "500");
				returnResult.put("message", "均价数据更新异常，" + e);
				failResult.add(returnResult);
			}
		}
		dateList.removeAll(dealList);//移除所以已处理
		for(String d : dateList){//把所有没有数据的记录添加到失败记录中
			Map<String,Object> returnResult = new HashMap<String,Object>();
			returnResult.put("date", d);
			returnResult.put("code", "500");
			returnResult.put("message", "为获取到当月数据");
			failResult.add(returnResult);
		}
		return this.successResult(failResult);
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
