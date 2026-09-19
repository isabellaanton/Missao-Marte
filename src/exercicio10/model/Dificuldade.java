package exercicio10.model;

public enum Dificuldade {
    FACIL(30, 4, 1, 1),
    MEDIO(20, 5, 2, 2),
    DIFICIL(15, 6, 3, 3);

    private final int pontosIniciais;
    private final int passageiros;
    private final int asteroides;
    private final int inimigos;

    Dificuldade(int pontosIniciais, int passageiros, int asteroides, int inimigos) {
        this.pontosIniciais = pontosIniciais;
        this.passageiros = passageiros;
        this.asteroides = asteroides;
        this.inimigos = inimigos;
    }

    public int getPontosIniciais() { return pontosIniciais; }
    public int getPassageiros() { return passageiros; }
    public int getAsteroides() { return asteroides; }
    public int getInimigos() { return inimigos; }

    public static Dificuldade deString(String valor) {
        if (valor == null) return MEDIO;
        String normalizado = valor.trim().toUpperCase()
                .replace("Ã", "A").replace("Ã‰", "E").replace("Ã", "I")
                .replace("Ã“", "O").replace("Ãš", "U");
        for (Dificuldade dificuldade : values()) {
            if (dificuldade.name().equals(normalizado)) return dificuldade;
        }
        return MEDIO;
    }
}

