package com.mxt.price.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.mxt.price.utils.CommonUtils;

/**
 * @author chenyu23
 * @data 2017年7月20日 下午4:42:46
 * @Description 
 * 异常处理器，继承SimpleMappingExceptionResolver类
 * 当捕获异常时会调用doResolveException方法
 */
public class ExceptionHandler extends SimpleMappingExceptionResolver{

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class); 
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {
			logger.error(CommonUtils.exceptionToStr(ex));	//打印详细日志
			return super.doResolveException(request, response, handler, ex);
	}
}
