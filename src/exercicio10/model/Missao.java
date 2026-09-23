package solidexercicio10.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Missao {
    private final Nave nave;
    private final List<Passageiro> passageiros = new ArrayList<Passageiro>();
    private final List<Asteroide> asteroides = new ArrayList<Asteroide>();
    private final List<Inimigo> inimigos = new ArrayList<Inimigo>();

    public Missao(Nave nave) { this.nave = nave; }
    public Nave getNave() { return nave; }
    public List<Passageiro> getPassageiros() { return Collections.unmodifiableList(passageiros); }
    public List<Asteroide> getAsteroides() { return Collections.unmodifiableList(asteroides); }
    public List<Inimigo> getInimigos() { return Collections.unmodifiableList(inimigos); }
    public void adicionarPassageiro(Passageiro passageiro) { passageiros.add(passageiro); }
    public void adicionarAsteroide(Asteroide asteroide) { asteroides.add(asteroide); }
    public void adicionarInimigo(Inimigo inimigo) { inimigos.add(inimigo); }

    public Passageiro getPassageiroNaPosicaoDaNave() {
        for (Passageiro passageiro : passageiros) if (mesmaPosicao(passageiro, nave)) return passageiro;
        return null;
    }
    public boolean embarcarPassageiroNaPosicao() {
        Passageiro passageiro = getPassageiroNaPosicaoDaNave();
        if (passageiro == null || !nave.embarcar(passageiro)) return false;
        passageiros.remove(passageiro);
        return true;
    }
    public boolean verificaColisao() {
        for (Asteroide asteroide : asteroides) if (mesmaPosicao(asteroide, nave)) return true;
        for (Inimigo inimigo : inimigos) if (mesmaPosicao(inimigo, nave)) return true;
        return false;
    }
    public void moverInimigos(Random random, int min, int max) {
        for (Inimigo inimigo : inimigos) {
            int dx = random.nextInt(3) - 1;
            int dy = random.nextInt(3) - 1;
            if (inimigo.getX() + dx >= min && inimigo.getX() + dx <= max
                    && inimigo.getY() + dy >= min && inimigo.getY() + dy <= max) inimigo.mover(dx, dy);
        }
    }
    public boolean todosEmbarcados() { return passageiros.isEmpty(); }
    private boolean mesmaPosicao(Posicionavel primeira, Posicionavel segunda) {
        return primeira.getX() == segunda.getX() && primeira.getY() == segunda.getY();
    }
}
