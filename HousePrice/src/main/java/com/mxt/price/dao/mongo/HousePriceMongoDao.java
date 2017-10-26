package com.mxt.price.dao.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mxt.price.modal.mongo.HousePriceMongo;
import com.mxt.price.template.MongoDbTemplate;

/**
 * HousePriceMongoDao mongodb数据操作
 * @author maoxiaotai
 * @data 2017年10月20日 下午3:39:55
 * @Description TODO
 */
@Component
public class HousePriceMongoDao extends MongoDbTemplate<HousePriceMongo>{
	
	private final String collectionName = HousePriceMongo.class.getSimpleName();
	
	/**
	 * 保存housepriceMongo对象
	 * @param housePrice
	 */
	public void save(HousePriceMongo housePrice){
		super.save(housePrice , collectionName);
	}
	
	/**
	 * 查询housePriceMongo列表
	 * @param query
	 * @return
	 */
	public List<HousePriceMongo> queryList(Query query){
		return super.queryList(query, collectionName);
	}
	
	/**
	 * 如果不存在插入记录，如果存在就更新
	 * @param query
	 * @param update
	 */
	public void upset(Query query , Update update){
		super.updateInser(query, update, collectionName);
	}
}
