package com.springBoot.Olympic_Games;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import domain.Discipline;
import domain.Game;
import domain.MyUser;
import domain.Role;
import domain.Sport;
import domain.Stadium;
import repository.DisciplineRepository;
import repository.GameRepository;
import repository.SportRepository;
import repository.StadiumRepository;
import repository.UserRepository;

@Component
public class InitDataConfig implements CommandLineRunner {

	//when setting this to true => FIRST change ddl-auto to create-drop in application.properties
	//when setting this to false => FIRST change ddl-auto to update in application.properties
	private boolean resetDb = true;
	
	private PasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SportRepository sportRepository;
	@Autowired
	private StadiumRepository stadiumRepository;
	@Autowired
	private DisciplineRepository disciplineRepository;
	@Autowired
	private GameRepository gameRepository;
	
	private List<Sport> sports = new ArrayList<>();
	private List<Stadium> stadiums = new ArrayList<>();
	private List<Discipline> disciplines = new ArrayList<>();
	
	@Override
	public void run(String... args) throws Exception {
		if(resetDb) {
			initUsers();
			initDisciplines();
			initStadiums();
			initSports();
			initGames();
		}
	}
	
	private void initUsers() {
		
		var user = MyUser
			.builder()
			.userName("user")
			.role(Role.USER)
			.password(encoder.encode("user123!"))
			.build();
			
		var admin = MyUser
			.builder()
			.userName("admin")
			.role(Role.ADMIN)
			.password(encoder.encode("admin123!"))
			.build();
		
		userRepository.saveAll(Arrays.asList(admin, user));
	}

	private void initSports() {
		
		var men_individual = disciplines.get(0);
		var women_individual = disciplines.get(1);
		var men_team = disciplines.get(2);
		var women_team = disciplines.get(3);
		var mixed_team = disciplines.get(4);
		var men_51_kg = disciplines.get(5);
		var women_50_kg = disciplines.get(6);
		var men_road_race = disciplines.get(7);
		var women_road_race = disciplines.get(8);
		var men_time_trial = disciplines.get(9);
		var women_time_trial = disciplines.get(10);
		
		Set<Discipline> archery_disciplines = new HashSet<>();
		archery_disciplines.addAll(Arrays.asList(men_individual,women_individual,men_team,women_team,mixed_team));
		
		Set<Discipline> basketball_disciplines = new HashSet<>();
		basketball_disciplines.addAll(Arrays.asList(men_team,women_team));
		
		Set<Discipline> boxing_disciplines = new HashSet<>();
		boxing_disciplines.addAll(Arrays.asList(men_51_kg,women_50_kg));
		
		Set<Discipline> cycling_disciplines = new HashSet<>();
		cycling_disciplines.addAll(Arrays.asList(men_road_race,women_road_race,men_time_trial,women_time_trial));
		
		Set<Discipline> football_disciplines = new HashSet<>();
		football_disciplines.addAll(Arrays.asList(men_team,women_team));
		
		var rolland_garros = stadiums.get(0);
		var parc_des_princes = stadiums.get(1);
		var arena_paris_sud_1 = stadiums.get(2);
		var arena_paris_sud_4 = stadiums.get(3);
		var arena_paris_sud_6 = stadiums.get(4);
		
		Set<Stadium> archery_stadiums = new HashSet<>();
		archery_stadiums.addAll(Arrays.asList(parc_des_princes,arena_paris_sud_4,arena_paris_sud_6));
		
		Set<Stadium> football_stadiums = new HashSet<>();
		football_stadiums.addAll(Arrays.asList(rolland_garros,parc_des_princes));
		
		Set<Stadium> basketball_stadiums = new HashSet<>();
		basketball_stadiums.addAll(Arrays.asList(arena_paris_sud_1,arena_paris_sud_6));
		
		Set<Stadium> boxing_stadiums = new HashSet<>();
		boxing_stadiums.addAll(Arrays.asList(arena_paris_sud_1,arena_paris_sud_4,arena_paris_sud_6));
		
		Set<Stadium> cycling_stadiums = new HashSet<>();
		cycling_stadiums.addAll(Arrays.asList(
			rolland_garros, 
			parc_des_princes, 
			arena_paris_sud_1, 
			arena_paris_sud_4, 
			arena_paris_sud_6));
		
		var archery = Sport.builder().name("Archery").disciplines(archery_disciplines).stadiums(archery_stadiums).build();
		var basketball = Sport.builder().name("Basketball").disciplines(basketball_disciplines).stadiums(basketball_stadiums).build();
		var boxing = Sport.builder().name("Boxing").disciplines(boxing_disciplines).stadiums(boxing_stadiums).build();
		var cycling = Sport.builder().name("Cycling").disciplines(cycling_disciplines).stadiums(cycling_stadiums).build();
		var football = Sport.builder().name("Football").disciplines(football_disciplines).stadiums(football_stadiums).build();
		
		sports = Arrays.asList(archery,basketball,boxing,cycling,football);
		
		sportRepository.saveAll(sports);
	}
	
	private void initStadiums() {
		
		var stadium_1 = Stadium.builder().name("Rolland Garros").build();
		var stadium_2 = Stadium.builder().name("Parc des Princes").build();
		var stadium_3 = Stadium.builder().name("Arena Paris Sud 1").build();
		var stadium_4 = Stadium.builder().name("Arena Paris Sud 4").build();
		var stadium_5 = Stadium.builder().name("Arena Paris Sud 6").build();
		
		stadiums = Arrays.asList(stadium_1,stadium_2,stadium_3,stadium_4,stadium_5);
		
		stadiumRepository.saveAll(stadiums);
	}
	
	private void initDisciplines() {
		var men_individual = Discipline.builder().name("Men's Individual").build();
		var women_individual = Discipline.builder().name("Women's Individual").build();
		var men_team = Discipline.builder().name("Men's Team").build();
		var women_team = Discipline.builder().name("Women's Team").build();
		var mixed_team = Discipline.builder().name("Mixed Team").build();
		var men_51_kg = Discipline.builder().name("Men's 51kg").build();
		var women_50_kg = Discipline.builder().name("Women's 50kg").build();
		var men_road_race = Discipline.builder().name("Men's Road Race").build();
		var women_road_race = Discipline.builder().name("Women's Road Race").build();
		var men_time_trial = Discipline.builder().name("Men's Individual Time Trial").build();
		var women_time_trial = Discipline.builder().name("Women's Individual Time Trial").build();
		
		disciplines = Arrays.asList(
			men_individual, 
			women_individual, 
			men_team, 
			women_team, 
			mixed_team, 
			men_51_kg, 
			women_50_kg, 
			men_road_race, 
			women_road_race, 
			men_time_trial, 
			women_time_trial);
		
		disciplineRepository.saveAll(disciplines);
	}
	
	private void initGames() {
		
		var football_game = Game
			.builder()
			.sport(sports.get(4))
			.olympicNr1("12345")
			.olympicNr2("13345")
			.date(LocalDate.of(2024, 07, 30))
			.time(LocalTime.of(18, 0))
			.disciplines(Set.of(disciplines.get(2)))
			.stadium(stadiums.get(1))
			.seats(50)
			.seatsSold(10)
			.price(100.5)
			.build();
		
		gameRepository.save(football_game);
	}
}
