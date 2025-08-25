package br.edu.ficv.diploma_b4.service;

import br.edu.ficv.diploma_b4.model.Curso;
import java.util.List;
import java.util.Optional;

public interface CursoService {
    Curso salvar(Curso curso);
    List<Curso> listarTodos();
    Optional<Curso> buscarPorId(Integer id);
    Curso atualizar(Integer id, Curso cursoDetalhes);
    void excluir(Integer id);
}