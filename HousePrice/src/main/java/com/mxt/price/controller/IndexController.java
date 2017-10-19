package com.mxt.price.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController{
	
	@RequestMapping("/index")
	public String index(Model model) {
		System.out.println(2/0);
		return "index";
	}
	
	@RequestMapping("/error404")
	public String error404(Model model) {
		return "index";
	}
}
