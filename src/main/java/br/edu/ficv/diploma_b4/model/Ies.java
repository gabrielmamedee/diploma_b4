package br.edu.ficv.diploma_b4.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "instituicoes")
public class Ies {
    @Id
    private String id;
    private String nome;
    private long codigoMEC;
    private String cnpj;
    private Endereco endereco;
    private Credenciamento credenciamento;
    private Recredenciamento recredenciamento;
    private Mantenedora mantenedora;



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
    public static class Mantenedora {
        private String razaoSocial;
        private String cnpj;
        private Endereco endereco;
    }

    @Data
    public static class Credenciamento {
        private String tipo;
        private String numero;
        private String data;
        private String veiculoPublicacao;
        private String dataPublicacao;
        private long secaoPublicacao;
        private long paginaPublicacao;
        private long numeroDOU;
    }

    @Data
    public static class Recredenciamento {
        private InformacoesTramitacaoEmec informacoesTramitacaoEmec;
    }

    @Data
    public static class InformacoesTramitacaoEmec {
        private long numeroProcesso;
        private String tipoProcesso;
        private String dataCadastro;
        private String dataProtocolo;
    }
}
