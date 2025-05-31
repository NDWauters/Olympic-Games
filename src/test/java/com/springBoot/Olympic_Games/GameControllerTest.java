package com.springBoot.Olympic_Games;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import domain.Discipline;
import domain.Game;
import domain.Sport;
import domain.Stadium;
import exceptions.SportNotFoundException;
import exceptions.StadiumNotFoundException;
import service.GameService;
import service.SportService;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private GameService mockGameService;
	
	@MockBean
	private SportService mockSportService;
	
	private long ID = 1L;
	private long unknownID = 99L;
	private Sport sport;
	private List<Game> games;
	
	@BeforeEach
	public void before() {
		this.sport = new Sport(ID,"Football");
		
		Set<Discipline> disciplines1 = new HashSet<>();
		Set<Discipline> disciplines2 = new HashSet<>();
		
		disciplines1.addAll(List.of(aDiscipline(1, "Men's Team")));
		
		Game game1 = aGame(1L, LocalDate.of(2024, 7, 26), LocalTime.of(10, 0), 0.50, 20, 5, "12345", "13345", disciplines1, new ArrayList<>(), 1L);
		
		Game game2 = aGame(2L, LocalDate.of(2024, 7, 27), LocalTime.of(12, 0), 10.50, 50, 10, "32345", "33300", disciplines2, new ArrayList<>(), 1L);
		
		this.games = List.of(game1,game2);
		
		when(mockSportService.get(ID)).thenReturn(sport);
		when(mockGameService.getGamesBySport(sport)).thenReturn(games);
		when(mockGameService.isUniqueOlympicNr(game1.getOlympicNr1())).thenReturn(true);
		when(mockGameService.isUniqueOlympicNr(game2.getOlympicNr1())).thenReturn(true);
	}
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGamesBySport_unknownSport_showsErrorPage() throws Exception {
		
		int unknownID = 99;
		
		when(mockSportService.get(unknownID))
			.thenThrow(new SportNotFoundException(unknownID));
		
		mockMvc
			.perform(get("/game/" + unknownID))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockSportService).get(unknownID);
	}
	
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testGamesBySport_withUserRole_showsIndexPage() throws Exception {
		mockMvc
			.perform(get("/game/" + ID))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/index"))
			.andExpect(model().attributeExists("userName"))
			.andExpect(model().attributeExists("sport"))
			.andExpect(model().attributeExists("games"))
			.andExpect(model().attribute("userName", "user"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("games", games));
	}
	
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testGamesBySport_withAdminRole_showsIndexPage() throws Exception {
		mockMvc
			.perform(get("/game/" + ID))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/index"))
			.andExpect(model().attributeExists("userName"))
			.andExpect(model().attributeExists("sport"))
			.andExpect(model().attributeExists("games"))
			.andExpect(model().attribute("userName", "admin"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("games", games));
	}
	
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testCreateGame_GET_withUserRole_showsAccessDeniedPage() throws Exception {
		mockMvc
			.perform(get("/game/create/" + ID))
			.andExpect(status().isForbidden());
	}
	
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testCreateGame_GET_withAdminRole_unknownSport_showsErrorPage() throws Exception {
		
		when(mockSportService.get(unknownID))
			.thenThrow(new SportNotFoundException(unknownID));
		
		mockMvc
			.perform(get("/game/create/" + unknownID))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
	}
	
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testCreateGame_GET_withAdminRole_showsCreateForm() throws Exception {
		mockMvc
			.perform(get("/game/create/" + ID))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/create"))
			.andExpect(model().attributeExists("sport", "game"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("game", new Game()));
	}
	
	
	@WithMockUser(username = "user", roles = { "USER" })
	@Test
	public void testSubmitGame_POST_withUserRole_showsAccessDeniedPage() throws Exception {
		mockMvc
			.perform(post("/game/create/" + ID))
			.andExpect(status().isForbidden());
	}
	
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_dateOutOfRange_showsFormAgain() throws Exception {
		
		// date is out of range
		Game invalidGame = aGame(10L, LocalDate.of(2024, 8, 20), LocalTime.of(10,0), 20, 50, 20, "14566", "15500", new HashSet<>(), new ArrayList<>(), 1L);
		
		Mockito.when(mockGameService.isUniqueOlympicNr(invalidGame.getOlympicNr1()))
		.thenReturn(true);
		
		mockMvc
			.perform(post("/game/create/" + ID).with(csrf()).flashAttr("game", invalidGame))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/create"))
			.andExpect(model().attributeExists("sport", "game"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("game", invalidGame))
			.andExpect(model().errorCount(1));
		
		Mockito.verify(mockGameService).isUniqueOlympicNr(invalidGame.getOlympicNr1());
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_timeOutOfRange_showsFormAgain() throws Exception {
		
		// date is out of range
		Game invalidGame = aGame(10L, LocalDate.of(2024, 7, 26), LocalTime.of(7,0), 20, 50, 20, "14566", "15500", new HashSet<>(), new ArrayList<>(), 1L);
		
		Mockito.when(mockGameService.isUniqueOlympicNr(invalidGame.getOlympicNr1()))
		.thenReturn(true);
		
		mockMvc
			.perform(post("/game/create/" + ID).with(csrf()).flashAttr("game", invalidGame))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/create"))
			.andExpect(model().attributeExists("sport", "game"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("game", invalidGame))
			.andExpect(model().errorCount(1));
		
		Mockito.verify(mockGameService).isUniqueOlympicNr(invalidGame.getOlympicNr1());
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_stadiumEmpty_showsFormAgain() throws Exception {
		
		// date is out of range
		Game invalidGame = aGame(10L, LocalDate.of(2024, 7, 26), LocalTime.of(10,0), 20, 50, 20, "14566", "15500", new HashSet<>(), new ArrayList<>(), -1L);
		
		Mockito.when(mockGameService.isUniqueOlympicNr(invalidGame.getOlympicNr1()))
		.thenReturn(true);
		
		mockMvc
			.perform(post("/game/create/" + ID).with(csrf()).flashAttr("game", invalidGame))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/create"))
			.andExpect(model().attributeExists("sport", "game"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("game", invalidGame))
			.andExpect(model().errorCount(1));
		
		Mockito.verify(mockGameService).isUniqueOlympicNr(invalidGame.getOlympicNr1());
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_tooManyDisciplines_showsFormAgain() throws Exception {
		
		List<Long> disciplineIds = new ArrayList<>();
		disciplineIds.addAll(List.of(1L,2L,3L));
		
		// date is out of range
		Game invalidGame = aGame(10L, LocalDate.of(2024, 7, 26), LocalTime.of(10,0), 20, 50, 20, "14566", "15500", new HashSet<>(), disciplineIds, 1L);
		
		Mockito.when(mockGameService.isUniqueOlympicNr(invalidGame.getOlympicNr1()))
		.thenReturn(true);
		
		mockMvc
			.perform(post("/game/create/" + ID).with(csrf()).flashAttr("game", invalidGame))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/create"))
			.andExpect(model().attributeExists("sport", "game"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("game", invalidGame))
			.andExpect(model().errorCount(1));
		
		Mockito.verify(mockGameService).isUniqueOlympicNr(invalidGame.getOlympicNr1());
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_olympicNr1NotUnique_showsFormAgain() throws Exception {
		
		Game game = aGame(10L, LocalDate.of(2024, 7, 27), LocalTime.of(10,0), 20, 50, 20, "14566", "15500", new HashSet<>(), new ArrayList<>(), 1L);
		
		Mockito.when(mockGameService.isUniqueOlympicNr(game.getOlympicNr1()))
			.thenReturn(false);
		
		mockMvc
			.perform(post("/game/create/" + ID).with(csrf()).flashAttr("game", game))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/create"))
			.andExpect(model().attributeExists("sport", "game"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("game", game))
			.andExpect(model().errorCount(1));
		
		Mockito.verify(mockGameService).isUniqueOlympicNr(game.getOlympicNr1());
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_olympicNr2OutOfRange_showsFormAgain() throws Exception {
		
		// date is out of range
		Game invalidGame = aGame(10L, LocalDate.of(2024, 7, 26), LocalTime.of(10,0), 20, 50, 20, "14566", "16500", new HashSet<>(), new ArrayList<>(), 1L);
		
		Mockito.when(mockGameService.isUniqueOlympicNr(invalidGame.getOlympicNr1()))
			.thenReturn(true);
		
		mockMvc
			.perform(post("/game/create/" + ID).with(csrf()).flashAttr("game", invalidGame))
			.andExpect(status().isOk())
			.andExpect(view().name("/game/create"))
			.andExpect(model().attributeExists("sport", "game"))
			.andExpect(model().attribute("sport", sport))
			.andExpect(model().attribute("game", invalidGame))
			.andExpect(model().errorCount(1));
		
		Mockito.verify(mockGameService).isUniqueOlympicNr(invalidGame.getOlympicNr1());
	}
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_unknownSport_showsErrorPage() throws Exception {
		
		Game game = games.get(0);
		
		doThrow(new SportNotFoundException(unknownID)).when(mockGameService).createGame(unknownID, game);
		
		mockMvc
			.perform(post("/game/create/" + unknownID).with(csrf()).flashAttr("game", game))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockGameService).isUniqueOlympicNr(game.getOlympicNr1());
	}
	
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_unknownStadium_showsErrorPage() throws Exception {
		
		Game game = games.get(0);
		
		doThrow(new StadiumNotFoundException(ID)).when(mockGameService).createGame(ID, game);
		
		mockMvc
			.perform(post("/game/create/" + ID).with(csrf()).flashAttr("game", game))
			.andExpect(status().isOk())
			.andExpect(view().name("error"));
		
		verify(mockGameService).isUniqueOlympicNr(game.getOlympicNr1());
	}
	
	
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Test
	public void testSubmitGame_POST_withAdminRole_createSucceeded_showsIndexPage() throws Exception {
		
		Game game = games.get(0);
		
		mockMvc
			.perform(post("/game/create/" + ID).with(csrf()).flashAttr("game", game))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/game/" + ID));
		
		verify(mockGameService).isUniqueOlympicNr(game.getOlympicNr1());
	}
	
	
	private Game aGame(
		long id, 
		LocalDate date, 
		LocalTime time, 
		double price, 
		int seats, 
		int seatsSold, 
		String olympicNr1, 
		String olympicNr2, 
		Set<Discipline> disciplines,
		List<Long> displineIds,
		long stadiumId) {
		
		Game game = new Game(
			id, 
			date, 
			time, 
			price, 
			seats, 
			seatsSold, 
			olympicNr1, 
			olympicNr2,
			sport, 
			Stadium.builder().Id(1L).name("test stadium").build(),
			disciplines,
			new HashSet<>(),
			displineIds,
			stadiumId);
		
		return game;
	}
	
	private Discipline aDiscipline(long id, String name) {
		return new Discipline(id,  name, Set.of(sport));
	}
}
