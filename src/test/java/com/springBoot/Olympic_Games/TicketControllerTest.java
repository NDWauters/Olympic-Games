package com.springBoot.Olympic_Games;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import domain.Game;
import domain.MyUser;
import domain.Sport;
import domain.Stadium;
import domain.UserGame;
import domain.UserGameKey;
import dto.ticketDTO;
import exceptions.GameNotFoundException;
import exceptions.UserNotFoundException;
import service.GameService;
import service.TicketService;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TicketControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private GameService mockGameService;
	
	@MockBean
	private TicketService mockTicketService;
	
	private Game game;
	private Sport sport;
	private Stadium stadium;
	private MyUser user;
	private ticketDTO ticketDTO;
	private List<UserGame> tickets;
	private String unknownUserName = "userName";
	private long ID = 1L;
	
	@BeforeEach
	public void before() {
		
		sport = new Sport(1L, "Football");
		stadium = new Stadium(1L, "test stadium");
		
		user = MyUser
			.builder()
			.id(1L)
			.userName("user")
			.password("paasword")
			.build();
		
		game = Game
			.builder()
			.id(ID)
			.date(LocalDate.of(2024, 7, 27))
			.time(LocalTime.of(10, 0))
			.price(20.5)
			.seats(50)
			.seatsSold(10)
			.olympicNr1("12345")
			.olympicNr1("13345")
			.sport(sport)
			.stadium(stadium)
			.build();
		
		tickets = List.of(new UserGame(new UserGameKey(1L, ID), user, game, 5));
		
		ticketDTO = new ticketDTO(sport.getId(), game.getId(), user.getUserName(), 5, 5);
	}
	
	@WithMockUser(username = "userName", roles = { "USER" })
	@Test
	public void testTicketsForUsers_withUserRole_unknownUser_showsErrorPage() throws Exception {
		
		when(mockTicketService.getTicketsForUser(unknownUserName))
			.thenThrow(new UserNotFoundException(unknownUserName));
		
		mockMvc
			.perform(get("/ticket"))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockTicketService).getTicketsForUser(unknownUserName);
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testTicketsForUsers_withAdminRole_showsAccessDeniedPage() throws Exception {
		mockMvc
			.perform(get("/ticket"))
			.andExpect(status().isForbidden());
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testTicketsForUsers_withUserRole_showsIndexPage() throws Exception {
		
		when(mockTicketService.getTicketsForUser(user.getUserName()))
			.thenReturn(tickets);
		
		mockMvc
			.perform(get("/ticket"))
			.andExpect(status().isOk())
			.andExpect(view().name("/ticket/index"))
			.andExpect(model().attributeExists("tickets"))
			.andExpect(model().attribute("tickets", tickets));
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testBuyTickets_GET_withAdminRole_showsAccessDeniedPage() throws Exception {
		mockMvc
			.perform(get("/ticket/buy/" + ID))
			.andExpect(status().isForbidden());
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testBuyTickets_GET_withUserRole_unknownGame_showsErrorPage() throws Exception {
		
		long unknownID = 99L;
		
		when(mockGameService.getGame(unknownID))
			.thenThrow(new GameNotFoundException(unknownID));
		
		mockMvc
			.perform(get("/ticket/buy/" + unknownID))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockGameService).getGame(unknownID);
	}
	
	@WithMockUser(username = "userName", roles = { "USER" })
	@Test
	public void testBuyTickets_GET_withUserRole_unknownUser_showsErrorPage() throws Exception {
		
		when(mockGameService.getGame(ID))
			.thenReturn(game);
		
		when(mockTicketService.getTicketForForm(game, unknownUserName))
			.thenThrow(new UserNotFoundException(unknownUserName));
		
		mockMvc
			.perform(get("/ticket/buy/" + ID))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockGameService).getGame(ID);
		verify(mockTicketService).getTicketForForm(game, unknownUserName);
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testBuyTickets_GET_withUserRole_showsBuyForm() throws Exception {
		
		when(mockGameService.getGame(ID))
			.thenReturn(game);
	
		when(mockTicketService.getTicketForForm(game, user.getUserName()))
			.thenReturn(ticketDTO);
		
		mockMvc
			.perform(get("/ticket/buy/" + ID))
			.andExpect(status().isOk())
			.andExpect(view().name("/ticket/buy"))
			.andExpect(model().attributeExists("game", "ticketDTO"))
			.andExpect(model().attribute("game", game))
			.andExpect(model().attribute("ticketDTO", ticketDTO));
		
		verify(mockGameService).getGame(ID);
		verify(mockTicketService).getTicketForForm(game, user.getUserName());
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitTickets_POST_withAdminRole_showsAccessDeniedPage() throws Exception {
		mockMvc
			.perform(post("/ticket/buy/" + ID))
			.andExpect(status().isForbidden());
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_amountTooLow_showsFormAgain() throws Exception {
		
		when(mockGameService.getGame(ID))
			.thenReturn(game);
		
		ticketDTO invalidTicket = new ticketDTO(game.getSport().getId(), game.getId(), user.getUserName(), 0, 0);
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", invalidTicket))
			.andExpect(status().isOk())
			.andExpect(view().name("/ticket/buy"))
			.andExpect(model().attributeExists("game", "ticketDTO"))
			.andExpect(model().attribute("game", game))
			.andExpect(model().attribute("ticketDTO", invalidTicket));
		
		verify(mockGameService).getGame(ID);
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_amountTooHigh_showsFormAgain() throws Exception {
		
		when(mockGameService.getGame(ID))
			.thenReturn(game);
		
		ticketDTO invalidTicket = new ticketDTO(game.getSport().getId(), game.getId(), user.getUserName(), 0, 21);
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", invalidTicket))
			.andExpect(status().isOk())
			.andExpect(view().name("/ticket/buy"))
			.andExpect(model().attributeExists("game", "ticketDTO"))
			.andExpect(model().attribute("game", game))
			.andExpect(model().attribute("ticketDTO", invalidTicket));
		
		verify(mockGameService).getGame(ID);
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_amountWithPreviouslyBoughtTooHigh_showsFormAgain() throws Exception {
		
		when(mockGameService.getGame(ID))
			.thenReturn(game);
		
		ticketDTO invalidTicket = new ticketDTO(game.getSport().getId(), game.getId(), user.getUserName(), 10, 11);
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", invalidTicket))
			.andExpect(status().isOk())
			.andExpect(view().name("/ticket/buy"))
			.andExpect(model().attributeExists("game", "ticketDTO"))
			.andExpect(model().attribute("game", game))
			.andExpect(model().attribute("ticketDTO", invalidTicket));
		
		verify(mockGameService).getGame(ID);
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_amountOverAllGamesTooHigh_showsFormAgain() throws Exception {
		
		ticketDTO invalidTicket = new ticketDTO(game.getSport().getId(), game.getId(), user.getUserName(), 0, 5);
		
		when(mockGameService.getGame(ID))
			.thenReturn(game);
		
		when(mockTicketService.getAmountOverAllGames(user.getUserName()))
			.thenReturn(96);
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", invalidTicket))
			.andExpect(status().isOk())
			.andExpect(view().name("/ticket/buy"))
			.andExpect(model().attributeExists("game", "ticketDTO"))
			.andExpect(model().attribute("game", game))
			.andExpect(model().attribute("ticketDTO", invalidTicket));
		
		verify(mockGameService).getGame(ID);
		verify(mockTicketService).getAmountOverAllGames(user.getUserName());
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_availableSeatsInsufficient_showsFormAgain() throws Exception {
		
		ticketDTO invalidTicket = new ticketDTO(game.getSport().getId(), game.getId(), user.getUserName(), 0, 5);
		
		when(mockGameService.getGame(ID))
			.thenReturn(game);
		
		when(mockTicketService.getAmountOverAllGames(user.getUserName()))
			.thenReturn(0);
		
		when(mockTicketService.hasGameSufficientSeats(invalidTicket.gameId(), invalidTicket.amount()))
		.thenReturn(false);
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", invalidTicket))
			.andExpect(status().isOk())
			.andExpect(view().name("/ticket/buy"))
			.andExpect(model().attributeExists("game", "ticketDTO"))
			.andExpect(model().attribute("game", game))
			.andExpect(model().attribute("ticketDTO", invalidTicket));
		
		verify(mockGameService).getGame(ID);
		verify(mockTicketService).getAmountOverAllGames(user.getUserName());
		verify(mockTicketService).hasGameSufficientSeats(invalidTicket.gameId(), invalidTicket.amount());
	}
	
	@WithMockUser(username = "userName", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_unknownUser_showsErrorPage() throws Exception {
		
		ticketDTO ticketDtoWithUnknownUser = new ticketDTO(game.getSport().getId(), game.getId(), unknownUserName, 0, 5);
		
		// first service call in validator that checks this
		when(mockTicketService.getAmountOverAllGames(unknownUserName))
			.thenThrow(new UserNotFoundException(unknownUserName));
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", ticketDtoWithUnknownUser))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockTicketService).getAmountOverAllGames(unknownUserName);
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_unknownGame_showsErrorPage() throws Exception {
		
		long unknownID = 99;
		
		ticketDTO ticketDtoWithUnknownGame = new ticketDTO(game.getSport().getId(), unknownID, user.getUserName(), 0, 5);
		
		// first service call in validator that checks this
		when(mockTicketService.hasGameSufficientSeats(unknownID, ticketDtoWithUnknownGame.amount()))
			.thenThrow(new GameNotFoundException(unknownID));
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", ticketDtoWithUnknownGame))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockTicketService).hasGameSufficientSeats(unknownID, ticketDtoWithUnknownGame.amount());
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_errorWhenCreating_showsErrorPage() throws Exception {
		
		// first service call in validator that checks User
		when(mockTicketService.getAmountOverAllGames(ticketDTO.userName()))
			.thenReturn(0);
		
		// first service call in validator that checks Game
		when(mockTicketService.hasGameSufficientSeats(ticketDTO.gameId(), ticketDTO.amount()))
			.thenReturn(true);
		
		// service call for adding tickets
		doThrow(new RuntimeException()).when(mockTicketService).buyTickets(ticketDTO.gameId(), ticketDTO.userName(), ticketDTO);
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", ticketDTO))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockTicketService).buyTickets(ticketDTO.gameId(), ticketDTO.userName(), ticketDTO);
		verify(mockTicketService).getAmountOverAllGames(ticketDTO.userName());
		verify(mockTicketService).hasGameSufficientSeats(ticketDTO.gameId(), ticketDTO.amount());
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitTickets_POST_withUserRole_createSucceeded_showsIndexPageWithMessage() throws Exception {
		
		// first service call in validator that checks User
		when(mockTicketService.getAmountOverAllGames(ticketDTO.userName()))
			.thenReturn(0);
		
		// first service call in validator that checks Game
		when(mockTicketService.hasGameSufficientSeats(ticketDTO.gameId(), ticketDTO.amount()))
			.thenReturn(true);
		
		mockMvc
			.perform(post("/ticket/buy/" + ID).with(csrf()).flashAttr("ticketDTO", ticketDTO))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/game/" + ID))
			.andExpect(flash().attributeExists("ticketsBoughtMsg"));
		
		verify(mockTicketService).buyTickets(ticketDTO.gameId(), ticketDTO.userName(), ticketDTO);
		verify(mockTicketService).getAmountOverAllGames(ticketDTO.userName());
		verify(mockTicketService).hasGameSufficientSeats(ticketDTO.gameId(), ticketDTO.amount());
	}
}
