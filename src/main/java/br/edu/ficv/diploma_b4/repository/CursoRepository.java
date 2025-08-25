package br.edu.ficv.diploma_b4.repository;

import br.edu.ficv.diploma_b4.model.Curso;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CursoRepository extends MongoRepository<Curso, Integer> {
}
