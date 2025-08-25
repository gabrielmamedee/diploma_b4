package br.edu.ficv.diploma_b4.service;

import br.edu.ficv.diploma_b4.model.Curso;
import br.edu.ficv.diploma_b4.model.HistoricoAcademico;
import br.edu.ficv.diploma_b4.model.Ies;
import br.edu.ficv.diploma_b4.repository.CursoRepository;
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

    @Autowired
    private CursoRepository cursoRepository;

    public DadosDiplomaDTO montarDadosDiploma(HistoricoAcademico historico) {
        Ies ies = iesRepository.findById("09491298000316")
                .orElseThrow(() -> new RuntimeException("Dados da IES Emissora com ID 09491298000316 não encontrados no banco."));

        if (historico.getCodigoCursoEMEC() == null) {
            throw new RuntimeException("O Histórico Acadêmico não possui um código de curso (codigoCursoEMEC) associado.");
        }
        Curso curso = cursoRepository.findById(historico.getCodigoCursoEMEC())
                .orElseThrow(() -> new RuntimeException("Dados do Curso com código " + historico.getCodigoCursoEMEC() + " não encontrados."));

        DadosDiplomaDTO dadosDiploma = new DadosDiplomaDTO();

        dadosDiploma.setDiplomado(mapearDiplomado(historico));
        dadosDiploma.setDataConclusao(formatarData(historico.getDataConclusaoCurso()));
        dadosDiploma.setDadosCurso(mapearDadosCurso(historico, curso, ies));
        dadosDiploma.setIesEmissora(mapearIesEmissora(ies));

        return dadosDiploma;
    }

    private DadosDiplomaDTO.Diplomado mapearDiplomado(HistoricoAcademico historico) {
        DadosDiplomaDTO.Diplomado diplomado = new DadosDiplomaDTO.Diplomado();
        diplomado.setId(historico.getMatricula());
        diplomado.setNome(historico.getNomeAluno());
        diplomado.setNacionalidade(historico.getNacionalidade());
        diplomado.setCpf(historico.getCpf().replaceAll("[^\\d]", ""));
        diplomado.setSexo(historico.getSexo());
        if (historico.getNaturalidade() != null) {
            DadosDiplomaDTO.Naturalidade naturalidadeDto = new DadosDiplomaDTO.Naturalidade();
            naturalidadeDto.setCodigoMunicipio(historico.getNaturalidade().getCodigoMunicipio());
            naturalidadeDto.setNomeMunicipio(historico.getNaturalidade().getNomeMunicipio());
            naturalidadeDto.setUf(historico.getNaturalidade().getUf());
            diplomado.setNaturalidade(naturalidadeDto);
        }
        diplomado.setDataNascimento(formatarData(historico.getDataNascimento()));
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

    private DadosDiplomaDTO.DadosCurso mapearDadosCurso(HistoricoAcademico historico, Curso curso, Ies ies) {
        DadosDiplomaDTO.DadosCurso dadosCursoDto = new DadosDiplomaDTO.DadosCurso();

        dadosCursoDto.setNomeCurso(curso.getNomeCurso());
        dadosCursoDto.setCodigoCursoEMEC(curso.getCodigoCursoEMEC());
        dadosCursoDto.setModalidade(curso.getModalidade());
        dadosCursoDto.setGrauConferido(curso.getGrauConferido());

        if (curso.getTituloConferido() != null) {
            DadosDiplomaDTO.TituloConferido tituloDto = new DadosDiplomaDTO.TituloConferido();
            tituloDto.setTitulo(curso.getTituloConferido().getTitulo());
            dadosCursoDto.setTituloConferido(tituloDto);
        }

        if (curso.getAutorizacao() != null) {
            DadosDiplomaDTO.AtoRegulatorio autorizacaoDto = new DadosDiplomaDTO.AtoRegulatorio();
            copyAtoRegulatorio(curso.getAutorizacao(), autorizacaoDto);
            dadosCursoDto.setAutorizacao(autorizacaoDto);
        }

        if (curso.getReconhecimento() != null) {
            DadosDiplomaDTO.AtoRegulatorio reconhecimentoDto = new DadosDiplomaDTO.AtoRegulatorio();
            copyAtoRegulatorio(curso.getReconhecimento(), reconhecimentoDto);
            dadosCursoDto.setReconhecimento(reconhecimentoDto);
        }

        //if (curso.getInformacoesTramitacaoEmec() != null) {
        //    DadosDiplomaDTO.InformacoesTramitacaoEmec infoDto = new DadosDiplomaDTO.InformacoesTramitacaoEmec();
        //    copyInformacoesTramitacaoEmec(curso.getInformacoesTramitacaoEmec(), infoDto);
        //    dadosCursoDto.setInformacoesTramitacaoEmec(infoDto);
        //}

        if (ies.getEndereco() != null) {
            DadosDiplomaDTO.Endereco enderecoDto = new DadosDiplomaDTO.Endereco();
            copyEndereco(ies.getEndereco(), enderecoDto);
            dadosCursoDto.setEnderecoCurso(enderecoDto);
        }

        return dadosCursoDto;
    }

    private DadosDiplomaDTO.IesEmissora mapearIesEmissora(Ies ies) {
        DadosDiplomaDTO.IesEmissora iesDto = new DadosDiplomaDTO.IesEmissora();
        iesDto.setNome(ies.getNome());
        iesDto.setCnpj(ies.getCnpj());
        iesDto.setCodigoMEC(ies.getCodigoMEC());

        if (ies.getEndereco() != null) {
            DadosDiplomaDTO.Endereco enderecoDto = new DadosDiplomaDTO.Endereco();
            copyEndereco(ies.getEndereco(), enderecoDto);
            iesDto.setEndereco(enderecoDto);
        }

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

        if (ies.getCredenciamento() != null) {
            DadosDiplomaDTO.Credenciamento credenciamentoDto = new DadosDiplomaDTO.Credenciamento();
            Ies.Credenciamento credenciamentoEntidade = ies.getCredenciamento();
            credenciamentoDto.setTipo(credenciamentoEntidade.getTipo());
            credenciamentoDto.setNumero(credenciamentoEntidade.getNumero());
            credenciamentoDto.setData(credenciamentoEntidade.getData());
            credenciamentoDto.setVeiculoPublicacao(credenciamentoEntidade.getVeiculoPublicacao());
            credenciamentoDto.setDataPublicacao(credenciamentoEntidade.getDataPublicacao());
            credenciamentoDto.setSecaoPublicacao((int) credenciamentoEntidade.getSecaoPublicacao());
            credenciamentoDto.setPaginaPublicacao((int) credenciamentoEntidade.getPaginaPublicacao());
            credenciamentoDto.setNumeroDOU((int) credenciamentoEntidade.getNumeroDOU());
            iesDto.setCredenciamento(credenciamentoDto);
        }

        if (ies.getRecredenciamento() != null) {
            DadosDiplomaDTO.Recredenciamento recredenciamentoDto = new DadosDiplomaDTO.Recredenciamento();
            DadosDiplomaDTO.InformacoesTramitacaoEmec infoDto = new DadosDiplomaDTO.InformacoesTramitacaoEmec();
            Ies.InformacoesTramitacaoEmec infoEntidade = ies.getRecredenciamento().getInformacoesTramitacaoEmec();
            copyInformacoesTramitacaoEmec(infoEntidade, infoDto);
            recredenciamentoDto.setInformacoesTramitacaoEmec(infoDto);
            iesDto.setRecredenciamento(recredenciamentoDto);
        }

        return iesDto;
    }

    private String formatarData(String dataDdMmAaaa) {
        if (dataDdMmAaaa == null || dataDdMmAaaa.isBlank()) return null;
        try {
            return LocalDate.parse(dataDdMmAaaa, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.err.println("AVISO: Não foi possível converter a data '" + dataDdMmAaaa + "'. Verifique o formato. Retornando valor original.");
            return dataDdMmAaaa;
        }
    }

    private void copyEndereco(Ies.Endereco source, DadosDiplomaDTO.Endereco destination) {
        destination.setLogradouro(source.getLogradouro());
        destination.setNumero(source.getNumero());
        destination.setBairro(source.getBairro());
        destination.setCodigoMunicipio(source.getCodigoMunicipio());
        destination.setNomeMunicipio(source.getNomeMunicipio());
        destination.setUf(source.getUf());
        destination.setCep(source.getCep());
    }

    private void copyAtoRegulatorio(Curso.AtoRegulatorio source, DadosDiplomaDTO.AtoRegulatorio destination) {
        destination.setTipo(source.getTipo());
        destination.setNumero(source.getNumero());
        destination.setData(source.getData());
        destination.setVeiculoPublicacao(source.getVeiculoPublicacao());
        destination.setDataPublicacao(source.getDataPublicacao());
    }

    private void copyInformacoesTramitacaoEmec(Curso.InformacoesTramitacaoEmec source, DadosDiplomaDTO.InformacoesTramitacaoEmec destination) {
        destination.setNumeroProcesso(source.getNumeroProcesso());
        destination.setTipoProcesso(source.getTipoProcesso());
        destination.setDataCadastro(source.getDataCadastro());
        destination.setDataProtocolo(source.getDataProtocolo());
    }
    // No método mapearIesEmissora, a infoEmec vem da entidade Ies, então precisamos de uma sobrecarga do método.
    private void copyInformacoesTramitacaoEmec(Ies.InformacoesTramitacaoEmec source, DadosDiplomaDTO.InformacoesTramitacaoEmec destination) {
        destination.setNumeroProcesso(source.getNumeroProcesso());
        destination.setTipoProcesso(source.getTipoProcesso());
        destination.setDataCadastro(source.getDataCadastro());
        destination.setDataProtocolo(source.getDataProtocolo());
    }
}