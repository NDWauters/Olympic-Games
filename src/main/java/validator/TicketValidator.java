package validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dto.ticketDTO;
import service.TicketService;

@Component
public class TicketValidator implements Validator {

	@Autowired
	private TicketService ticketService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ticketDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ticketDTO ticket = (ticketDTO) target;
		
		if(ticket.amount() <= 0) {
			errors.rejectValue("amount", "2Low.usergame.amount", "Min amount is 1");
			return;
		}
		
		if(ticket.amount() + ticket.alreadyBought() > 20) {
			errors.rejectValue("amount", "2High.usergame.amount", "Max amount is 20");
			return;
		}
		
		int amountOverAllGames = ticketService.getAmountOverAllGames(ticket.userName());
		
		if((amountOverAllGames + ticket.amount()) > 100) {
			errors.rejectValue("amount", "total.usergame.amount", "Total amount of tickets over all games is higher than 100");
			return;
		}
		
		if(!ticketService.hasGameSufficientSeats(ticket.gameId(), ticket.amount())) {
			errors.rejectValue("amount", "available_seats.usergame.amount", "Amount should be lower than the available seats for this game");
			return;
		}
	}

}
