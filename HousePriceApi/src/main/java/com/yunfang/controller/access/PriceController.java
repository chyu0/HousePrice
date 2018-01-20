package com.yunfang.controller.access;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunfang.annotation.AuthVerify;
import com.yunfang.controller.BaseController;
import com.yunfang.enums.ResultCode;
import com.yunfang.modal.Result;
import com.yunfang.modal.ServiceCode;
import com.yunfang.service.ServiceCodeService;
import com.yunfang.utils.CommonUtils;
import com.yunfang.utils.MethodUtils;

/**
 * api
 * @author maoxiaotai
 * @data 2017年11月2日 下午4:41:40
 * @Description TODO
 */
@Controller
@RequestMapping("property")
public class PriceController extends BaseController{
	
	private static Logger logger = LoggerFactory.getLogger(PriceController.class);
	
	@Resource
	private ServiceCodeService serviceCodeService;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private final String serviceCodeName = "serviceCode";
	
	private final String accessSignatureName = "token";
	
	private final String timeStampName = "time";

	/**
	 * 通过servicecode判断具体执行那个service的那个方法
	 * @param time
	 * @param token
	 * @param serviceCode
	 * @return
	 */
	@AuthVerify(serviceCode = serviceCodeName, timeStamp = timeStampName , access_sign = accessSignatureName)
	@RequestMapping("transaction")
	@ResponseBody
	public Result transaction(String time, String token, String serviceCode){
		Result result = new Result();
		if(StringUtils.isBlank(serviceCode)){
			result.setSuccess(false);
			result.setDate(new Date());
			result.setMessage(ResultCode.NULLSERVICECODE.getDesc());
			result.setCode(ResultCode.NULLSERVICECODE.getCode());
			return result;
		}
		
		ServiceCode service = serviceCodeService.getServiceByCode(serviceCode);
			
		if(service == null || StringUtils.isBlank(service.getServiceName()) || StringUtils.isBlank(service.getMethodName())){
			result.setSuccess(false);
			result.setDate(new Date());
			result.setMessage(ResultCode.ERRORSERVICECODE.getDesc());
			result.setCode(ResultCode.ERRORSERVICECODE.getCode());
			return result;
		}
			
		String serviceBean = service.getServiceName();
		String methodName = service.getMethodName();
		
		try{	
			Object objService = MethodUtils.getTarget(applicationContext.getBean(serviceBean));
			Class<?> clazz = objService.getClass();
			
			@SuppressWarnings("unchecked")
			Map<String,Object[]> paramsMap = request.getParameterMap();  
			
			Class<?>[] argsClass = new Class[paramsMap.size() - 3];
			
			Object[] args = new Object[paramsMap.size() - 3];
			
			int index = 0;
			for(Map.Entry<String,Object[]> param : paramsMap.entrySet()){
				String key = param.getKey();
				if(!timeStampName.equals(key) && !accessSignatureName.equals(key) && !serviceCodeName.equals(key)){
					Object[] value = paramsMap.get(param.getKey());//数组形式
					if(value!=null && value.length > 0){
						argsClass[index] = value[0].getClass();
						args[index] = value[0];
						index++;
					}
				}
			}
			
			//获取对应方法
			Method method = clazz.getDeclaredMethod(methodName, argsClass);
			
			if(method == null){
				result.setSuccess(false);
				result.setDate(new Date());
				result.setMessage("serviceCode异常或者参数错误");
				result.setCode(ResultCode.NOSUCHMETHOD.getCode());
				return result;
			}
			
			List<String> paramNames = MethodUtils.getParamterName(clazz, methodName);
			Object[] newArgs = new Object[args.length];
			for(int i = 0 ; i<paramNames.size() ; i++){
				Object[] v = paramsMap.get(paramNames.get(i));
				newArgs[i] = (v != null && v.length > 0) ?  v[0] : null ;
			}
			
			Object resultObj = method.invoke(objService , newArgs);
			
			result.setSuccess(true);
			result.setGuid(UUID.randomUUID().toString());
			result.setDate(new Date());
			result.setMessage("接口调用正常");
			result.setResult((JSONObject)JSON.toJSON(resultObj));
			result.setCode(ResultCode.SUCCESS.getCode());
			return result;
			
		}catch(Exception e){
			logger.error(serviceBean + "," + methodName +"接口调用异常" + CommonUtils.exceptionToStr(e));
			result.setSuccess(false);
			result.setDate(new Date());
			result.setMessage(ResultCode.ERROR.getDesc());
			result.setCode(ResultCode.ERROR.getCode());
			return result;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			System.out.println(new Date().getTime());
	}
}
