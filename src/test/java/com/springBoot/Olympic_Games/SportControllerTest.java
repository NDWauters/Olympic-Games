package com.springBoot.Olympic_Games;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import domain.Sport;
import service.SportService;
import service.TicketService;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SportControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private SportService mockSportService;
	
	@MockBean
	private TicketService mockTicketService;
	
	private List<Sport> sports = List.of(new Sport(1L,"Football"),new Sport(2L,"Boxing"));
	
	@BeforeEach
	public void before() {
		Mockito
			.when(mockSportService.get())
			.thenReturn(sports);
		
		Mockito
			.when(mockTicketService.getAmountOverAllGames("user"))
			.thenReturn(5);
	}
	
	@WithAnonymousUser
	@Test
	public void testSportListGet_NoAccessAnonymous() throws Exception {
	    mockMvc
	    	.perform(get("/sport"))
            .andExpect(redirectedUrlPattern("**/login"));
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSportListGet_WithUserRole() throws Exception {
		mockMvc
			.perform(get("/sport"))
			.andExpect(status().isOk())
			.andExpect(view().name("/sport/index"))
			.andExpect(model().attributeExists("sports"))
			.andExpect(model().attributeExists("amountTickets"))
			.andExpect(model().attribute("sports", sports))
			.andExpect(model().attribute("amountTickets", 5));
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSportListGet_WithAdminRole() throws Exception {
		mockMvc
			.perform(get("/sport"))
			.andExpect(status().isOk())
			.andExpect(view().name("/sport/index"))
			.andExpect(model().attributeExists("sports"))
			.andExpect(model().attributeExists("amountTickets"))
			.andExpect(model().attribute("sports", sports))
			.andExpect(model().attribute("amountTickets", 0));
	}
	
}
