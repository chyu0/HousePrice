package com.yunfang.service;

import java.util.List;

import com.yunfang.modal.PublicKey;

/**
 * @author maoxiaotai
 * @data 2017年11月2日 上午10:23:42
 * @Description TODO
 */
public interface PublicKeyService {

	/**
	 * 保存key
	 * @param housePrice
	 */
	public void save(PublicKey publiKey);
	
	/**
	 * 更新key
	 * @param publicKey
	 */
	public void update(PublicKey publicKey);
	
	/**
	 * 通过key进行查找
	 * @param publicKey
	 * @return
	 */
	public PublicKey findVerifyByKey(String publicKey);
	
	/**
	 * 查询所有键
	 * @return
	 */
	public List<PublicKey> findAllKeys();
}
