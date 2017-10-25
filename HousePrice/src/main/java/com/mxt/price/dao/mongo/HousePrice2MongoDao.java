package com.mxt.price.dao.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mxt.price.modal.HousePrice2;
import com.mxt.price.template.MongoDbTemplate;

@Component
public class HousePrice2MongoDao extends MongoDbTemplate<HousePrice2> {

	public void save(HousePrice2 housePrice){
		super.save(housePrice , HousePrice2.class.getSimpleName());
	}
	
	public List<HousePrice2> find(Query query){
		return super.queryList(query, HousePrice2.class.getSimpleName());
	}
}
