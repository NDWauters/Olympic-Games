package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.LocalDateSerializer;
import utils.LocalTimeSerializer;
import validator.ValidEventDate;
import validator.ValidEventTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "games")
@NamedQuery(name = "Game.getOlympicNrCount", query = "SELECT COUNT(*) FROM Game g WHERE g.olympicNr1 = :olympicNr")
public class Game implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("game_id")
	private Long id;
	
	@Column(nullable = false)
	@NotNull
	@ValidEventDate(minDate = "2024-07-26",maxDate = "2024-08-11")
	@JsonProperty("date")
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate date;
	
	@Column(nullable = false)
	@NotNull
	@ValidEventTime(minTime = "08:00")
	@JsonSerialize(using = LocalTimeSerializer.class)
	@JsonProperty("time")
	private LocalTime time;
	
	@Column(nullable = false)
	@DecimalMin("0.01")
	@DecimalMax("150.00")
	@NotNull
	@JsonProperty("ticket_price")
	private Double price;
	
	@Column(nullable = false)
	@Min(0)
	@Max(50)
	@NotNull
	@JsonIgnore
	private Integer seats;
	
	@Column(nullable = false)
	@JsonIgnore
	private Integer seatsSold;
	
	@Column(nullable = false, unique = true)
	@NotBlank
	@Pattern(regexp = "^[1-9]{1}[0-9]{4}$", message = "{pattern.game.olympicNr1}")
	@JsonProperty("olympic_nr_1")
	private String olympicNr1;
	
	@Column(nullable = false)
	@NotBlank
	@JsonProperty("olympic_nr_2")
	private String olympicNr2;
	
	@ManyToOne
	@JsonIgnore
	private Sport sport;
	
	@ManyToOne
	@JsonIgnore
	private Stadium stadium;
	
	@ManyToMany
	@Builder.Default
	@JsonIgnore
	private Set<Discipline> disciplines = new HashSet<>();
	
	@OneToMany(mappedBy = "game")
	@Builder.Default
	@JsonIgnore
	private Set<UserGame> tickets = new HashSet<>();
	
	@Transient
	@Builder.Default
	@JsonIgnore
	private List<Long> disciplineIds = new ArrayList<>();
	
	@Transient
	@JsonIgnore
	private Long stadiumId;
	
	@JsonProperty("disciplines")
	public String getDisciplinesString() {
		return this.disciplines
			.stream()
			.map(Discipline::getName)
			.collect(Collectors.joining(","));
	}
	
	@JsonProperty("seats_available")
	public Integer getSeatsAvailable() {
		return (seats - seatsSold);
	}
	
	@JsonProperty("sport")
	public String getSportName() {
		return this.sport.getName();
	}
	
	@JsonProperty("stadium")
	public String getStadiumName() {
		return this.stadium.getName();
	}
	
	public void addDiscipline(Discipline d) {
		
		if(d == null) {
			throw new NoSuchElementException("discipline");
		}
		
		disciplines.add(d);
	}
	
	public int getAmountTicketsForUser(String userName) {
		UserGame ticket = tickets
			.stream()
			.filter(ug -> ug.getUser().getUserName().equals(userName))
			.findFirst()
			.orElse(null);
		
		return ticket == null 
			? 0 
			: ticket.getAmount();
	}
}
