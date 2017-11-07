package com.yunfang.service.impl;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.yunfang.enums.PublicKeyVerifyCode;
import com.yunfang.modal.PublicKey;
import com.yunfang.service.AccessVerifyService;
import com.yunfang.service.PublicKeyService;
import com.yunfang.utils.CommonUtils;
import com.yunfang.utils.SignUtils;

@Service
public class AccessVerifyServiceImpl implements AccessVerifyService, ApplicationListener<ContextRefreshedEvent> {
	
	private static Logger logger = LoggerFactory.getLogger(AccessVerifyServiceImpl.class);
	
	/**
	 * 存放数字签名验证映射
	 */
	private static final Map<String ,Object> keyMap = new HashMap<String, Object>();
	
	/**
	 * 密匙
	 */
	@Value("${privateKey}")
	private String privateKey = null;
	
	/**
	 * 允许时间搓，最大间隔，默认是10000ms
	 */
	@Value("${timespace}")
	private Long timespace = 10000L;

	
	@Resource
	private PublicKeyService publicKeyService;
	
	@Override
	public PublicKeyVerifyCode verify(Long timestamp, String publicKey, String serviceCode) {
		
		//验证时间搓是否合法
		Date now = new Date();
		if(now.getTime() - timestamp > timespace){
			return PublicKeyVerifyCode.TIMESTAMPOUT;
		}
		
		//首先校验publicKey是否正确，可否使用
		boolean verify = SignUtils.verify(publicKey, privateKey, keyMap);
		
		//公匙不可用
		if(!verify){
			return PublicKeyVerifyCode.ACCESSERROR;
		}
		
		PublicKey key = publicKeyService.findVerifyByKey(publicKey);
		if(key != null && key.getServiceCodes() != null && key.getServiceCodes().size() > 0){
			List<String> serviceCodes = key.getServiceCodes();
			for(String code : serviceCodes){
				if(code.equals(serviceCode)){
					return PublicKeyVerifyCode.ACCESSSUCCESS;
				}
			}
		}
		
		return PublicKeyVerifyCode.NOAUTH;
	}

	@Override
	public void addNewVerify(String publicKey) {
		try {
			SignUtils.addNewPublicKey(keyMap,privateKey, publicKey);
		} catch (NoSuchAlgorithmException e) {
			logger.error("添加publicKey失败" + CommonUtils.exceptionToStr(e));
		}
	}

	/**
	 * 容器启动时触发
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try{
			if(event.getApplicationContext().getParent() == null){
				List<PublicKey> publicKeys = publicKeyService.findAllKeys();
				List<String> keys = new ArrayList<String>();
				for(PublicKey publicKey : publicKeys){
					keys.add(publicKey.getPublicKey());
				}
				SignUtils.genKeyPair(keyMap ,privateKey ,keys);
			}
		}catch(Exception e){
			logger.error("公匙初始化失败" + CommonUtils.exceptionToStr(e));
		}
	}

}
