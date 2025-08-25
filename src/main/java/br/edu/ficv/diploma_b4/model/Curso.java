package br.edu.ficv.diploma_b4.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cursos")
public class Curso {

    @Id
    private Integer codigoCursoEMEC;

    private String nomeCurso;
    //private InformacoesTramitacaoEmec informacoesTramitacaoEmec;
    private String modalidade;
    private TituloConferido tituloConferido;
    private String grauConferido;
    private Ies.Endereco enderecoCurso;
    private AtoRegulatorio autorizacao;
    private AtoRegulatorio reconhecimento;

    @Data
    public static class TituloConferido {
        private String titulo;
    }

    @Data
    public static class AtoRegulatorio {
        private String tipo;
        private String numero;
        private String data;
        private String veiculoPublicacao;
        private String dataPublicacao;
    }

    @Data
    public static class InformacoesTramitacaoEmec {
        private Long numeroProcesso;
        private String tipoProcesso;
        private String dataCadastro;
        private String dataProtocolo;
    }
}