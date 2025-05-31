package com.springBoot.Olympic_Games;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import exceptions.GameNotFoundException;
import exceptions.SportNotFoundException;

@RestControllerAdvice
public class GameErrorAdvice {
	
	@ResponseBody
	@ExceptionHandler(SportNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String sportNotFoundException(SportNotFoundException ex) {
		return ex.getMessage();
	}
	
	@ResponseBody
	@ExceptionHandler(GameNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String gameNotFoundException(GameNotFoundException ex) {
		return ex.getMessage();
	}
	
	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String generalException(Exception ex) {
		return "Something went wrong";
	}
}
