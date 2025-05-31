package com.springBoot.Olympic_Games;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domain.Game;
import domain.Sport;
import service.GameService;
import service.SportService;

@RestController
@RequestMapping("/rest")
public class GameRestController {

	@Autowired
	private GameService gameService;
	@Autowired
	private SportService sportService;
	
	@GetMapping("/game/{sportid}")
	public List<Game> getGamesBySport(@PathVariable int sportid){
		
		Sport sport = sportService.get(sportid);
		
		return gameService.getGamesBySport(sport);
	}
	
	@GetMapping("/game/seats/{gameid}")
	public Integer getAvailableSeatsForGame(@PathVariable int gameid){
		
		Game game = gameService.getGame(gameid);
		
		return game.getSeatsAvailable();
	}
	
}
