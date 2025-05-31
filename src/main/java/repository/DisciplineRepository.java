package repository;

import org.springframework.data.repository.CrudRepository;

import domain.Discipline;

public interface DisciplineRepository extends CrudRepository<Discipline, Long> {}