package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import domain.Game;
import domain.MyUser;
import domain.UserGame;
import domain.UserGameKey;
import dto.ticketDTO;
import exceptions.GameNotFoundException;
import exceptions.UserNotFoundException;
import lombok.NoArgsConstructor;
import repository.GameRepository;
import repository.TicketRepository;
import repository.UserRepository;

@Service
@NoArgsConstructor
public class TicketService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private GameRepository gameRepository; 
	
	public List<UserGame> getTicketsForUser(String userName){
		
		MyUser user = userRepository.findByUserName(userName);
		
		if(user == null) {
			throw new UserNotFoundException(userName);
		}
		
		return ticketRepository
			.findAllByUser(user);
	}
	
	public ticketDTO getTicketForForm(Game game, String userName) {
		
		MyUser user = userRepository.findByUserName(userName);
		
		if(user == null) {
			throw new UserNotFoundException(userName);
		}
		
		var key = new UserGameKey(user.getId(), game.getId());
		
		UserGame ticket = ticketRepository
			.findById(key)
			.orElse(new UserGame(key, user, game, 0));
		
		return new ticketDTO(
			game.getSport().getId(), 
			game.getId(), 
			userName, 
			ticket.getAmount(), 
			0);
	}

	public void buyTickets(long gameid, String userName, ticketDTO ticket) {
		
		MyUser user = userRepository.findByUserName(userName);
		
		if(user == null) {
			throw new UserNotFoundException(userName);
		}
		
		Game game = gameRepository
			.findById(gameid)
			.orElse(null);
		
		if(game == null) {
			throw new GameNotFoundException(gameid);
		}
		
		UserGameKey key = new UserGameKey(user.getId(), gameid);
		
		UserGame ticketFromDb = ticketRepository
			.findById(key)
			.orElse(null);
		
		Integer alreadyBought = 0;
		
		if(ticketFromDb == null) {
			ticketFromDb = new UserGame(
				key, 
				user, 
				game, 
				ticket.amount());
		} else {
			alreadyBought = ticketFromDb.getAmount();
			ticketFromDb.setAmount(alreadyBought + ticket.amount());
		}
		
		ticketRepository.save(ticketFromDb);
		
		game.setSeatsSold(game.getSeatsSold() + ticket.amount());
		gameRepository.save(game);
	}
	
	public Integer getAmountOverAllGames(String userName) {
		
		MyUser user = userRepository.findByUserName(userName);
		
		if(user == null) {
			throw new UserNotFoundException(userName);
		}
		
		return ticketRepository
			.getTotalAmountForUserFromAllGames(user)
			.orElse(0);
	}
	
	public boolean hasGameSufficientSeats(long gameid, int amount) {
		
		if(gameid == 0L) {
			return true;
		}
		
		Game game = gameRepository
			.findById(gameid)
			.orElse(null);
		
		if(game == null) {
			throw new GameNotFoundException(gameid);
		}
		
		return game.getSeatsAvailable() >= amount;
	}
}
