package com.mxt.price.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mxt.price.modal.common.BaseData;
import com.mxt.price.modal.mongo.DistrictDataMongo;
import com.mxt.price.modal.mongo.HousePriceMongo;
import com.mxt.price.modal.redis.HousePriceRedis;
import com.mxt.price.service.HousePriceMongoService;
import com.mxt.price.service.HousePriceRedisService;
import com.mxt.price.utils.CommonUtils;
import com.mxt.price.utils.DateUtils;

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
	
	/**
	 * 跳转至平均价视图层
	 * @param model
	 * @return
	 */
	@RequestMapping("avgChart")
	public String getAvgChart(Model model){
		return "chart/avg_chart";
	}
	
	/**
	 * 跳转至平均价视图层
	 * @param model
	 * @return
	 */
	@RequestMapping("riseChart")
	public String getRiseChart(Model model){
		return "chart/rise_chart";
	}
	
	/**
	 * 显示某城市对应各区县在某时间段内的基础数据
	 * @param model 	model
	 * @param city		市级
	 * @param startTime 开始时间
	 * @param district  区县
	 * @param endTime 	结束时间
	 * @return avg_chart视图
	 */
	@RequestMapping("/canvasBaseDataChart")
	@ResponseBody
	public Map<String,Object> canvasBaseDataChart(Model model , String city ,String startTime ,String endTime , String district) {
		try{
			Map<String,Object> result = new HashMap<String,Object>();
			List<HousePriceMongo> avgPriceList = housePriceMongoService.findHousePricesByCityAndDate(city, startTime, endTime);
			if(avgPriceList != null && avgPriceList.size() > 0){
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
				result.put("disMap", disMap);
				result.put("dates", DateUtils.getMonthBetween(startTime, endTime));
				return successResult(result);
			}else{
				return failResult("未取到数据，请重写选择");
			}
		}catch(Exception e){
			logger.error("获取平均值曲线数据异常" + CommonUtils.exceptionToStr(e));
		}
		return failResult("系统异常");
	}
	
	
	/**
	 * 跳转至趋势视图层
	 * @param model
	 * @return
	 */
	@RequestMapping("distRiseChart")
	public String getDistRiseChart(Model model){
		return "chart/dist_rise_chart";
	}
	
	
	/**
	 * 绘制某市，在某个时间段内的涨幅情况
	 * @param model 	model
	 * @param city		市级
	 * @param startTime 开始时间
	 * @param district  区县
	 * @param endTime 	结束时间
	 * @return avg_chart视图
	 */
	@RequestMapping("/canvasDistRiseChart")
	@ResponseBody
	public Map<String,Object> canvasDistRiseChart(Model model ,String province, String city ,String startTime ,String endTime) {
		try{
			Map<String,Object> result = new HashMap<String,Object>();
			List<String> dates = DateUtils.getMonthBetween(startTime, endTime);
			for(String date : dates){
				List<String> rPriceRises = housePriceRedisService.getPriceRiseRankByDate(province, city, date);
				if(rPriceRises == null || rPriceRises.size() <= 0){//无数据写缓存，过期时间1天
					HousePriceRedis priceRedis = new HousePriceRedis();
					priceRedis.setDate(date);
					priceRedis.setCity(city);
					priceRedis.setProvince(province);
					HousePriceMongo housePrice = housePriceMongoService.findHousePricesByDateAndCity(date, province, city);
					if(housePrice != null && housePrice.getDistricts() != null){
						List<DistrictDataMongo> districts = housePrice.getDistricts();
						for(DistrictDataMongo district : districts){
							priceRedis.setDistrict(district.getDistrict());
							priceRedis.setBaseData(district.getBaseData());
							housePriceRedisService.addPriceRiseRankByDate(priceRedis);
						}
					}
				}
			}
			List<Map<String,Object>> priceRiseList = housePriceRedisService.getPriceRiseRankByCityAndDate(province, city, dates);
			result.put("priceRise", priceRiseList);
			return successResult(result);
		}catch(Exception e){
			logger.error("获取涨幅曲线数据异常" + CommonUtils.exceptionToStr(e));
		}
		return failResult("系统异常");
	}
	
	/**
	 * 跳转至趋势视图层
	 * @param model
	 * @return
	 */
	@RequestMapping("distAvgChart")
	public String getDistAvgChart(Model model){
		return "chart/dist_avg_chart";
	}
	
	
	/**
	 * 查询某市，所有小区，在某月份房价分步情况
	 * @param model
	 * @param province
	 * @param city
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping("/canvasDistPriceChart")
	@ResponseBody
	public Map<String,Object> canvasDistPriceChart(Model model ,String province, String city , String date) {
		try{
			Map<String,Object> result = new HashMap<String,Object>();
			HousePriceMongo housePrice = housePriceMongoService.findHousePricesByDateAndCity(date, province, city);
			result.put("housePrice", housePrice);
			return successResult(result);
		}catch(Exception e){
			logger.error("获取涨幅曲线数据异常" + CommonUtils.exceptionToStr(e));
		}
		return failResult("系统异常");
	}
	
}
