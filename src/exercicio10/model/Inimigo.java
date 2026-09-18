package exercicio10.model;

public final class Inimigo extends EntidadeMapa implements Posicionavel {
    public Inimigo(int x, int y) { super(x, y); }
    @Override public void mover(int dx, int dy) { x += dx; y += dy; }
    @Override public char getSimbolo() { return 'X'; }
}

