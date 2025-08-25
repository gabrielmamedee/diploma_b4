package br.edu.ficv.diploma_b4.service;

import br.edu.ficv.diploma_b4.model.Curso;
import br.edu.ficv.diploma_b4.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoServiceImpl implements CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Override
    public Curso salvar(Curso curso) {
        // Validações futuras podem ser adicionadas aqui
        return cursoRepository.save(curso);
    }

    @Override
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    @Override
    public Optional<Curso> buscarPorId(Integer id) {
        return cursoRepository.findById(id);
    }

    @Override
    public Curso atualizar(Integer id, Curso cursoDetalhes) {
        Curso cursoExistente = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com o id: " + id));

        cursoDetalhes.setCodigoCursoEMEC(id); // Garante que o ID correto seja usado na atualização
        return cursoRepository.save(cursoDetalhes);
    }

    @Override
    public void excluir(Integer id) {
        if (cursoRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Curso não encontrado com o id: " + id);
        }
        cursoRepository.deleteById(id);
    }
}