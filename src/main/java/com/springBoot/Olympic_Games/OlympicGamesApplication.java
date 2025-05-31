package com.springBoot.Olympic_Games;

import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import perform.PerformRest;
import service.GameService;
import service.MyUserDetailsService;
import service.SportService;
import service.TicketService;
import validator.GameValidator;
import validator.TicketValidator;

@SpringBootApplication
@EnableJpaRepositories("repository")
@EntityScan("domain")
public class OlympicGamesApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(OlympicGamesApplication.class, args);
		
		try {
			new PerformRest();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/",	"/sport");
		registry.addViewController("/403").setViewName("403");
	}
	
	// -----------------------------------------------------------------------------------------------------
	
	@Bean
	LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}
	
	// -----------------------------------------------------------------------------------------------------
	
	@Bean
	MyUserDetailsService myUserDetailsService() {
		return new MyUserDetailsService();
	}
	
	@Bean
	GameService gameService() {
		return new GameService();
	}
	
	@Bean
	SportService sportService() {
		return new SportService();
	}
	
	@Bean
	TicketService ticketService() {
		return new TicketService();
	}
	
	// -----------------------------------------------------------------------------------------------------
	
	@Bean
	GameValidator gameValidator() {
		return new GameValidator();
	}
	
	@Bean
	TicketValidator ticketValidator() {
		return new TicketValidator();
	}
	
	// -----------------------------------------------------------------------------------------------------
	
	@Bean
	SimpleMappingExceptionResolver simpleMappingExceptionResolver(){
		SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
		Properties mappings = new Properties();
		r.setDefaultErrorView("error");
		r.setExceptionMappings(mappings);
		return r;
	}
}
