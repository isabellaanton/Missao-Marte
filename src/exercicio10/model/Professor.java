package exercicio10.model;

public final class Professor extends Passageiro {
    public Professor(String nome, int x, int y) { super(nome, "Professor", x, y); }
    @Override public int getPontuacao() { return 15; }
    @Override public char getSimbolo() { return 'P'; }
}

