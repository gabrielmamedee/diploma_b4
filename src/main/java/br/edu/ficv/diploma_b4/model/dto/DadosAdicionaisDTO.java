package br.edu.ficv.diploma_b4.model.dto;

import br.edu.ficv.diploma_b4.model.HistoricoAcademico.Naturalidade;
import lombok.Data;

/**
 * Esta classe representa os dados complementares do aluno que
 * não estão no PDF e serão enviados no corpo da requisição POST.
 */
@Data
public class DadosAdicionaisDTO {
    private String sexo;
    private Naturalidade naturalidade;

}