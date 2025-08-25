package br.edu.ficv.diploma_b4.model.dto;

import lombok.Data;

/**
 * Classe Raiz que representa a estrutura completa do JSON.
 */
@Data
public class DiplomaJsonDTO {
    private DadosDiplomaDTO diploma;
    //private DocumentacaoAcademicaDTO documentacaoAcademica;
    private String degreeType;
}