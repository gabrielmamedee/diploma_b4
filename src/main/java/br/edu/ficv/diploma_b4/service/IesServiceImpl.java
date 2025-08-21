package br.edu.ficv.diploma_b4.service;

import br.edu.ficv.diploma_b4.model.Ies;
import br.edu.ficv.diploma_b4.repository.IesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IesServiceImpl implements IesService {

    @Autowired
    private IesRepository iesRepository;

    @Override
    public Ies salvar(Ies ies) {
        return iesRepository.save(ies);
    }

    @Override
    public List<Ies> listarTodas() {
        return iesRepository.findAll();
    }

    @Override
    public Optional<Ies> buscarPorId(String id) {
        return iesRepository.findById(id);
    }

    @Override
    public Ies atualizar(String id, Ies iesDetalhes) {
        Ies iesExistente = iesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada com o id: " + id));

        iesDetalhes.setId(id);
        return iesRepository.save(iesDetalhes);
    }

    @Override
    public void excluir(String id) {
        if (iesRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Instituição não encontrada com o id: " + id);
        }
        iesRepository.deleteById(id);
    }
}