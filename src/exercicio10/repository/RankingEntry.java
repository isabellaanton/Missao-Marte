package solidexercicio10.repository;

import solidexercicio10.model.Dificuldade;

public final class RankingEntry {
    private final String nome;
    private final int pontos;
    private final Dificuldade dificuldade;
    private final int passageiros;
    private final long tempoSegundos;

    public RankingEntry(String nome, int pontos, Dificuldade dificuldade, int passageiros, long tempoSegundos) {
        this.nome = nome;
        this.pontos = pontos;
        this.dificuldade = dificuldade;
        this.passageiros = passageiros;
        this.tempoSegundos = tempoSegundos;
    }
    public String getNome() { return nome; }
    public int getPontos() { return pontos; }
    public Dificuldade getDificuldade() { return dificuldade; }
    public int getPassageiros() { return passageiros; }
    public long getTempoSegundos() { return tempoSegundos; }
}
