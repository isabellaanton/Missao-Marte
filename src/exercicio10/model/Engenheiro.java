package exercicio10.model;

public final class Engenheiro extends Passageiro {
    public Engenheiro(String nome, int x, int y) { super(nome, "Engenheiro", x, y); }
    @Override public int getPontuacao() { return 20; }
    @Override public char getSimbolo() { return 'E'; }
}

