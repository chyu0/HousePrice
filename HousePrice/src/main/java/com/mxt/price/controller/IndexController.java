package com.mxt.price.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页，错误页等
 * @author maoxiaotai
 * @data 2017年10月31日 上午10:27:24
 */
@Controller
public class IndexController extends BaseController{
	
	@RequestMapping("/index")
	public String index(Model model) {
		return "index";
	}
	
	@RequestMapping("/error404")
	public String error404(Model model) {
		return "index";
	}
}
