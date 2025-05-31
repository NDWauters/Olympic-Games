package validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import domain.Game;
import service.GameService;

@Component
public class GameValidator implements Validator {

	@Autowired
	private GameService gameService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Game.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		Game game = (Game)target;
		
		if(!gameService.isUniqueOlympicNr(game.getOlympicNr1())) {
			errors.rejectValue("olympicNr1", "uniqueNr.game.olympicNr1", "Olympic nr 1 should be unique");
		}
		
		if(!game.getOlympicNr1().isBlank() && !game.getOlympicNr2().isBlank()) {
			
			int olympicNr1Int = Integer.parseInt(game.getOlympicNr1());
			int olympicNr2Int = Integer.parseInt(game.getOlympicNr2());
			
			if(olympicNr2Int > (olympicNr1Int + 1000) || olympicNr2Int < (olympicNr1Int - 1000)) {
				errors.rejectValue("olympicNr2", "outOfRange.game.olympicNr1", "Olympic nr 2 should be in range of [olympic nr 1 - 1000, olympic nr 1 + 1000]");
			}
		}
		
		if(game.getStadiumId() == -1L) {
			errors.rejectValue("stadiumId", "null.game.stadiumId", "Should be filled in");
		}
		
		if(game.getDisciplineIds().size() > 2) {
			errors.rejectValue("disciplineIds", "outOfRange.game.disciplineIds", "Too many disciplines selected");
		}
		
	}

}
