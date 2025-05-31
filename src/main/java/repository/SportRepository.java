package repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import domain.Sport;

public interface SportRepository extends CrudRepository<Sport, Long> {
	List<Sport> getAll();
}
