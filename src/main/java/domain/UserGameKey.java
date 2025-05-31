package domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGameKey implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "user_id")
	Long userId;
	
	@Column(name = "game_id")
	Long gameId;
}
