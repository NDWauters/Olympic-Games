package com.springBoot.Olympic_Games;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import domain.Game;
import domain.Sport;
import jakarta.validation.Valid;
import service.GameService;
import service.SportService;
import validator.GameValidator;

@Controller
@RequestMapping("/game")
public class GameController extends BaseController {

	@Autowired
	private GameValidator gameValidator;
	@Autowired
	private GameService gameService;
	@Autowired
	private SportService sportService; 
	
	@GetMapping("/{sportid}")
	public String gamesBySport(@PathVariable long sportid, Model model, Principal principal) {
		
		Sport sport = sportService.get(sportid);
		
		model.addAttribute("userName", principal.getName());
		model.addAttribute("sport", sport);
		model.addAttribute("games", gameService.getGamesBySport(sport));
		
		return "/game/index";
	}
	
	@GetMapping("/create/{sportid}")
	public String createGame(
		@PathVariable int sportid, 
		Model model) {
		
		model.addAttribute("sport", sportService.get(sportid));
		model.addAttribute("game", new Game());
		
		return "/game/create";
	}
	
	@PostMapping("/create/{sportid}")
	public String submitGame( 
		@PathVariable int sportid,
		@Valid Game game,
		BindingResult result,
		Model model) {
		
		gameValidator.validate(game, result);
		
		if (result.hasErrors()) {
			
			model.addAttribute("sport", sportService.get(sportid));
			model.addAttribute("game", game);
			
            return "/game/create";
        }
		
		gameService.createGame(sportid, game);
		
		return "redirect:/game/" + sportid;
	}

}
