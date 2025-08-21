package br.edu.ficv.diploma_b4.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "historicos_academicos")
public class HistoricoAcademico {

    @Id
    private String id;

    // Dados do Aluno
    private String nomeAluno;
    private String dataNascimento;
    private String nacionalidade;
    private String rg;
    private String matricula;
    private String cpf;

    // Dados do Ingresso e Curso
    private String anoSemestreIngresso;
    private String dataRealizacaoProcessoSeletivo;
    private String formaIngresso;
    private String nomeCurso;
    private String inicioRealizacaoCurso;
    private String fimRealizacaoCurso;

    // Dados de Desempenho
    private double horasAtividadesComplementares;
    private double totalHorasAula;
    private double coeficienteRendimento;

    // --- Lista de Disciplinas Cursadas ---
    private List<Disciplina> disciplinas;

    // Observacoes
    private String situacaoENADE;
    private String dataConclusaoCurso;
    private String dataColacaoGrau;
    private String dataExpedicaoDiploma;
}
