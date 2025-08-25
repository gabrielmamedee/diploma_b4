package br.edu.ficv.diploma_b4.controller;

import br.edu.ficv.diploma_b4.model.HistoricoAcademico;
import br.edu.ficv.diploma_b4.model.dto.DadosAdicionaisDTO;
import br.edu.ficv.diploma_b4.model.dto.DadosDiplomaDTO;
import br.edu.ficv.diploma_b4.repository.HistoricoAcademicoRepository;
import br.edu.ficv.diploma_b4.service.DiplomaMappingService;
import br.edu.ficv.diploma_b4.service.PdfExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/historicos")
public class HistoricoController {

    @Autowired
    private PdfExtractionService pdfExtractionService;

    @Autowired
    private HistoricoAcademicoRepository historicoRepository;

    @Autowired
    private DiplomaMappingService diplomaMappingService;

    /**
     * Endpoint para fazer o upload do histórico e dos dados adicionais.
     * Aceita uma requisição multipart/form-data com duas partes:
     * 1. Uma parte chamada "dados", que é um JSON.
     * 2. Uma parte chamada "file", que é o arquivo PDF.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadEProcessarHistorico(
            @RequestPart("dados") DadosAdicionaisDTO dadosAdicionais,
            @RequestPart("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, envie um arquivo PDF!");
        }
        if (dadosAdicionais == null) {
            return ResponseEntity.badRequest().body("Por favor, envie os dados adicionais do diplomado!");
        }

        try {
            // 1. O serviço extrai os dados do PDF como antes
            HistoricoAcademico historicoExtraido = pdfExtractionService.extrairDados(file.getInputStream());

            // 2. AQUI ACONTECE A COMBINAÇÃO:
            // "Enriquecemos" o objeto extraído com os dados que vieram do JSON.
            historicoExtraido.setSexo(dadosAdicionais.getSexo());
            historicoExtraido.setNaturalidade(dadosAdicionais.getNaturalidade());
            historicoExtraido.setCodigoCursoEMEC(dadosAdicionais.getCodigoCursoEMEC());

            // 3. Salvamos o objeto COMPLETO (PDF + JSON) no banco de dados
            HistoricoAcademico historicoSalvo = historicoRepository.save(historicoExtraido);

            // 4. Retornamos o objeto completo salvo como resposta
            return ResponseEntity.ok(historicoSalvo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao processar a requisição: " + e.getMessage());
        }
    }

    /**
     * Endpoint para buscar um histórico salvo no banco de dados pelo seu ID.
     * URL: GET http://localhost:8080/api/historicos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<HistoricoAcademico> getHistoricoPorId(@PathVariable String id) {
        return historicoRepository.findById(id)
                .map(historico -> ResponseEntity.ok(historico))
                .orElse(ResponseEntity.notFound().build()); // Se não encontrar, retorna 404 Not Found, melhorar posteriormente essa execao
    }

    /**
     * Endpoint para buscar um histórico pelo ID e gerar a primeira parte do JSON do diploma.
     * GET
     * URL: /api/historicos/{id}/diploma-json
     */
    @GetMapping("/{id}/diploma-json")
    public ResponseEntity<?> gerarDadosDiplomaJson(@PathVariable String id) {
        // 1. Busca o histórico completo (com dados do PDF + da requisição) no banco
        return historicoRepository.findById(id)
                .map(historicoEncontrado -> {
                    // 2. Se encontrar, chama o serviço de mapeamento para fazer a "mágica"
                    DadosDiplomaDTO diplomaJson = diplomaMappingService.montarDadosDiploma(historicoEncontrado);
                    // 3. Retorna 200 OK com o JSON do diploma no corpo da resposta
                    return ResponseEntity.ok(diplomaJson);
                })
                .orElse(ResponseEntity.notFound().build()); // 4. Se não encontrar o ID, retorna 404
    }
}