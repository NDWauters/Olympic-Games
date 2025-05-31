package com.springBoot.Olympic_Games;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import domain.Game;
import dto.ticketDTO;
import jakarta.validation.Valid;
import service.GameService;
import service.TicketService;
import validator.TicketValidator;

@Controller
@RequestMapping("/ticket")
public class TicketController extends BaseController {

	@Autowired
	private GameService gameService;
	@Autowired
	private TicketService ticketService;
	@Autowired
	private TicketValidator validator;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping
	public String ticketsForUser(Model model, Principal principal) {
		
		model.addAttribute("tickets", ticketService.getTicketsForUser(principal.getName()));
		
		return "/ticket/index";
	}
	
	@GetMapping("/buy/{gameid}")
	public String buyTickets(@PathVariable long gameid, Model model, Principal principal) {
		
		Game game = gameService.getGame(gameid);
		
		model.addAttribute("game", game);
		model.addAttribute("ticketDTO", ticketService.getTicketForForm(game, principal.getName()));
		
		return "/ticket/buy";
	}	
	
	@PostMapping("/buy/{gameid}") 
  	public String submitTickets(
	    @PathVariable long gameid,
	    @Valid ticketDTO ticketDTO, 
	    BindingResult result, 
	    Model model, 
	    Principal principal,
	    RedirectAttributes attributes,
	    Locale locale) {
	  
	  validator.validate(ticketDTO, result);
	  
	  if (result.hasErrors()) {
			
		  model.addAttribute("game", gameService.getGame(gameid));
		  model.addAttribute("ticketDTO", ticketDTO);
			
		  return "/ticket/buy";
      }
		
	  ticketService.buyTickets(gameid, principal.getName(), ticketDTO);
	  
	  attributes.addFlashAttribute("ticketsBoughtMsg", messageSource.getMessage("ticketsBoughtMsg", new Object[] { ticketDTO.amount() }, locale));
	  
	  return String.format("redirect:/game/%s", ticketDTO.sportId()); 
  }
}
