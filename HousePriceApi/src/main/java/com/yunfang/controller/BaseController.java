package com.yunfang.controller;

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
}
