package service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import domain.Game;
import domain.Sport;
import exceptions.GameNotFoundException;
import exceptions.SportNotFoundException;
import exceptions.StadiumNotFoundException;
import lombok.NoArgsConstructor;
import repository.GameRepository;
import repository.SportRepository;
import repository.StadiumRepository;

@Service
@NoArgsConstructor
public class GameService {

	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private SportRepository sportRepository;
	@Autowired
	private StadiumRepository stadiumRepository;
	
	public List<Game> getGamesBySport(Sport sport){
		return gameRepository
			.findAllBySportOrderByDateAscTimeAsc(sport);
	}
	
	public Game getGame(long gameid) {
		Game game = gameRepository
			.findById(gameid)
			.orElse(null);
		
		if(game == null) {
			throw new GameNotFoundException(gameid);
		}
		
		return game;
	}
	
	public void createGame(long sportid, Game game) {
		
		var sport = sportRepository
			.findById(sportid)
			.orElse(null);
		
		if(sport == null) {
			throw new SportNotFoundException(sportid);
		}
		
		game.setSport(sport);
		
		var stadium = stadiumRepository
			.findById(game.getStadiumId())
			.orElse(null);
		
		if(stadium == null) {
			throw new StadiumNotFoundException(game.getStadiumId());
		}
		
		game.setStadium(stadium);
		
		var disciplines = sport
			.getDisciplines()
			.stream()
			.filter(d -> game.getDisciplineIds().contains(d.getId()))
			.collect(Collectors.toList());
		
		disciplines.forEach(d -> game.addDiscipline(d));
		
		game.setSeatsSold(0);
		
		gameRepository.save(game);
	}
	
	public boolean isUniqueOlympicNr(String nr) {
		int amount = gameRepository.getOlympicNrCount(nr);
		return amount == 0;
	}
}
