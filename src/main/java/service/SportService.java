package service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import domain.Sport;
import exceptions.SportNotFoundException;
import lombok.NoArgsConstructor;
import repository.SportRepository;

@Service
@NoArgsConstructor
public class SportService {

	@Autowired
	private SportRepository sportRepository;
	
	public List<Sport> get(){
		return Collections.unmodifiableList(sportRepository.getAll());
	}
	
	public Sport get(long id){
		
		var sport =  sportRepository
			.findById(id)
			.orElse(null);
		
		if(sport == null) {
			throw new SportNotFoundException(id);
		}
		
		return sport;
	}
	
}
