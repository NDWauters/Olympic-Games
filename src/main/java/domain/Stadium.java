package domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "name")
@Table(name = "stadiums")
@NamedQuery(name = "Stadium.getAll", query = "SELECT s from Stadium s")
public class Stadium implements Serializable {

	private static final long serialVersionUID = 1L;

	public Stadium(long id, String name) {
		
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("stadium_id")
	private Long Id;
	
	@Column(nullable = false)
	@JsonProperty("stadium_name")
	private String name;
	
	@OneToMany
	@Builder.Default
	private Set<Game> games = new HashSet<>();
	
	@ManyToMany(mappedBy = "stadiums")
	@Builder.Default
	private Set<Sport> sports = new HashSet<>();
}
