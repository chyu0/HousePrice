package com.mxt.price.test.redis;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mxt.price.dao.redis.HousePriceRedisDao;
import com.mxt.price.modal.BaseData;
import com.mxt.price.modal.CityData;
import com.mxt.price.modal.DistrictData;
import com.mxt.price.modal.HousePrice2;
import com.mxt.price.modal.PrivinceData;

/**
 * 单元测试类，测试RedisGeneratorTemplate中的相关操作
 * @author maoxiaotai
 * @data 2017年10月25日 下午9:32:38
 * @Description TODO
 */
@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations = { "classpath:spring.xml" })  
@ActiveProfiles(value="dev")
public class RedisGeneratorTemplateTest {

	@Resource
	private HousePriceRedisDao housePriceRedisDao;
	HousePrice2 housePrice2 = new HousePrice2();
	
	{
		BaseData baseData = new BaseData();
		baseData.setMaxPrice(new BigDecimal(30000.0d));
		baseData.setMinPrice(new BigDecimal(10000.0d));
		baseData.setAvgPrice(new BigDecimal(15000.0d));
		
		DistrictData districtData = new DistrictData();
		districtData.setDistrict("江夏区");
		districtData.setBaseData(baseData);
		
		CityData cityData = new CityData();
		cityData.setCity("武汉市");
		cityData.setDistricts(Arrays.asList(new DistrictData[]{districtData}));
		
		PrivinceData privinceData = new PrivinceData();
		privinceData.setPrivince("湖北省");
		privinceData.setCitys(Arrays.asList(new CityData[]{cityData}));
		
		housePrice2.setDate("2017-01");
		housePrice2.setPrivinces(Arrays.asList(new PrivinceData[]{privinceData}));
	}
	
	@Test
	public void testPut() {
		housePriceRedisDao.put("HousePrice2", housePrice2);
	}
	
	@Test
	public void testlPush(){
		housePriceRedisDao.lPush("housePriceList", housePrice2 , housePrice2 , housePrice2);
	}
	
	@Test
	public void testLRange(){
		List<HousePrice2> list = housePriceRedisDao.lRange("housePriceList", 0, 2);
		if(list!=null && list.size()>0){
			System.out.println(list.size());
			for(HousePrice2 housePrice2 : list){
				System.out.println(housePrice2.getDate());
			}
		}
	}
	
	@Test
	public void testLTrim(){
		housePriceRedisDao.lTrim("housePriceList", 0, 3);
	}

	@Test
	public void testLLen(){
		System.out.println(housePriceRedisDao.lLen("housePriceList"));
	}
	
	@Test
	public void testzAdd(){
		housePriceRedisDao.zAdd("housePriceSet", 1.0, housePrice2);
	}
	
	@Test
	public void testhAdd(){
		Map<String,HousePrice2> hashes = new HashMap<String,HousePrice2>();
		hashes.put("2017-01", housePrice2);
		hashes.put("2017-02", housePrice2);
		hashes.put("2017-03", housePrice2);
		housePriceRedisDao.hMSet("housePriceHash", hashes);
	}
	
	@Test
	public void testhGet(){
		HousePrice2 housePrice = housePriceRedisDao.hGet("housePriceHash", "2017*");
		System.out.println(housePrice.getDate());
	}
	
	@Test
	public void testhMGet(){
		List<HousePrice2> housePrices = housePriceRedisDao.hMGet("housePriceHash", "2017-01" ,"2017-02");
		if(housePrices!=null && housePrices.size() > 0){
			System.out.println(housePrices.size());
			for(HousePrice2 h : housePrices){
				System.out.println(h.getDate());
			}
		}
	}
}
