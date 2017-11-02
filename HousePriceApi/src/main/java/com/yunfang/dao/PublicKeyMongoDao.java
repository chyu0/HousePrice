package com.yunfang.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.yunfang.modal.PublicKey;
import com.yunfang.mongo.MongoDbTemplate;

/**
 * 密匙dao，负责对做密匙保存更新操作
 * @author maoxiaotai
 * @data 2017年11月2日 上午10:26:25
 * @Description TODO
 */
@Component
public class PublicKeyMongoDao extends MongoDbTemplate<PublicKey> {

	private final String collectionName = PublicKey.class.getSimpleName();
	
	/**
	 * 保存
	 * @param publicKey
	 */
	public void save(PublicKey publicKey){
		super.save(publicKey , collectionName);
	}
	
	/**
	 * 更新
	 * @param publicKey
	 */
	public void update(Query query , Update update){
		super.updateFirst(query, update, collectionName);;
	}
	
	/**
	 * 通过key查询
	 * @param query
	 * @param publicKey
	 * @return
	 */
	public PublicKey findOnePublicKey(Query query){
		return super.queryOne(query , collectionName);
	}
	
	/**
	 * 查询publicKey列表
	 * @param query
	 * @return
	 */
	public List<PublicKey> getPublicKeys(Query query){
		return super.queryList(query, collectionName);
	}
	
	/**
	 * 查询所有publicKey
	 * @param query
	 * @return
	 */
	public List<PublicKey> getAllPublicKeys(){
		Query query = new Query();
		return super.queryList(query, collectionName);
	}
}
