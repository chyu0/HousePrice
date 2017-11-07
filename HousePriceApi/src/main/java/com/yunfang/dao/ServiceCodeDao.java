package com.yunfang.dao;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.yunfang.modal.ServiceCode;
import com.yunfang.mongo.MongoDbTemplate;

@Component
public class ServiceCodeDao extends MongoDbTemplate<ServiceCode>{

	private final String collectionName = ServiceCode.class.getSimpleName();
	
	/**
	 * 保存
	 * @param serviceCode
	 */
	public void save(ServiceCode serviceCode){
		super.save(serviceCode , collectionName);
	}
	
	/**
	 * 查询serviceCode列表
	 * @param query
	 * @return
	 */
	public ServiceCode getServiceCodeOne(Query query){
		return super.queryOne(query, collectionName);
	}
}
