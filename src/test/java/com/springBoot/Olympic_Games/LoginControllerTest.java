package com.springBoot.Olympic_Games;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testLogin_wrongPassword() throws Exception {
		mockMvc
			.perform(formLogin("/login")
				.user("userName")
				.password("WrongPassword"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrlPattern("/login?error"));
	}
	
	@Test
	public void testLogin_correctPassword() throws Exception {
		mockMvc
			.perform(formLogin("/login")
				.user("user")
				.password("user123!"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/sport"));
	}
	
	@Test
	public void testLogout() throws Exception {
		mockMvc
			.perform(get("/login").param("logout", "logout"))
			.andExpect(status().isOk())
			.andExpect(view().name("login"))
			.andExpect(model().attributeExists("msg"));
	}
	
	@Test
	public void testError() throws Exception {
		mockMvc
			.perform(get("/login").param("error", "error"))
			.andExpect(status().isOk())
			.andExpect(view().name("login"))
			.andExpect(model().attributeExists("error"));
	}
	
}
