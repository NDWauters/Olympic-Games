package domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
@Table(name = "sports")
@NamedQuery(name = "Sport.getAll", query = "SELECT s FROM Sport s")
public class Sport implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Sport(long id, String name) {
		this.Id = id;
		this.name = name;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@OneToMany
	@Builder.Default
	private Set<Game> games = new HashSet<>();
	
	@ManyToMany
	@Builder.Default
	private Set<Discipline> disciplines = new HashSet<>();
	
	@ManyToMany
	@Builder.Default
	private Set<Stadium> stadiums = new HashSet<>();
}
