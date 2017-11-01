package com.mxt.price.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mxt.price.modal.common.BaseData;
import com.mxt.price.modal.mongo.DistrictDataMongo;
import com.mxt.price.modal.mongo.HousePriceMongo;
import com.mxt.price.modal.redis.HousePriceRedis;
import com.mxt.price.service.HousePriceMongoService;
import com.mxt.price.service.HousePriceRedisService;
import com.mxt.price.utils.BigDecimalUtils;
import com.mxt.price.utils.CommonUtils;
import com.mxt.price.utils.DateUtils;
import com.mxt.price.utils.HousePriceExcelUtils;

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
	
	/**
	 * 数据初始化，此处从Excel初始化数据
	 * readExcel之后最好进行一次updateRise.action初始化涨幅数据
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/readExcel")
	@ResponseBody
	public Map<String,Object> readExcel(Model model) throws IOException {
		try{
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("price_excel.xls");
			File file = new File(url.getFile());
			List<HousePriceMongo> housePriceList = HousePriceExcelUtils.getHousePriceMongosByExcel(file);
			for(HousePriceMongo housePrice : housePriceList){
				housePriceMongoService.updateInser(housePrice);//插入mongodb，有记录是更新为Excel中数据
			}
			return this.successResult();
		}catch(Exception e){
			logger.error("从Excel保存数据至MongoDB异常：" + CommonUtils.exceptionToStr(e));
			return this.failResult("从Excel保存数据至MongoDB异常");
		}
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
