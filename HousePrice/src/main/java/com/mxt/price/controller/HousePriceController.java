package com.mxt.price.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mxt.price.modal.HousePrice2;
import com.mxt.price.service.HousePriceMongoService;

@Controller
@RequestMapping("/housePrice")
public class HousePriceController extends BaseController {

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
	public Map<String,Object> readExcel(Model model) {
		
		return this.successResult();
	}
}
