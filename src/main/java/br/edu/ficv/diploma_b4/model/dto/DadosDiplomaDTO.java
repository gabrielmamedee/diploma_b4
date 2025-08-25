package br.edu.ficv.diploma_b4.model.dto;

import lombok.Data;

/**
 * DTO consolidado que representa toda a estrutura do bloco "dadosDiploma" do JSON.
 * Todas as sub-estruturas necessárias estão definidas aqui como classes aninhadas estáticas
 * para facilitar a organização e manutenção.
 */
@Data
public class DadosDiplomaDTO {

    private Diplomado diplomado;
    private String dataConclusao;
    private DadosCurso dadosCurso;
    private IesEmissora iesEmissora;

    // ===================================================================
    // CLASSES ANINHADAS QUE REPRESENTAM A ESTRUTURA DO JSON
    // ===================================================================

    @Data
    public static class Diplomado {
        private String id;
        private String nome;
        private String sexo;
        private String nacionalidade;
        private Naturalidade naturalidade;
        private String cpf;
        private Rg rg;
        private String dataNascimento;
    }

    @Data
    public static class DadosCurso {
        private String nomeCurso;
        private Integer codigoCursoEMEC;
        private InformacoesTramitacaoEmec informacoesTramitacaoEmec;
        private String modalidade;
        private TituloConferido tituloConferido;
        private String grauConferido;
        private Endereco enderecoCurso;
        private AtoRegulatorio autorizacao;
        private AtoRegulatorio reconhecimento;
    }

    @Data
    public static class IesEmissora {
        private String nome;
        private Long codigoMEC;
        private String cnpj;
        private Endereco endereco;
        private Credenciamento credenciamento;
        private Recredenciamento recredenciamento;
        private Mantenedora mantenedora;
    }

    // --- Estruturas de Suporte (reutilizadas e de nível mais baixo) ---

    @Data
    public static class Naturalidade {
        private String codigoMunicipio;
        private String nomeMunicipio;
        private String uf;
    }

    @Data
    public static class Rg {
        private String numero;
        private String uf;
        private String orgaoExpedidor;
    }

    @Data
    public static class Endereco {
        private String logradouro;
        private String numero;
        private String bairro;
        private String codigoMunicipio;
        private String nomeMunicipio;
        private String uf;
        private String cep;
    }

    @Data
    public static class InformacoesTramitacaoEmec {
        private Long numeroProcesso;
        private String tipoProcesso;
        private String dataCadastro;
        private String dataProtocolo;
    }

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
    public static class Credenciamento extends AtoRegulatorio {
        private Integer secaoPublicacao;
        private Integer paginaPublicacao;
        private Integer numeroDOU;
    }

    @Data
    public static class Recredenciamento {
        private InformacoesTramitacaoEmec informacoesTramitacaoEmec;
    }

    @Data
    public static class Mantenedora {
        private String razaoSocial;
        private String cnpj;
        private Endereco endereco;
    }
}