package exercicio10.model;

public final class Asteroide extends EntidadeMapa implements Posicionavel{
    public Asteroide(int x, int y) { super(x, y); }
    @Override public char getSimbolo() { return '#'; }
}

