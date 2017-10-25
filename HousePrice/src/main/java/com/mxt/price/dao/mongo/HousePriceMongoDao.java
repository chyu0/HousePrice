package com.mxt.price.dao.mongo;

import org.springframework.stereotype.Component;

import com.mxt.price.modal.HousePrice;
import com.mxt.price.template.MongoDbTemplate;

/**
 * HousePriceMongoDao mongodb数据操作
 * @author maoxiaotai
 * @data 2017年10月20日 下午3:39:55
 * @Description TODO
 */
@Component
public class HousePriceMongoDao extends MongoDbTemplate<HousePrice>{
	
	public void save(HousePrice housePrice){
		super.save(housePrice , HousePrice.class.getSimpleName());
	}
	
}
