package com.yunfang.controller.access;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunfang.annotation.AuthVerify;
import com.yunfang.controller.BaseController;
import com.yunfang.enums.ResultCode;
import com.yunfang.modal.DistrictDataMongo;
import com.yunfang.modal.HousePriceMongo;
import com.yunfang.modal.Result;
import com.yunfang.service.HousePriceMongoService;

/**
 * 价格查询api
 * @author maoxiaotai
 * @data 2017年11月2日 下午4:41:40
 * @Description TODO
 */
@Controller
@RequestMapping("price")
public class PriceController extends BaseController{
	
	@Resource
	private HousePriceMongoService housePriceMongoService;

	@AuthVerify(verifyFlag = "avgPrice")
	@RequestMapping("avgPrice")
	@ResponseBody
	public Result getPriceByCity(String time_stamp, String access_signature ,String province_name, String city_name, String date){
		Result result = new Result();
		try{
			if(StringUtils.isBlank(province_name) || StringUtils.isBlank(city_name) || StringUtils.isBlank(date)){
				result.setSuccess(false);
				result.setDate(new Date());
				result.setMessage("参数异常");
				result.setCode(ResultCode.REQUESTPARAMSERROR.getCode());
				return result;
			}
			
			HousePriceMongo mongo = housePriceMongoService.findHousePricesByDateAndCity(date, province_name, city_name);
			
			if(mongo == null || mongo.getDistricts() == null || mongo.getDistricts().size() <= 0){
				result.setSuccess(false);
				result.setDate(new Date());
				result.setMessage("获取数据为空");
				result.setCode(ResultCode.NULLRESULT.getCode());
				return result;
			}
			
			Map<String , Object> map = new HashMap<String, Object>();
			
			map.put("date", mongo.getDate());
			map.put("city_name", city_name);
			map.put("province_name", province_name);
			List<DistrictDataMongo> dists = mongo.getDistricts();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for(DistrictDataMongo dist : dists){
				Map<String,Object> d = new HashMap<String,Object>();
				d.put("district_name", dist.getDistrict());
				d.put("district_avg_price", dist.getBaseData().getAvgPrice());
				list.add(d);
			}
			map.put("districts", list);
			
			result.setSuccess(true);
			result.setGuid(UUID.randomUUID().toString());
			result.setDate(new Date());
			result.setMessage("接口调用正常");
			result.setResult((JSONObject)JSON.toJSON(map));
			result.setCode(ResultCode.SUCCESS.getCode());
		}catch(Exception e){
			result.setSuccess(false);
			result.setDate(new Date());
			result.setCode(ResultCode.ERROR.getCode());
			result.setMessage("接口调用异常，请检查参数是否正确");
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			System.out.println(new Date().getTime());
	}
}
