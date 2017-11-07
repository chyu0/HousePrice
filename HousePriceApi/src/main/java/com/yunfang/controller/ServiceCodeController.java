package com.yunfang.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yunfang.service.ServiceCodeService;
import com.yunfang.utils.CommonUtils;

@Controller
@RequestMapping("serviceCode")
public class ServiceCodeController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ServiceCodeController.class);
	
	@Resource
	private ServiceCodeService serviceCodeService;
	
	/**
	 * 新增或更新操作
	 * @param publicKey
	 * @param verifyFlag
	 * @return
	 */
	@RequestMapping("saveCode")
	@ResponseBody
	public String saveCode(String code , String serviceBean , String methodName){
		try{
			serviceCodeService.addNewServiceCode(code, serviceBean, methodName);
			return "success";
		}catch(Exception e){
			logger.error("保存或更新publickey失败" + CommonUtils.exceptionToStr(e));
			return "fail";
		}
	}
	
}
