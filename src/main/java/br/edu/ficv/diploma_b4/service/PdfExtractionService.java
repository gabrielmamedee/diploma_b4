package br.edu.ficv.diploma_b4.service;

import br.edu.ficv.diploma_b4.model.Disciplina;
import br.edu.ficv.diploma_b4.model.HistoricoAcademico;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfExtractionService {

    public HistoricoAcademico extrairDados(InputStream pdfInputStream) throws IOException {

        byte[] allBytes = pdfInputStream.readAllBytes();

        try (PDDocument document = Loader.loadPDF(allBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String textoCompleto = stripper.getText(document);

            HistoricoAcademico historico = new HistoricoAcademico();

            // Extracao dados simples do aluno e do curso
            historico.setNomeAluno(extrairValor(textoCompleto, "Nome do\\(a\\) aluno\\(a\\): (.*)"));
            historico.setDataNascimento(extrairValor(textoCompleto, "Data de nascimento:\\s*(\\d{2}/\\d{2}/\\d{4})"));
            historico.setNacionalidade(extrairValor(textoCompleto, "Nacionalidade: (.*)"));
            historico.setRg(extrairValor(textoCompleto, "R\\.G:\\s*(.*?)\\s*Nº da matrícula:"));
            historico.setMatricula(extrairValor(textoCompleto, "Nº da matrícula:\\s*(.*?)\\s*CPF:"));
            historico.setCpf(extrairValor(textoCompleto, "CPF: (.*)"));

            historico.setAnoSemestreIngresso(extrairValor(textoCompleto, "Ano/Semestre:\\s*(.*?)\\s*Realizado em:"));
            historico.setDataRealizacaoProcessoSeletivo(extrairValor(textoCompleto, "Realizado em: (.*)"));
            historico.setFormaIngresso(extrairValor(textoCompleto, "Forma de Ingresso: (.*)"));
            historico.setNomeCurso(extrairValor(textoCompleto, "Curso:\\s*(.*?)\\s*Período de realização do curso:"));

            String periodoRealizacao = extrairValor(textoCompleto, "Período de realização do curso: (.*)");
            if (periodoRealizacao != null && periodoRealizacao.contains("à")) {
                String[] datas = periodoRealizacao.split("à");
                historico.setInicioRealizacaoCurso(datas[0].trim());
                historico.setFimRealizacaoCurso(datas[1].trim());
            }

            // Extracao dados de desempenho
            String craString = extrairValor(textoCompleto, "Coeficiente de Rendimento \\(C\\.R\\.A\\): ([\\d,.]+)");
            if (craString != null) {
                historico.setCoeficienteRendimento(Double.parseDouble(craString.replace(",", ".")));
            }

            String totalHorasString = extrairValor(textoCompleto, "Total de Horas-Aula: (\\d+)");
            if(totalHorasString != null) {
                historico.setTotalHorasAula(Double.parseDouble(totalHorasString));
            }

            String atividadesCompString = extrairValor(textoCompleto, "Atividades Complementares: (\\d+)");
            if(atividadesCompString != null) {
                historico.setHorasAtividadesComplementares(Double.parseDouble(atividadesCompString));
            }

            // Extracao observações e datas finais
            historico.setSituacaoENADE(extrairValor(textoCompleto, "Situação ENADE: (.*)"));
            historico.setDataConclusaoCurso(extrairValor(textoCompleto, "Data de Conclusão do curso: (.*)"));
            historico.setDataColacaoGrau(extrairValor(textoCompleto, "Data da Colação de Grau: (.*)"));
            historico.setDataExpedicaoDiploma(extrairValor(textoCompleto, "Data da Expedição do Diploma: (.*)"));

            // Extracao lista de disciplinas
            historico.setDisciplinas(extrairDisciplinasDaTabela(textoCompleto));

            return historico;
        }
    }

    private String extrairValor(String texto, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private List<Disciplina> extrairDisciplinasDaTabela(String texto) {
        List<Disciplina> disciplinas = new ArrayList<>();
        final String regex = "(\\d{2,3}h/a)\\s+([A-Z]{2})\\s+([\\d,]+)\\s+([A-Z]{2,5})";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(texto);

        int lastIndex = 0;
        String inicioTabelaMarker = "DISCIPLINAS";
        int inicioTabela = texto.indexOf(inicioTabelaMarker);
        if (inicioTabela != -1) {
            lastIndex = inicioTabela + inicioTabelaMarker.length();
        }

        while (matcher.find(lastIndex)) {
            int startOfMatch = matcher.start();
            String blocoAnterior = texto.substring(lastIndex, startOfMatch).trim();

            Pattern padraoDisciplina = Pattern.compile("(\\d{4}\\.\\d)\\s+(\\d+º Sem)\\s+(.*)", Pattern.DOTALL);
            Matcher matcherDisciplina = padraoDisciplina.matcher(blocoAnterior);

            if (matcherDisciplina.find()) {
                Disciplina disciplina = new Disciplina();

                disciplina.setPeriodo(matcherDisciplina.group(1).trim());
                disciplina.setSemestre(matcherDisciplina.group(2).trim());

                String blocoNomeCompleto = matcherDisciplina.group(3);
                String nomeDisciplina;
                String nomeDocente = null;

                Pattern docentePattern = Pattern.compile("\\s+(Prof[ª\\.]{1,2}.*)", Pattern.DOTALL);
                Matcher docenteMatcher = docentePattern.matcher(blocoNomeCompleto);

                if (docenteMatcher.find()) {
                    nomeDisciplina = blocoNomeCompleto.substring(0, docenteMatcher.start()).replace("\n", " ").trim();
                    nomeDocente = docenteMatcher.group(1).replace("\n", " ").trim();
                } else {
                    nomeDisciplina = blocoNomeCompleto.replace("\n", " ").trim();
                }

                disciplina.setNome(nomeDisciplina);
                disciplina.setDocente(nomeDocente);

                disciplina.setCargaHoraria(matcher.group(1).trim());
                disciplina.setFrequencia(matcher.group(2).trim());
                disciplina.setMedia(Double.parseDouble(matcher.group(3).replace(",", ".")));
                disciplina.setSituacao(matcher.group(4).trim());

                disciplinas.add(disciplina);
            }
            lastIndex = matcher.end();
        }
        return disciplinas;
    }
}