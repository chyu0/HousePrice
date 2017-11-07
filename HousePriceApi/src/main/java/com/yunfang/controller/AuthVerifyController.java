package com.yunfang.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yunfang.modal.PublicKey;
import com.yunfang.service.AccessVerifyService;
import com.yunfang.service.PublicKeyService;
import com.yunfang.utils.CommonUtils;

/**
 * 权限添加
 * @author maoxiaotai
 * @data 2017年11月2日 下午1:56:27
 * @Description TODO
 */
@Controller
@RequestMapping("auth")
public class AuthVerifyController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(AuthVerifyController.class);

	@Resource
	private PublicKeyService publicKeyService;
	
	@Resource
	private AccessVerifyService accessVerifyService;
	
	/**
	 * 新增或更新操作
	 * @param publicKey
	 * @param verifyFlag
	 * @return
	 */
	@RequestMapping("saveKey")
	@ResponseBody
	public String saveKey(String publicKey , String serviceCodeStr){
		try{
			PublicKey key = publicKeyService.findVerifyByKey(publicKey);
			if(key == null){
				PublicKey publicKeyObj = new PublicKey();
				publicKeyObj.setPublicKey(publicKey);
				if(StringUtils.isNotBlank(serviceCodeStr)){
					List<String> serviceCodes = Arrays.asList(serviceCodeStr.split(","));
					publicKeyObj.setServiceCodes(serviceCodes);
				}
				publicKeyService.save(publicKeyObj);
				accessVerifyService.addNewVerify(publicKey);
			}else{
				if(StringUtils.isNotBlank(serviceCodeStr)){
					List<String> curServiceCodes = key.getServiceCodes();
					List<String> serviceCode = new ArrayList<String>(Arrays.asList(serviceCodeStr.split(",")));
					serviceCode.removeAll(curServiceCodes);
					serviceCode.addAll(curServiceCodes);
					key.setServiceCodes(serviceCode);
					publicKeyService.update(key);
				}
			}
			return "success";
		}catch(Exception e){
			logger.error("保存或更新publickey失败" + CommonUtils.exceptionToStr(e));
			return "fail";
		}
	}
	
	/**
	 * 查询操作
	 * @param publicKey
	 * @return
	 */
	@RequestMapping("findPublicKey")
	@ResponseBody
	public PublicKey findPublicKey(String publicKey){
		PublicKey key = publicKeyService.findVerifyByKey(publicKey);
		return key;
	}
	
}
