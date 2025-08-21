package br.edu.ficv.diploma_b4.repository;

import br.edu.ficv.diploma_b4.model.Ies;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IesRepository extends MongoRepository<Ies, String> {
}
