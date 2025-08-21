package br.edu.ficv.diploma_b4.repository;

import br.edu.ficv.diploma_b4.model.HistoricoAcademico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoAcademicoRepository extends MongoRepository<HistoricoAcademico, String> {

}
