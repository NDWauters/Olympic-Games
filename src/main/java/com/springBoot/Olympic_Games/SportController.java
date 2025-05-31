package com.springBoot.Olympic_Games;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import service.SportService;
import service.TicketService;

@Controller
@RequestMapping("/sport")
public class SportController extends BaseController {
	
	@Autowired
	private SportService sportService;
	@Autowired
	private TicketService ticketService;
	
	@GetMapping
	public String sportList(Model model, Principal principal) {
		
		model.addAttribute("sports", sportService.get());
		model.addAttribute("amountTickets", ticketService.getAmountOverAllGames(principal.getName()));
		
		return "/sport/index";
	}
}
