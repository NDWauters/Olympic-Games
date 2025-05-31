package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import domain.Game;
import domain.MyUser;
import domain.UserGame;
import domain.UserGameKey;

public interface TicketRepository extends JpaRepository<UserGame, UserGameKey> {
	List<UserGame> findAllByUser(@Param("user") MyUser user);
	Optional<Integer> getTotalAmountForUserFromAllGames(@Param("user") MyUser user);
}
