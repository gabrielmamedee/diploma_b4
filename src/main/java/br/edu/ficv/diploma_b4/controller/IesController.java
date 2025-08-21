package br.edu.ficv.diploma_b4.controller;

import br.edu.ficv.diploma_b4.model.Ies;
import br.edu.ficv.diploma_b4.service.IesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instituicoes")
public class IesController {

    @Autowired
    private IesService iesService;

    /**
     * Endpoint para CADASTRAR uma nova instituição.
     * POST
     * URL: /api/instituicoes
     * CORPO (BODY): JSON com os dados da Ies.
     */
    @PostMapping
    public ResponseEntity<Ies> cadastrarIes(@RequestBody Ies ies) {
        Ies iesSalva = iesService.salvar(ies); // <-- MUDANÇA: Chama o serviço
        return ResponseEntity.status(HttpStatus.CREATED).body(iesSalva);
    }

    /**
     * Endpoint para LISTAR TODAS as instituições cadastradas.
     * GET
     * URL: /api/instituicoes
     */
    @GetMapping
    public ResponseEntity<List<Ies>> listarTodas() {
        List<Ies> instituicoes = iesService.listarTodas(); // <-- MUDANÇA: Chama o serviço
        return ResponseEntity.ok(instituicoes);
    }

    /**
     * Endpoint para BUSCAR UMA instituição pelo seu ID.
     * GET
     * URL: /api/instituicoes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ies> buscarPorId(@PathVariable String id) {
        return iesService.buscarPorId(id) // <-- MUDANÇA: Chama o serviço
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para ATUALIZAR uma instituição existente.
     * PUT
     * URL: /api/instituicoes/{id}
     * CORPO (BODY): JSON com os dados completos da Ies a serem atualizados.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editarIes(@PathVariable String id, @RequestBody Ies iesDetalhes) {
        try {
            Ies iesAtualizada = iesService.atualizar(id, iesDetalhes); // <-- MUDANÇA: Chama o serviço
            return ResponseEntity.ok(iesAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para EXCLUIR uma instituição pelo ID.
     * DELETE
     * URL: /api/instituicoes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirIes(@PathVariable String id) {
        try {
            iesService.excluir(id); // <-- MUDANÇA: Chama o serviço
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}