package com.yunfang.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yunfang.modal.Email;
import com.yunfang.utils.CommonUtils;
import com.yunfang.utils.EmailUtils;

/**
 * 异常告警
 * @author maoxiaotai
 * @data 2017年12月19日 下午9:00:43
 * @Description TODO
 */
@Component
@Aspect
public class ExceptionAspect {
	
	private static Logger logger = LoggerFactory.getLogger(ExceptionAspect.class);
	
	@AfterThrowing(pointcut="execution(* com.yunfang..*.*(..))",throwing="ex")
	public void throwEx(JoinPoint joinpoint, Throwable ex){
		StringBuffer str = new StringBuffer();
		str.append("method:").append(joinpoint.getSignature().getName());
		str.append(",exception:").append(CommonUtils.exceptionToStr(ex));
		Email email = new Email();
		email.setContent(str.toString());
		email.setSubject("HousePriceApi异常");
		email.setTo(Arrays.asList(new String[]{"1939861002@qq.com"}));
		EmailUtils.sendEmail(email);
		logger.error("系统异常：" + str.toString());
	}
	
}
