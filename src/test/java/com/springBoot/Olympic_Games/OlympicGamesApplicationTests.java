package com.springBoot.Olympic_Games;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
class OlympicGamesApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testLoginGet() throws Exception {
    	mockMvc
    		.perform(get("/login"))
	        .andExpect(status().isOk())
	    	.andExpect(view().name("login"));
	}
	
	@Test
	public void testAccessDeniedPageGet() throws Exception {
    	mockMvc
    		.perform(get("/403"))
	        .andExpect(status().isOk())
	    	.andExpect(view().name("403"));
	}
	
	@WithAnonymousUser
	@Test
	public void testNoAccessAnonymous() throws Exception {
	    mockMvc
	    	.perform(get("/"))
            .andExpect(redirectedUrlPattern("**/login"));
	}
}
