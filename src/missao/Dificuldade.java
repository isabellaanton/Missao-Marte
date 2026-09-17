package missao;

public enum Dificuldade {
    FACIL(30, 2, 1),
    MEDIO(20, 3, 2),
    DIFICIL(15, 5, 3);

    private final int pontosIniciais;
    private final int qtdAsteroides;
    private final int qtdInimigos;

    Dificuldade(int pontosIniciais, int qtdAsteroides, int qtdInimigos) {
        this.pontosIniciais = pontosIniciais;
        this.qtdAsteroides = qtdAsteroides;
        this.qtdInimigos = qtdInimigos;
    }

    public int getPontosIniciais() { return pontosIniciais; }
    public int getQtdAsteroides() { return qtdAsteroides; }
    public int getQtdInimigos() { return qtdInimigos; }
}