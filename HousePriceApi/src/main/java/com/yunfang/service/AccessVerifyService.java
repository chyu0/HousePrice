package com.yunfang.service;

import com.yunfang.enums.PublicKeyVerifyCode;

/**
 * 权限校验
 * @author maoxiaotai
 * @data 2017年11月2日 上午11:01:40
 * @Description TODO
 */
public interface AccessVerifyService{
	
	/**
	 * 校验是否有查询的权限
	 * @param timestamp 时间撮
	 * @param accessToken 公匙
	 * @param verifyFlag 校验模块
	 * @return PublicKeyVerifyCode枚举对象0：成功，1or2：失败
	 */
	public PublicKeyVerifyCode verify(Long timestamp , String accessToken , String verifyFlag);
	
	/**
	 * 手动添加验证
	 * @param publicKey
	 */
	public void addNewVerify(String publicKey);

}
