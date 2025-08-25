package br.edu.ficv.diploma_b4.service;

import br.edu.ficv.diploma_b4.model.HistoricoAcademico;
import br.edu.ficv.diploma_b4.model.Ies;
import br.edu.ficv.diploma_b4.repository.IesRepository;
import br.edu.ficv.diploma_b4.model.dto.DadosDiplomaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DiplomaMappingService {

    @Autowired
    private IesRepository iesRepository;

    /**
     * Orquestra o preenchimento de todo o bloco "dadosDiploma".
     * Este é o método principal que será chamado pelo Controller.
     */
    public DadosDiplomaDTO montarDadosDiploma(HistoricoAcademico historico) {
        // 1. Busca os dados de configuração da instituição no banco.
        Ies ies = iesRepository.findById("09491298000316")
                .orElseThrow(() -> new RuntimeException("Dados da IES Emissora com ID 09491298000316 não encontrados no banco."));

        DadosDiplomaDTO dadosDiploma = new DadosDiplomaDTO();

        // 2. Preenche cada parte principal do DTO usando métodos auxiliares
        dadosDiploma.setDiplomado(mapearDiplomado(historico));
        dadosDiploma.setDataConclusao(formatarData(historico.getDataConclusaoCurso()));
        dadosDiploma.setDadosCurso(mapearDadosCurso(historico, ies));
        dadosDiploma.setIesEmissora(mapearIesEmissora(ies));

        return dadosDiploma;
    }

    /**
     * Mapeia os dados do objeto "diplomado" conforme sua especificação.
     */
    private DadosDiplomaDTO.Diplomado mapearDiplomado(HistoricoAcademico historico) {
        DadosDiplomaDTO.Diplomado diplomado = new DadosDiplomaDTO.Diplomado();

        diplomado.setId(historico.getMatricula());
        diplomado.setNome(historico.getNomeAluno());
        diplomado.setNacionalidade(historico.getNacionalidade());
        diplomado.setCpf(historico.getCpf().replaceAll("[^\\d]", ""));
        diplomado.setSexo(historico.getSexo()); // Vem da requisição, já salvo no HistoricoAcademico

        // Mapeia o objeto de naturalidade que veio da requisição
        if (historico.getNaturalidade() != null) {
            DadosDiplomaDTO.Naturalidade naturalidadeDto = new DadosDiplomaDTO.Naturalidade();
            naturalidadeDto.setCodigoMunicipio(historico.getNaturalidade().getCodigoMunicipio());
            naturalidadeDto.setNomeMunicipio(historico.getNaturalidade().getNomeMunicipio());
            naturalidadeDto.setUf(historico.getNaturalidade().getUf());
            diplomado.setNaturalidade(naturalidadeDto);
        }

        // Transformação: Formatar data para YYYY-MM-DD
        diplomado.setDataNascimento(formatarData(historico.getDataNascimento()));

        // Transformação: "Desfragmentar" o RG
        if (historico.getRg() != null) {
            String[] rgParts = historico.getRg().split(" ");
            if (rgParts.length >= 3) {
                DadosDiplomaDTO.Rg rgDto = new DadosDiplomaDTO.Rg();
                rgDto.setNumero(rgParts[0]);
                rgDto.setOrgaoExpedidor(rgParts[1]);
                rgDto.setUf(rgParts[2]);
                diplomado.setRg(rgDto);
            }
        }
        return diplomado;
    }

    /**
     * Mapeia os dados do objeto "dadosCurso".
     */
    private DadosDiplomaDTO.DadosCurso mapearDadosCurso(HistoricoAcademico historico, Ies ies) {
        DadosDiplomaDTO.DadosCurso dadosCurso = new DadosDiplomaDTO.DadosCurso();

        // Transformação: Limpar o nome do curso
        if (historico.getNomeCurso() != null) {
            String nomeCursoTratado = historico.getNomeCurso()
                    .replace("Bacharelado em", "")
                    .replace("Bacharel", "")
                    .trim();
            dadosCurso.setNomeCurso(nomeCursoTratado);
        }

        // Mapeia o endereço do curso a partir dos dados da IES
        if (ies.getEndereco() != null) {
            DadosDiplomaDTO.Endereco enderecoDto = new DadosDiplomaDTO.Endereco();
            copyEndereco(ies.getEndereco(), enderecoDto);
            dadosCurso.setEnderecoCurso(enderecoDto);
        }

        // ATENÇÃO: Demais dados viriam dos "dados adicionais" (salvos no HistoricoAcademico)
        // ou da entidade IES, dependendo da regra de negócio.
        // Ex: dadosCurso.setModalidade(historico.getModalidade());
        DadosDiplomaDTO.TituloConferido titulo = new DadosDiplomaDTO.TituloConferido();
        titulo.setTitulo("Bacharel"); // Exemplo de dado fixo/da requisição
        dadosCurso.setTituloConferido(titulo);
        dadosCurso.setGrauConferido("Bacharelado"); // Exemplo

        return dadosCurso;
    }

    /**
     * Mapeia os dados da entidade Ies (do banco) para o DTO IesEmissora.
     */
    /**
     * Mapeia os dados da entidade Ies (do banco) para o DTO IesEmissora.
     * VERSÃO ATUALIZADA para incluir credenciamento e recredenciamento.
     */
    private DadosDiplomaDTO.IesEmissora mapearIesEmissora(Ies ies) {
        DadosDiplomaDTO.IesEmissora iesDto = new DadosDiplomaDTO.IesEmissora();
        iesDto.setNome(ies.getNome());
        iesDto.setCnpj(ies.getCnpj());
        iesDto.setCodigoMEC(ies.getCodigoMEC());

        // Mapeia o endereço da IES
        if (ies.getEndereco() != null) {
            DadosDiplomaDTO.Endereco enderecoDto = new DadosDiplomaDTO.Endereco();
            copyEndereco(ies.getEndereco(), enderecoDto);
            iesDto.setEndereco(enderecoDto);
        }

        // Mapeia a mantenedora e seu endereço
        if (ies.getMantenedora() != null) {
            DadosDiplomaDTO.Mantenedora mantenedoraDto = new DadosDiplomaDTO.Mantenedora();
            mantenedoraDto.setRazaoSocial(ies.getMantenedora().getRazaoSocial());
            mantenedoraDto.setCnpj(ies.getMantenedora().getCnpj());

            if (ies.getMantenedora().getEndereco() != null) {
                DadosDiplomaDTO.Endereco enderecoMantenedoraDto = new DadosDiplomaDTO.Endereco();
                copyEndereco(ies.getMantenedora().getEndereco(), enderecoMantenedoraDto);
                mantenedoraDto.setEndereco(enderecoMantenedoraDto);
            }
            iesDto.setMantenedora(mantenedoraDto);
        }

        // --- LÓGICA ADICIONADA PARA O CREDENCIAMENTO ---
        if (ies.getCredenciamento() != null) {
            DadosDiplomaDTO.Credenciamento credenciamentoDto = new DadosDiplomaDTO.Credenciamento();
            Ies.Credenciamento credenciamentoEntidade = ies.getCredenciamento();

            credenciamentoDto.setTipo(credenciamentoEntidade.getTipo());
            credenciamentoDto.setNumero(credenciamentoEntidade.getNumero());
            credenciamentoDto.setData(credenciamentoEntidade.getData());
            credenciamentoDto.setVeiculoPublicacao(credenciamentoEntidade.getVeiculoPublicacao());
            credenciamentoDto.setDataPublicacao(credenciamentoEntidade.getDataPublicacao());
            credenciamentoDto.setSecaoPublicacao( (int) credenciamentoEntidade.getSecaoPublicacao());
            credenciamentoDto.setPaginaPublicacao( (int) credenciamentoEntidade.getPaginaPublicacao());
            credenciamentoDto.setNumeroDOU( (int) credenciamentoEntidade.getNumeroDOU());

            iesDto.setCredenciamento(credenciamentoDto);
        }

        // --- LÓGICA ADICIONADA PARA O RECREDENCIAMENTO ---
        if (ies.getRecredenciamento() != null) {
            DadosDiplomaDTO.Recredenciamento recredenciamentoDto = new DadosDiplomaDTO.Recredenciamento();
            DadosDiplomaDTO.InformacoesTramitacaoEmec infoDto = new DadosDiplomaDTO.InformacoesTramitacaoEmec();
            Ies.InformacoesTramitacaoEmec infoEntidade = ies.getRecredenciamento().getInformacoesTramitacaoEmec();

            infoDto.setNumeroProcesso(infoEntidade.getNumeroProcesso());
            infoDto.setTipoProcesso(infoEntidade.getTipoProcesso());
            infoDto.setDataCadastro(infoEntidade.getDataCadastro());
            infoDto.setDataProtocolo(infoEntidade.getDataProtocolo());

            recredenciamentoDto.setInformacoesTramitacaoEmec(infoDto);
            iesDto.setRecredenciamento(recredenciamentoDto);
        }

        return iesDto;
    }

    /**
     * Função auxiliar para converter datas do formato DD/MM/YYYY para YYYY-MM-DD.
     */
    private String formatarData(String dataDdMmAaaa) {
        if (dataDdMmAaaa == null || dataDdMmAaaa.isBlank()) return null;
        try {
            DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatoSaida = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dataDdMmAaaa, formatoEntrada).format(formatoSaida);
        } catch (DateTimeParseException e) {
            // Adiciona um log de erro para ajudar na depuração
            System.err.println("AVISO: Não foi possível converter a data '" + dataDdMmAaaa + "'. Verifique o formato. Retornando valor original.");
            return dataDdMmAaaa;
        }
    }

    /**
     * Função auxiliar para copiar dados de um objeto Endereco de entidade para DTO.
     */
    private void copyEndereco(Ies.Endereco source, DadosDiplomaDTO.Endereco destination) {
        destination.setLogradouro(source.getLogradouro());
        destination.setNumero(source.getNumero());
        destination.setBairro(source.getBairro());
        destination.setCodigoMunicipio(source.getCodigoMunicipio());
        destination.setNomeMunicipio(source.getNomeMunicipio());
        destination.setUf(source.getUf());
        destination.setCep(source.getCep());
    }
}