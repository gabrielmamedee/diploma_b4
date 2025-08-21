package br.edu.ficv.diploma_b4.controller;

import br.edu.ficv.diploma_b4.model.HistoricoAcademico;
import br.edu.ficv.diploma_b4.repository.HistoricoAcademicoRepository;
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

    /**
     * Endpoint para fazer o upload de um histórico em PDF, processá-lo e salvá-lo no banco.
     * URL: POST http://localhost:8080/api/historicos/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadEProcessarHistorico(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, envie um arquivo!");
        }

        try {
            HistoricoAcademico historicoExtraido = pdfExtractionService.extrairDados(file.getInputStream());
            HistoricoAcademico historicoSalvo = historicoRepository.save(historicoExtraido);
            return ResponseEntity.ok(historicoSalvo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao processar o arquivo PDF: " + e.getMessage());
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
}