package domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "users_games")
@NamedQueries({
	@NamedQuery(
		name = "UserGame.findAllByUser", 
		query = "SELECT ug "
				+ "FROM UserGame ug "
				+ "WHERE ug.user = :user "
				+ "ORDER BY ug.game.sport.name ASC,ug.game.date ASC,ug.game.time ASC"),
	@NamedQuery(name = "UserGame.getTotalAmountForUserFromAllGames", query = "SELECT SUM(ug.amount) FROM UserGame ug WHERE ug.user = :user"),
})
public class UserGame {

	@EmbeddedId
	private UserGameKey id;
	
	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private MyUser user;
	
	@ManyToOne
	@MapsId("gameId")
	@JoinColumn(name = "game_id")
	private Game game;
	
	@Column(nullable = false)
	@Max(20)
	private int amount;
}
