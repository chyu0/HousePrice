package com.yunfang.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yunfang.modal.Email;
import com.yunfang.modal.HousePriceMongo;
import com.yunfang.service.HousePriceMongoService;
import com.yunfang.utils.CommonUtils;
import com.yunfang.utils.EmailUtils;
import com.yunfang.utils.HousePriceExcelUtils;

/**
 * 数据初始化，从Excel中读取数据，进行初始化
 * @author maoxiaotai
 * @data 2017年11月2日 下午5:05:04
 * @Description TODO
 */
@Controller
@RequestMapping("init")
public class InitDataController extends BaseController{

	@Resource
	private HousePriceMongoService housePriceMongoService;
	
	private static Logger logger = LoggerFactory.getLogger(InitDataController.class);
	
	/**
	 * 数据初始化，此处从Excel初始化数据
	 * readExcel之后最好进行一次updateRise.action初始化涨幅数据
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/readExcel")
	@ResponseBody
	public String readExcel(Model model) throws IOException {
		try{
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource("price_excel.xls");
			File file = new File(url.getFile());
			List<HousePriceMongo> housePriceList = HousePriceExcelUtils.getHousePriceMongosByExcel(file);
			for(HousePriceMongo housePrice : housePriceList){
				housePriceMongoService.updateInser(housePrice);//插入mongodb，有记录是更新为Excel中数据
			}
			Email email = new Email();
			email.setContent("初始化数据结束：" + JSONObject.toJSONString(housePriceList));
			email.setSubject("数据初始化");
			email.setTo(new ArrayList<String>(Arrays.asList(new String[]{"1939861002@qq.com","2896706067@qq.com"})));
			EmailUtils.sendEmail(email);
			return "success";
		}catch(Exception e){
			logger.error("从Excel保存数据至MongoDB异常：" + CommonUtils.exceptionToStr(e));
			return "fail";
		}
	}
}
