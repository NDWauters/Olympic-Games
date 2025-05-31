package perform;

import org.springframework.web.reactive.function.client.WebClient;

import domain.Game;
import reactor.core.publisher.Mono;

public class PerformRest {

	private final String SERVER_URI = "http://localhost:8080/rest";
	
	private WebClient webClient = WebClient.create();
	
	public PerformRest() {
		
		System.out.println("\n-------GET GAMES BY SPORT ID 5 -------");
		
		getAllGamesBySport(5);
		
		System.out.println("\n-------GET GAMES BY SPORT ID 999 (UNKNOWN ID) -------");
		
		try {
			getAllGamesBySport(999);
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		System.out.println("\n-------GET AVAILABLE SEATS FOR GAME WITH ID 1 -------");
		
		getAvailableSeats(1);
		
		System.out.println("\n-------GET AVAILABLE SEATS FOR GAME WITH ID 999 (UNKNOWN ID) -------");
		
		try {
			getAvailableSeats(999);
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private void getAllGamesBySport(int sportid) {
		webClient
			.get()
			.uri(SERVER_URI + "/game/" + sportid)
			.retrieve()
			.bodyToFlux(Game.class)
			.flatMap(g -> {
				System.out.println(g.toString());
				return Mono.empty();
			})
			.blockLast();
	}
	
	private void getAvailableSeats(int gameid) {
		webClient
			.get()
			.uri(SERVER_URI + "/game/seats/" + gameid)
			.retrieve()
			.bodyToFlux(Integer.class)
			.flatMap(i -> {
				System.out.println(i.toString());
				return Mono.empty();
			})
			.blockLast();
	}
}
