package br.edu.ficv.diploma_b4.service;

import br.edu.ficv.diploma_b4.model.Ies;
import java.util.List;
import java.util.Optional;

public interface IesService {
    Ies salvar(Ies ies);
    List<Ies> listarTodas();
    Optional<Ies> buscarPorId(String id);
    Ies atualizar(String id, Ies iesDetalhes);
    void excluir(String id);
}
