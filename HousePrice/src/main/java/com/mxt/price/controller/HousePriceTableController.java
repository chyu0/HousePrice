package com.mxt.price.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mxt.price.service.HousePriceMongoService;
import com.mxt.price.service.HousePriceRedisService;

/**
 * 表格数据展示层
 * @author maoxiaotai
 * @data 2017年11月1日 上午10:43:10
 * @Description TODO
 */
@Controller
@RequestMapping("/priceTable")
public class HousePriceTableController extends BaseController{
	private static Logger logger = LoggerFactory.getLogger(HousePriceTableController.class);

	@Resource
	private HousePriceMongoService housePriceMongoService;
	
	@Resource
	private HousePriceRedisService housePriceRedisService;
	
	
}
