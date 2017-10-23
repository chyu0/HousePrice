package com.mxt.price.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mxt.price.modal.BaseData;
import com.mxt.price.modal.DistrictData;
import com.mxt.price.modal.HousePrice2;
import com.mxt.price.service.HousePriceMongoService;
import com.mxt.price.utils.CommonUtils;
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
	
	@RequestMapping("/save")
	@ResponseBody
	public Map<String,Object> save(Model model) {
		housePriceMongoService.save();
		return this.successResult();
	}
	
	@RequestMapping("/find")
	@ResponseBody
	public List<HousePrice2> findHousePrice(Model model) {
		return housePriceMongoService.findHousePrice();
	}
	
	@RequestMapping("/readExcel")
	@ResponseBody
	public Map<String,Object> readExcel(Model model) throws IOException {
		try{
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("price_excel.xls");
			File file = new File(url.getFile());
			List<HousePrice2> housePriceList = HousePriceExcelUtils.getHousePricesByExcel(file);
			for(HousePrice2 housePrice : housePriceList){
				housePriceMongoService.save(housePrice);
			}
			return this.successResult();
		}catch(Exception e){
			logger.error("从Excel保存数据至MongoDB异常：" + CommonUtils.exceptionToStr(e));
			return this.failResult("从Excel保存数据至MongoDB异常");
		}
	}
	
	/**
	 * 显示某城市对应各区县在某时间段内，均价的分步情况
	 * @param model 	model
	 * @param city		市级
	 * @param startTime 开始时间
	 * @param endTime 	结束时间
	 * @return avg_chart视图
	 */
	@RequestMapping("/avgChart")
	public String getAvgChart(Model model , @ModelAttribute("city")String city ,@ModelAttribute("startTime")String startTime ,@ModelAttribute("endTime")String endTime) {
		try{
			List<HousePrice2> avgPriceList = housePriceMongoService.findHousePriceByCityAndDate(city, startTime, endTime);
			List<String> dateList = new ArrayList<String>();
			Map<String,List<BaseData>> disMap = new HashMap<String,List<BaseData>>();
			for(int index = avgPriceList.size()-1 ; index >= 0 ; index --){
				HousePrice2 housePrice = avgPriceList.get(index);
				dateList.add(housePrice.getDate());//日期数据
				List<DistrictData> dists = housePrice.getPrivinces().get(0).getCitys().get(0).getDistricts();
				for(DistrictData d : dists){
					List<BaseData> list = disMap.get(d.getDistrict());
					if(list == null){
						list = new ArrayList<BaseData>();
						disMap.put(d.getDistrict(), list);
					}
					list.add(d.getBaseData());
				}
			}
			model.addAttribute("avgPriceList", avgPriceList);
			model.addAttribute("disMap", disMap);
			model.addAttribute("dateList", dateList);
		}catch(Exception e){
			logger.error("获取平均值曲线数据异常" + CommonUtils.exceptionToStr(e));
		}
		return "chart/avg_chart";
	}
}
