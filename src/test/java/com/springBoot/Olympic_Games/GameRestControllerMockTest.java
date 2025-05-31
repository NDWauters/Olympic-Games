package com.springBoot.Olympic_Games;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static utils.InitFormatter.DATEFORMATTER;
import static utils.InitFormatter.TIMEFORMATTER;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import domain.Discipline;
import domain.Game;
import domain.Sport;
import domain.Stadium;
import exceptions.GameNotFoundException;
import exceptions.SportNotFoundException;
import service.GameService;
import service.SportService;

@SpringBootTest
public class GameRestControllerMockTest {

	@Mock
	private GameService mockGameService;
	
	@Mock
	private SportService mockSportService;
	
	private GameRestController controller;
	private MockMvc mockMvc;
	
	private String expectedFormattedLocalDate;
	private String expectedFormattedLocalTime;
	
	private final int ID = 1;
	private final Sport sport = new Sport(1,"Football");
	
	private Game aGame(
		long id, 
		LocalDate date, 
		LocalTime time, 
		double price, 
		int seats, 
		int seatsSold, 
		String olympicNr1, 
		String olympicNr2, 
		Set<Discipline> disciplines) {
		
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
			new ArrayList<>(),
			0L);
		
		this.expectedFormattedLocalDate = game.getDate().format(DATEFORMATTER);
		this.expectedFormattedLocalTime = game.getTime().format(TIMEFORMATTER);
		return game;
	}
	
	private Discipline aDiscipline(long id, String name) {
		return new Discipline(id,  name, Set.of(sport));
	}
	
	@BeforeEach
	public void before() {
		MockitoAnnotations.openMocks(this);
		controller = new GameRestController();
		mockMvc = standaloneSetup(controller).build();
		ReflectionTestUtils.setField(controller, "gameService", mockGameService);
		ReflectionTestUtils.setField(controller, "sportService", mockSportService);
		
		Mockito
			.when(mockSportService.get(ID))
			.thenReturn(sport);
	}
	
	@Test
	public void testGetGamesBySport_emptyList() throws Exception{
		Mockito
			.when(mockGameService.getGamesBySport(sport))
			.thenReturn(new ArrayList<>());
		
		mockMvc
			.perform(get("/rest/game/" + ID))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isEmpty());
		
		Mockito.verify(mockGameService).getGamesBySport(sport);
	}
	
	@Test
	public void testGetGamesBySport_noEmptyList() throws Exception{
		
		Set<Discipline> disciplines1 = new HashSet<>();
		disciplines1.addAll(List.of(aDiscipline(1, "Men's Team")));
		Game game1 = aGame(1L, LocalDate.of(2024, 7, 26), LocalTime.of(10, 0), 0.50, 20, 5, "12345", "13345", disciplines1);
		String expectedFormattedLocalDate1 = expectedFormattedLocalDate;
		String expectedFormattedLocalTime1 = expectedFormattedLocalTime;
		
		Set<Discipline> disciplines2 = new HashSet<>();
		Game game2 = aGame(2L, LocalDate.of(2024, 7, 27), LocalTime.of(12, 0), 10.50, 50, 10, "32345", "33300", disciplines2);
		String expectedFormattedLocalDate2 = expectedFormattedLocalDate;
		String expectedFormattedLocalTime2 = expectedFormattedLocalTime;
		
		List<Game> games = List.of(game1,game2);
		
		Mockito
			.when(mockGameService.getGamesBySport(sport))
			.thenReturn(games);
		
		mockMvc
			.perform(get("/rest/game/" + ID))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isNotEmpty())
			
			.andExpect(jsonPath("$[0].game_id").value(1L))
			.andExpect(jsonPath("$[0].date").value(expectedFormattedLocalDate1))
			.andExpect(jsonPath("$[0].time").value(expectedFormattedLocalTime1))
			.andExpect(jsonPath("$[0].ticket_price").value(0.50))
			.andExpect(jsonPath("$[0].olympic_nr_1").value("12345"))
			.andExpect(jsonPath("$[0].olympic_nr_2").value("13345"))
			.andExpect(jsonPath("$[0].seats_available").value(15))
			.andExpect(jsonPath("$[0].sport").value("Football"))
			.andExpect(jsonPath("$[0].stadium").value("test stadium"))
			.andExpect(jsonPath("$[0].disciplines").value("Men's Team"))
			
			.andExpect(jsonPath("$[1].game_id").value(2L))
			.andExpect(jsonPath("$[1].date").value(expectedFormattedLocalDate2))
			.andExpect(jsonPath("$[1].time").value(expectedFormattedLocalTime2))
			.andExpect(jsonPath("$[1].ticket_price").value(10.50))
			.andExpect(jsonPath("$[1].olympic_nr_1").value("32345"))
			.andExpect(jsonPath("$[1].olympic_nr_2").value("33300"))
			.andExpect(jsonPath("$[1].seats_available").value(40))
			.andExpect(jsonPath("$[1].sport").value("Football"))
			.andExpect(jsonPath("$[1].stadium").value("test stadium"))
			.andExpect(jsonPath("$[1].disciplines").value(""));
		
		Mockito.verify(mockGameService).getGamesBySport(sport);
	}
	
	@Test
	public void testGetGamesBySport_sportNotFound() throws Exception{
		
		int unknownID = 99;
		
		Mockito
			.when(mockSportService.get(unknownID))
			.thenThrow(new SportNotFoundException(unknownID));
		
		Exception exception = assertThrows(Exception.class, () -> {
			mockMvc.perform(get("/rest/game/" + unknownID)).andReturn();
		});
		
		assertTrue(exception.getCause() instanceof SportNotFoundException);
		
		Mockito.verify(mockSportService).get(unknownID);
	}
	
	@Test
	public void testgetAvailableSeatsForGame_availableSeatsForGameGiven() throws Exception{
		
		Game game = aGame(1L, LocalDate.of(2024, 07, 27), LocalTime.of(12, 0), 10.5, 50, 25, "12345", "13345", new HashSet<>());
		
		Mockito
			.when(mockGameService.getGame(ID))
			.thenReturn(game);
		
		mockMvc
			.perform(get("/rest/game/seats/" + ID))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(25));
		
		Mockito.verify(mockGameService).getGame(ID);
	}
	
	@Test
	public void testgetAvailableSeatsForGame_gameNotFound() throws Exception{
		
		int unknownID = 999;
		
		Mockito
			.when(mockGameService.getGame(unknownID))
			.thenThrow(new GameNotFoundException(unknownID));
		
		Exception exception = assertThrows(Exception.class, () -> {
			mockMvc.perform(get("/rest/game/seats/" + unknownID)).andReturn();
		});
		
		assertTrue(exception.getCause() instanceof GameNotFoundException);
		
		Mockito.verify(mockGameService).getGame(unknownID);
	}
}
