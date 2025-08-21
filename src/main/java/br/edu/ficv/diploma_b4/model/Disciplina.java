package br.edu.ficv.diploma_b4.model;

import lombok.Data;

@Data
public class Disciplina {
    private String periodo;
    private String semestre;
    private String nome;
    private String docente;
    private String cargaHoraria;
    private String frequencia;
    private double media;
    private String situacao;
}
