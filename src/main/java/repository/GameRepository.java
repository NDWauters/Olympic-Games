package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import domain.Game;
import domain.Sport;

public interface GameRepository extends JpaRepository<Game, Long> {

	List<Game> findAllBySportOrderByDateAscTimeAsc(Sport sport);
	Integer getOlympicNrCount(@Param("olympicNr") String nr);
}
