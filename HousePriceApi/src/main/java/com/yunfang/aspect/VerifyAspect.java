package com.yunfang.aspect;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yunfang.annotation.AuthVerify;
import com.yunfang.enums.PublicKeyVerifyCode;
import com.yunfang.enums.ResultCode;
import com.yunfang.modal.Result;
import com.yunfang.service.AccessVerifyService;

/**
 * aop切面层，主要负责做权限校验
 * @author maoxiaotai
 * @data 2017年11月2日 下午1:41:06
 * @Description TODO
 */
@Component
@Aspect
public class VerifyAspect {
	
	private static Logger logger = LoggerFactory.getLogger(VerifyAspect.class);
	
	@Resource
	private AccessVerifyService accessVerifyService;
	
	@Pointcut("execution(* com.yunfang.controller.access..*.*(..))")
	public void verify(){}
	
	@Around("verify()")
	public Object arround(ProceedingJoinPoint joinPoint){
		Result result = new Result();
		Date now = new Date();
		
		try{
			
			MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
			
			Method method = methodSignature.getMethod();
			
			//检查方法上，是否有AuthVerify注解
			AuthVerify verify = method.getAnnotation(AuthVerify.class);
			
			if(verify == null){
				return joinPoint.proceed(joinPoint.getArgs());
			}
			
			//此处不适用方法名做校验的原因，在于，可能会存在多个类具有同名方法的情况
			String verifyFlag = verify.verifyFlag();
			//依据不同情况，给出不同的校验参数，可自己定义
			String timeStampName = verify.timeStamp();
			String assignName = verify.access_sign();
			
			//调用方法参数和参数名的映射
			Object[] arg = joinPoint.getArgs();
			String[] paramNames = methodSignature.getParameterNames();  
			Map<String, Object> paramMap = new HashMap<String, Object>();
			for(int i=0 ; i<paramNames.length ; i++){
				paramMap.put(paramNames[i], arg[i]);
			}
			
			//参数校验
			if(paramMap.get(timeStampName) == null || paramMap.get(assignName) == null){
				result.setSuccess(false);
				result.setDate(now);
				result.setMessage("校验失败，请检查time_stamp或access_signature是否正确");
				result.setCode(ResultCode.VERIFYPARAMSERROR.getCode());
				return result;
			}
			
			//获取签名和时间戳
			Long timestamp = Long.parseLong((String)paramMap.get(timeStampName));
			String assign = (String)paramMap.get(assignName);
			
			
			//权限校验
			PublicKeyVerifyCode code = accessVerifyService.verify(timestamp, assign, verifyFlag);
			if(code != PublicKeyVerifyCode.ACCESSSUCCESS){
				result.setSuccess(false);
				result.setDate(now);
				result.setMessage(code.getDesc());
				result.setCode(ResultCode.ACCESSERROR.getCode());
				return result;
			}
			
			return joinPoint.proceed(joinPoint.getArgs());
			
		}catch(Throwable e){
			result.setSuccess(false);
			result.setDate(now);
			result.setMessage(e.toString());
			result.setCode(ResultCode.ERROR.getCode());
			logger.error("aop切入异常" + e);
			return result;
		}
	}
}
