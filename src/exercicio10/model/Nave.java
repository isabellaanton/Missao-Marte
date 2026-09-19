package exercicio10.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Nave extends EntidadeMapa implements Movel, Posicionavel {    private final String nome;
    private final int capacidade;
    private final List<Passageiro> passageiros = new ArrayList<Passageiro>();
    private int vidas = 3;

    public Nave(String nome, int x, int y, int capacidade)  {
        super(x, y);
        this.nome = nome;
        this.capacidade = capacidade;
    }

    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public int getVidas() { return vidas; }
    public List<Passageiro> getPassageiros() { return Collections.unmodifiableList(passageiros); }
    public boolean embarcar(Passageiro passageiro) {
        if (passageiro == null || passageiros.size() >= capacidade) return false;
        passageiros.add(passageiro);
        return true;
    }
    public void perderVida() { vidas = Math.max(0, vidas - 1); }
    @Override public void mover(int dx, int dy) { x += dx; y += dy; }
    @Override public char getSimbolo() { return '@'; }

    public boolean moverComLimites(char comando, int min, int max) {
        int dx = 0;
        int dy = 0;
        switch (Character.toLowerCase(comando)) {
            case 's': dy = 1; break;
            case 'w': dy = -1; break;
            case 'a': dx = -1; break;
            case 'd': dx = 1; break;
            default: return false;
        }
        if (x + dx < min || x + dx > max || y + dy < min || y + dy > max) return false;
        mover(dx, dy);
        return true;
    }
}

