package com.springBoot.Olympic_Games;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

public class BaseController {

	@ExceptionHandler(Exception.class)
	public ModelAndView handleCustomException(Exception ex){
		ModelAndView model = new ModelAndView("error");
		return model;
	}
	
}
