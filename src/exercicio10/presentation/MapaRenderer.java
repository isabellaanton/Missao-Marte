package solidexercicio10.presentation;

import solidexercicio10.model.Asteroide;
import solidexercicio10.model.Inimigo;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Passageiro;

/** Responsável apenas por transformar o estado da missão em texto no console. */
public final class MapaRenderer {
    public void desenhar(Missao missao, int pontos, String piloto, int min, int max) {
        System.out.printf("%n%s%n", ConsoleColors.colorir("=== MISSÃO MARTE ===", ConsoleColors.CYAN + ConsoleColors.BOLD));
        System.out.printf("Piloto: %s | Pontos: %s | Vidas: %s%n",
                ConsoleColors.colorir(piloto, ConsoleColors.WHITE),
                ConsoleColors.colorir(String.valueOf(pontos), ConsoleColors.GREEN + ConsoleColors.BOLD),
                ConsoleColors.colorir(String.valueOf(missao.getNave().getVidas()), ConsoleColors.RED + ConsoleColors.BOLD));
        for (int y = max; y >= min; y--) {
            System.out.printf("%s |", ConsoleColors.colorir(String.format("%3d", y), ConsoleColors.WHITE));
            for (int x = min; x <= max; x++) System.out.printf(" %s", ConsoleColors.colorir(simboloNaPosicao(missao, x, y)));
            System.out.println();
        }
        System.out.println(ConsoleColors.WHITE + "     " + eixoX(min, max) + ConsoleColors.RESET);
        System.out.println("Legenda: " + ConsoleColors.GREEN + "@ nave" + ConsoleColors.WHITE + " | "
                + ConsoleColors.YELLOW + "L base" + ConsoleColors.WHITE + " | "
                + ConsoleColors.CYAN + "P professor / E engenheiro / T astronauta" + ConsoleColors.WHITE + " | "
                + ConsoleColors.RED + "# asteroide" + ConsoleColors.WHITE + " | "
                + ConsoleColors.PURPLE + "X inimigo" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "Comandos: W/A/S/D mover, C embarcar, Q encerrar missão." + ConsoleColors.RESET);
    }

    private String eixoX(int min, int max) {
        StringBuilder resultado = new StringBuilder();
        for (int x = min; x <= max; x++) resultado.append(String.format(" %d", Math.abs(x) % 10));
        return resultado.toString();
    }
    private char simboloNaPosicao(Missao missao, int x, int y) {
        if (missao.getNave().getX() == x && missao.getNave().getY() == y) return '@';
        if (x == 0 && y == 0) return 'L';
        for (Passageiro p : missao.getPassageiros()) if (p.getX() == x && p.getY() == y) return p.getSimbolo();
        for (Asteroide a : missao.getAsteroides()) if (a.getX() == x && a.getY() == y) return a.getSimbolo();
        for (Inimigo i : missao.getInimigos()) if (i.getX() == x && i.getY() == y) return i.getSimbolo();
        return '.';
    }
}
