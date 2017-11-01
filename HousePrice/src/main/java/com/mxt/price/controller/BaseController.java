package com.mxt.price.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author maoxiaotai
 * @data 2017年10月20日 下午4:10:36
 * @Description BaseController
 */
@Controller
public class BaseController {

	@Resource
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    @ModelAttribute
    public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
    
    private <T> Map<String,Object> renderMap(String message , int code ,T data){
    	Map<String, Object> jsonMap = new HashMap<String, Object>();
    	jsonMap.put("code", code);
    	jsonMap.put("msg", message);
    	jsonMap.put("data", data);
    	return jsonMap;
    }
    
    protected <T> Map<String,Object> successResult(String message , T data) {
		return renderMap(message, 200, data);
	}
    
    protected <T> Map<String,Object> successResult(T data) {
		return renderMap(null, 200, data);
	}
    
    protected <T> Map<String,Object> successResult(String message) {
		return renderMap(message, 200, null);
	}
    
    protected <T> Map<String,Object> successResult() {
		return renderMap(null, 200, null);
	}
    
    protected <T> Map<String,Object> failResult(String message , T data) {
		return renderMap(message, 500, data);
	}
    
    protected <T> Map<String,Object> failResult(String message) {
		return renderMap(message, 500, null);
	}
    
    protected <T> Map<String,Object> failResult() {
		return renderMap(null, 500, null);
	}
}
