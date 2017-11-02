package com.yunfang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * @author chenyu23
 * @data 2017年11月2日 上午9:20:13
 * @Description TODO
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
@Documented
public @interface AuthVerify {
	
	/**
	 * 校验唯一性标识，会进行比对，查看该用户是否有相应权限，主要做校验
	 * @return
	 */
	public String verifyFlag() default "";
	
	/**
	 * 校验参数名，默认是time_stamp和access_signature
	 * @return
	 */
	public String timeStamp() default "time_stamp";
	public String access_sign() default "access_signature";
	
}
