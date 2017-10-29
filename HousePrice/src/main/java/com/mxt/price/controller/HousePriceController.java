package com.mxt.price.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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
 * @author maoxiaotai
 * @data 2017年10月20日 下午4:10:36
 * @Description HousePriceController
 */
@Controller
@RequestMapping("/housePrice")
public class HousePriceController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(HousePriceController.class);

	@Resource
	private HousePriceMongoService housePriceMongoService;
	
	@Resource
	private HousePriceRedisService housePriceRedisService;
	
	@Value("${zavg_price_date_limit}")
	private final int zAvgPriceDateLimit = 100;
	
	@RequestMapping("/readExcel")
	@ResponseBody
	public Map<String,Object> readExcel(Model model) throws IOException {
		try{
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("price_excel.xls");
			File file = new File(url.getFile());
			List<HousePriceMongo> housePriceList = HousePriceExcelUtils.getHousePriceMongosByExcel(file);
			for(HousePriceMongo housePrice : housePriceList){
				housePriceMongoService.updateInser(housePrice);//插入mongodb
			}
			return this.successResult();
		}catch(Exception e){
			logger.error("从Excel保存数据至MongoDB异常：" + CommonUtils.exceptionToStr(e));
			return this.failResult("从Excel保存数据至MongoDB异常");
		}
	}
	
	/**
	 * 更新某时间段所有城市的平均房价的涨幅
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
		return housePriceRedisService.getAvgRankByDate(district);
	}
	
	/**
	 * 显示某城市对应各区县在某时间段内，均价的分步情况
	 * @param model 	model
	 * @param city		市级
	 * @param startTime 开始时间
	 * @param district  区县
	 * @param endTime 	结束时间
	 * @return avg_chart视图
	 */
	@RequestMapping("/avgChart")
	public String getAvgChart(Model model , @ModelAttribute("city")String city ,@ModelAttribute("startTime")String startTime ,@ModelAttribute("endTime")String endTime , @ModelAttribute("district") String district) {
		try{
			List<HousePriceMongo> avgPriceList = housePriceMongoService.findHousePricesByCityAndDate(city, startTime, endTime);
			if(avgPriceList != null && avgPriceList.size() >= 0){
				List<String> dateList = new ArrayList<String>();
				Map<String,List<BaseData>> disMap = new HashMap<String,List<BaseData>>();
				for(int index = avgPriceList.size()-1 ; index >= 0 ; index --){
					HousePriceMongo housePrice = avgPriceList.get(index);
					dateList.add(housePrice.getDate());//日期数据
					List<DistrictDataMongo> dists = housePrice.getDistricts();
					for(DistrictDataMongo d : dists){
						if(d.getDistrict().equals(district)){
							List<BaseData> list = disMap.get(d.getDistrict());
							if(list == null){
								list = new ArrayList<BaseData>();
								disMap.put(d.getDistrict(), list);
							}
							list.add(d.getBaseData());
						}
					}
				}
				model.addAttribute("avgPriceList", avgPriceList);
				model.addAttribute("disMap", disMap);
				model.addAttribute("dateList", dateList);
			}
		}catch(Exception e){
			logger.error("获取平均值曲线数据异常" + CommonUtils.exceptionToStr(e));
		}
		return "chart/avg_chart";
	}
}
