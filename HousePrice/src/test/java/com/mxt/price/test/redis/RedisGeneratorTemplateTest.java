package com.mxt.price.test.redis;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mxt.price.modal.common.BaseData;
import com.mxt.price.modal.redis.HousePriceRedis;
import com.mxt.price.template.RedisGeneratorTemplate;

/**
 * 单元测试类，测试RedisGeneratorTemplate中的相关操作
 * @author maoxiaotai
 * @data 2017年10月25日 下午9:32:38
 * @Description TODO
 */
@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations = { "classpath:spring.xml" })  
@ActiveProfiles(value="dev")
public class RedisGeneratorTemplateTest extends RedisGeneratorTemplate<HousePriceRedis> {

	HousePriceRedis housePrice = new HousePriceRedis();
	
	{
		BaseData baseData = new BaseData();
		baseData.setMaxPrice(new BigDecimal(30000.0d));
		baseData.setMinPrice(new BigDecimal(10000.0d));
		baseData.setAvgPrice(new BigDecimal(15000.0d));
		
		housePrice.setProvince("湖北省");
		housePrice.setCity("武汉市");
		housePrice.setDistrict("江夏区");
		housePrice.setDate("2017-01");
	}
	
	@Test
	public void testlPush(){
		super.lPush("housePriceList", housePrice , housePrice , housePrice);
	}
	
	@Test
	public void testLRange(){
		List<HousePriceRedis> list = super.lRange("housePriceList", 0, 2);
		if(list!=null && list.size()>0){
			System.out.println(list.size());
			for(HousePriceRedis housePrice : list){
				System.out.println(housePrice.getDate());
			}
		}
	}
	
	@Test
	public void testLTrim(){
		super.lTrim("housePriceList", 0, 3);
	}

	@Test
	public void testLLen(){
		System.out.println(super.lLen("housePriceList"));
	}
	
	@Test
	public void testzAdd(){
		super.zAdd("housePriceSet", 1.0, housePrice);
	}
	
	@Test
	public void testhAdd(){
		Map<String,HousePriceRedis> hashes = new HashMap<String,HousePriceRedis>();
		hashes.put("2017-01", housePrice);
		hashes.put("2017-02", housePrice);
		hashes.put("2017-03", housePrice);
		super.hMSet("housePriceHash", hashes);
	}
	
	@Test
	public void testhGet(){
		HousePriceRedis housePrice = super.hGet("housePriceHash", "2017-01");
		System.out.println(housePrice.getDate());
	}
	
	@Test
	public void testhMGet(){
		List<HousePriceRedis> housePrices = super.hMGet("housePriceHash", "2017-01" ,"2017-02");
		if(housePrices!=null && housePrices.size() > 0){
			System.out.println(housePrices.size());
			for(HousePriceRedis h : housePrices){
				System.out.println(h.getDate());
			}
		}
	}
}
