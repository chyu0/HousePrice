package com.mxt.price.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
