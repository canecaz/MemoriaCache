package memoriacache.config;

import memoriacache.enums.Substitution;

public class Config {

    private int politicaEscrita, hitTime, readTime, writeTime, tamanhoLinha, numeroLinhas,
            associatividade, numeroConjuntos;
    private Substitution substitution;
    private String arquivoTexto, arquivoEntrada;

    public int getPoliticaEscrita() {
        return politicaEscrita;
    }

    public void setPoliticaEscrita(int politicaEscrita) {
        this.politicaEscrita = politicaEscrita;
    }

    public int getHitTime() {
        return hitTime;
    }

    public void setHitTime(int hitTime) {
        this.hitTime = hitTime;
    }

    public int getAssociatividade() {
        return associatividade;
    }

    public void setAssociatividade(int associatividade) {
        this.associatividade = associatividade;
    }

    public int getNumeroLinhas() {
        return numeroLinhas;
    }

    public void setNumeroLinhas(int numeroLinhas) {
        this.numeroLinhas = numeroLinhas;
    }

    public int getTamanhoLinha() {
        return tamanhoLinha;
    }

    public void setTamanhoLinha(int tamanhoLinha) {
        this.tamanhoLinha = tamanhoLinha;
    }

    public Substitution getSubstitution() {
        return substitution;
    }

    public void setSubstitution(Substitution substitution) {
        this.substitution = substitution;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }

    public int getReadTime() {
        return readTime;
    }

    public void setWriteTime(int writeTime) {
        this.writeTime = writeTime;
    }

    public int getWriteTime() {
        return writeTime;
    }

    public String getArquivoTexto() {
        return arquivoTexto;
    }

    public void setArquivoTexto(String arquivoTexto) {
        this.arquivoTexto = arquivoTexto;
    }

    public String getArquivoEntrada() {
        return arquivoEntrada;
    }

    public void setArquivoEntrada(String arquivoEntrada) {
        this.arquivoEntrada = arquivoEntrada;
    }

    public int getNumeroConjuntos() {
        return numeroConjuntos;
    }

    public void setNumeroConjuntos(int numeroConjuntos) {
        this.numeroConjuntos = numeroConjuntos;
    }
}
