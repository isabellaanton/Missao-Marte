package missao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    // Cores ANSI para formatação do console (sem emojis)
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BOLD = "\u001B[1m";

    public static void main(String[] args) {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);
        Path rankingPath = Paths.get("ranking.json");

        boolean executando = true;
        while (executando) {
            List<RankingEntry> ranking = loadRanking(rankingPath);

            System.out.println(ANSI_CYAN + "================================================================" + ANSI_RESET);
            System.out.println(ANSI_BOLD + "                MISSÃO MARTE UNIFOR — MENU                      " + ANSI_RESET);
            System.out.println(ANSI_CYAN + "================================================================" + ANSI_RESET);
            System.out.println("1. Iniciar Missão");
            System.out.println("2. Ver Ranking");
            System.out.println("3. Resetar Ranking");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1":
                    iniciarJogo(scanner, random, rankingPath, ranking);
                    break;
                case "2":
                    exibirRanking(ranking);
                    break;
                case "3":
                    ranking.clear();
                    saveRanking(rankingPath, ranking);
                    System.out.println(ANSI_YELLOW + "\n-> Ranking resetado com sucesso!\n" + ANSI_RESET);
                    break;
                case "4":
                    executando = false;
                    System.out.println("\nEncerrando o sistema da Missão Marte. Até breve!");
                    break;
                default:
                    System.out.println(ANSI_RED + "Opção inválida. Tente novamente.\n" + ANSI_RESET);
            }
        }
        scanner.close();
    }

    private static void iniciarJogo(Scanner scanner, Random random, Path rankingPath, List<RankingEntry> ranking) {
        System.out.print("\nDigite o nome do piloto: ");
        String pilotoNome = scanner.nextLine().trim();
        if (pilotoNome.isEmpty()) {
            pilotoNome = "Piloto Anônimo";
        }

        System.out.println("\nEscolha o Nível de Dificuldade:");
        System.out.println("1. FÁCIL   (30 pts iniciais, 2 Asteroides, 1 Inimigo)");
        System.out.println("2. MÉDIO   (20 pts iniciais, 3 Asteroides, 2 Inimigos)");
        System.out.println("3. DIFÍCIL (15 pts iniciais, 5 Asteroides, 3 Inimigos)");
        System.out.print("Opção (padrão 2): ");
        String difInput = scanner.nextLine().trim();

        Dificuldade dificuldade = Dificuldade.MEDIO;
        if (difInput.equals("1")) dificuldade = Dificuldade.FACIL;
        else if (difInput.equals("3")) dificuldade = Dificuldade.DIFICIL;

        System.out.print("Digite a dimensão do mapa (ex: 5 para mapa de -5 a 5): ");
        int dimensao = 5;
        try {
            String inputDim = scanner.nextLine().trim();
            if (!inputDim.isEmpty()) {
                dimensao = Integer.parseInt(inputDim);
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Usando dimensão padrão: 5");
        }

        int minX = -dimensao;
        int maxX = dimensao;
        int minY = -dimensao;
        int maxY = dimensao;

        Missao missao = criarNovaMissao(random, minX, maxX, minY, maxY, dificuldade);
        Nave nave = missao.getNave();
        int score = dificuldade.getPontosIniciais();
        boolean running = true;
        int passageirosResgatadosLocal = 0;

        System.out.println("\nPressione Enter para iniciar a missão...");
        scanner.nextLine();

        while (running) {
            desenharMapa(missao, minX, maxX, minY, maxY, score, pilotoNome, dificuldade);

            System.out.printf(ANSI_BOLD + "Nave (%d,%d) | Vidas: %d | Pontos: %d | A bordo: %d | Restantes: %d%n" + ANSI_RESET,
                    nave.getX(), nave.getY(), nave.getVidas(), score, nave.getPassageiros().size(), missao.getPassageiros().size());

            System.out.print("Comando (w=cima, s=baixo, a=esq, d=dir, c=embarcar, q=desistir): ");
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.isEmpty()) continue;
            char cmd = line.charAt(0);

            switch (cmd) {
                case 'w': nave.moveUp(); score--; break;
                case 's': nave.moveDown(); score--; break;
                case 'a': nave.moveLeft(); score--; break;
                case 'd': nave.moveRight(); score--; break;
                case 'c': {
                    Passageiro p = missao.passagemNaPosicao();
                    if (p == null) {
                        System.out.println(ANSI_YELLOW + "Nenhum passageiro nesta posição." + ANSI_RESET);
                    } else {
                        int pontosGanhos = p.getPontuacao(); // Chamada Polimórfica
                        boolean ok = missao.embarcarPassageiroNaPosicao();
                        if (ok) {
                            score += pontosGanhos;
                            passageirosResgatadosLocal++;
                            System.out.println(ANSI_GREEN + "Passageiro " + p.getNome() + " embarcado! +" + pontosGanhos + " pontos!" + ANSI_RESET);
                        } else {
                            System.out.println(ANSI_RED + "Nave cheia, não foi possível embarcar." + ANSI_RESET);
                        }
                    }
                    break;
                }
                case 'q':
                    System.out.println(ANSI_YELLOW + "Missão abortada pelo piloto." + ANSI_RESET);
                    running = false;
                    break;
                default:
                    System.out.println("Comando desconhecido.");
                    continue;
            }

            if (!running) break;

            // Movimentação dos inimigos a cada turno (Nível 3)
            for (Inimigo inimigo : missao.getInimigo()) {
                inimigo.moverAleatoriamente(minX, maxX, minY, maxY);
            }

            // Verificação de colisões (Asteroides e Inimigos)
            if (missao.verificaColisao()) {
                nave.perderVida();
                if (nave.getVidas() > 0) {
                    System.out.println(ANSI_RED + "COLISÃO DETECTADA! Você perdeu 1 vida. Vidas restantes: " + nave.getVidas() + ANSI_RESET);
                    nave.resetPosicao();
                } else {
                    System.out.println(ANSI_RED + "SUA NAVE FOI DESTRUÍDA! Sem vidas restantes. Fim de jogo." + ANSI_RESET);
                    break;
                }
            }

            // Verificação de energia/pontuação
            if (score <= 0) {
                System.out.println(ANSI_RED + "Pontuação zerada! Sem energia para continuar." + ANSI_RESET);
                break;
            }

            // Condição de Vitória: Todos embarcados e Pouso na Plataforma (0, 0)
            if (missao.todosEmbarcados()) {
                if (nave.getX() == 0 && nave.getY() == 0) {
                    System.out.println(ANSI_GREEN + ANSI_BOLD + "\nPARABÉNS! Pouso realizado na Plataforma (0,0) com todos os passageiros a bordo!" + ANSI_RESET);
                    System.out.printf("Pontuação final: %d pontos%n", score);

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    String dataHora = LocalDateTime.now().format(dtf);

                    RankingEntry novaEntrada = new RankingEntry(pilotoNome, score, dataHora, dificuldade.name(), passageirosResgatadosLocal);
                    ranking.add(novaEntrada);
                    ranking.sort(Comparator.comparingInt(RankingEntry::getScore).reversed());
                    if (ranking.size() > 5) {
                        ranking = new ArrayList<>(ranking.subList(0, 5));
                    }
                    saveRanking(rankingPath, ranking);
                    System.out.println(ANSI_GREEN + "Sua pontuação foi gravada no Ranking com sucesso!\n" + ANSI_RESET);
                    break;
                }
            }
        }
    }

    private static Missao criarNovaMissao(Random random, int minX, int maxX, int minY, int maxY, Dificuldade dif) {
        int cargaPassageiros = 4;
        Nave nave = new Nave("A-1", cargaPassageiros);
        Missao missao = new Missao(nave);

        // Adiciona passageiros (incluindo a subclasse Astronauta)
        while (missao.getPassageiros().size() < cargaPassageiros) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            if (x == nave.getX() && y == nave.getY()) continue;
            if (posicaoOcupada(missao, x, y)) continue;

            int qtdAtual = missao.getPassageiros().size();
            if (qtdAtual == 0) {
                missao.addPassageiro(new Professor("Dr. Silva", x, y));
            } else if (qtdAtual == 1) {
                missao.addPassageiro(new Engenheiro("Eng. Rosa", x, y));
            } else if (qtdAtual == 2) {
                missao.addPassageiro(new Astronauta("Astro. Marcos", x, y));
            } else {
                missao.addPassageiro(new Professor("Dra. Lima", x, y));
            }
        }

        // Adiciona asteroides conforme a dificuldade
        while (missao.getAsteroides().size() < dif.getQtdAsteroides()) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            if (x == nave.getX() && y == nave.getY()) continue;
            if (posicaoOcupada(missao, x, y)) continue;
            missao.addAsteroide(new Asteroide(x, y));
        }

        // Adiciona inimigos conforme a dificuldade
        while (missao.getInimigo().size() < dif.getQtdInimigos()) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            if (x == nave.getX() && y == nave.getY()) continue;
            if (posicaoOcupada(missao, x, y)) continue;
            missao.addInimigo(new Inimigo(x, y));
        }

        return missao;
    }

    private static boolean posicaoOcupada(Missao missao, int x, int y) {
        if (missao.getNave().getX() == x && missao.getNave().getY() == y) return true;
        for (Passageiro p : missao.getPassageiros()) {
            if (p.getX() == x && p.getY() == y) return true;
        }
        for (Asteroide a : missao.getAsteroides()) {
            if (a.getX() == x && a.getY() == y) return true;
        }
        for (Inimigo i : missao.getInimigo()) {
            if (i.getX() == x && i.getY() == y) return true;
        }
        return false;
    }

    private static void desenharMapa(Missao missao, int minX, int maxX, int minY, int maxY, int score, String pilotoNome, Dificuldade dif) {
        System.out.println();
        System.out.printf(ANSI_BOLD + "Mapa da Missão (Pontos: %d) | Piloto: %s | Dificuldade: %s%n" + ANSI_RESET, score, pilotoNome, dif.name());
        System.out.print("    ");
        for (int x = minX; x <= maxX; x++) {
            System.out.printf(" %2d", x);
        }
        System.out.println();
        System.out.print("    ");
        for (int x = minX; x <= maxX; x++) {
            System.out.print(" __");
        }
        System.out.println();

        Nave nave = missao.getNave();

        for (int y = minY; y <= maxY; y++) {
            System.out.printf("%3d|", y);
            for (int x = minX; x <= maxX; x++) {
                String charStr = " .";

                if (nave.getX() == x && nave.getY() == y) {
                    charStr = ANSI_GREEN + ANSI_BOLD + " N" + ANSI_RESET;
                } else {
                    boolean achou = false;
                    for (Passageiro p : missao.getPassageiros()) {
                        if (p.getX() == x && p.getY() == y) {
                            charStr = ANSI_CYAN + ANSI_BOLD + " " + p.getSimbolo() + ANSI_RESET;
                            achou = true;
                            break;
                        }
                    }
                    if (!achou) {
                        for (Asteroide a : missao.getAsteroides()) {
                            if (a.getX() == x && a.getY() == y) {
                                charStr = ANSI_RED + " *" + ANSI_RESET; // Asteroides representados por '*'
                                achou = true;
                                break;
                            }
                        }
                    }
                    if (!achou) {
                        for (Inimigo i : missao.getInimigo()) {
                            if (i.getX() == x && i.getY() == y) {
                                charStr = ANSI_PURPLE + " I" + ANSI_RESET;
                                achou = true;
                                break;
                            }
                        }
                    }
                    if (!achou && x == 0 && y == 0) {
                        charStr = ANSI_YELLOW + ANSI_BOLD + " L" + ANSI_RESET; // Plataforma de Pouso
                    }
                }
                System.out.print(" " + charStr);
            }
            System.out.println();
        }

        System.out.println(ANSI_WHITE + "Legenda: "
                + ANSI_GREEN + "N=Nave" + ANSI_WHITE + ", "
                + ANSI_CYAN + "P=Professor, E=Engenheiro, A=Astronauta" + ANSI_WHITE + ", "
                + ANSI_PURPLE + "I=Inimigo" + ANSI_WHITE + ", "
                + ANSI_RED + "*=Asteroide" + ANSI_WHITE + ", "
                + ANSI_YELLOW + "L=Plataforma (0,0)" + ANSI_RESET);

        if (missao.todosEmbarcados()) {
            System.out.println(ANSI_YELLOW + ANSI_BOLD + ">>> TODOS OS PASSAGEIROS FORAM RESGATADOS! Navegue até a Plataforma L em (0,0) para pousar! <<<" + ANSI_RESET);
        } else {
            System.out.println("Passageiros restantes na superfície:");
            for (Passageiro p : missao.getPassageiros()) {
                System.out.printf(" - %s (%s, +%d pts) em (%d,%d)%n", p.getNome(), p.getTipo(), p.getPontuacao(), p.getX(), p.getY());
            }
        }
        System.out.println();
    }

    private static void exibirRanking(List<RankingEntry> ranking) {
        System.out.println("\n" + ANSI_CYAN + "================ RANKING DOS MELHORES PILOTOS ================" + ANSI_RESET);
        if (ranking.isEmpty()) {
            System.out.println(" - Nenhuma pontuação registrada até o momento.");
        } else {
            int pos = 1;
            for (RankingEntry e : ranking) {
                System.out.printf("%d. %s — %d pts | Dif: %s | Resgatados: %d | Data: %s%n",
                        pos++, e.getName(), e.getScore(), e.getDificuldade(), e.getPassageirosResgatados(), e.getDataHora());
            }
        }
        System.out.println(ANSI_CYAN + "==============================================================\n" + ANSI_RESET);
    }

    // =========================================================================
    // PERSISTÊNCIA EM JSON (MÉTODOS DE LEITURA, ESCRITA E PARSER MANUAL)
    // =========================================================================

    private static List<RankingEntry> loadRanking(Path path) {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
            return parseRankingJson(json);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private static void saveRanking(Path path, List<RankingEntry> ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < ranking.size(); i++) {
            RankingEntry e = ranking.get(i);
            sb.append("  {\n");
            sb.append("    \"name\": \"").append(escapeJson(e.getName())).append("\",\n");
            sb.append("    \"score\": ").append(e.getScore()).append(",\n");
            sb.append("    \"dataHora\": \"").append(escapeJson(e.getDataHora())).append("\",\n");
            sb.append("    \"dificuldade\": \"").append(escapeJson(e.getDificuldade())).append("\",\n");
            sb.append("    \"passageirosResgatados\": ").append(e.getPassageirosResgatados()).append("\n");
            sb.append("  }");
            if (i < ranking.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");

        try {
            Files.write(path, sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Erro ao salvar o ranking: " + e.getMessage() + ANSI_RESET);
        }
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static List<RankingEntry> parseRankingJson(String json) {
        List<RankingEntry> list = new ArrayList<>();
        if (json == null || json.trim().isEmpty() || json.trim().equals("[]")) {
            return list;
        }

        json = json.trim();
        int index = 0;
        while (index < json.length()) {
            int start = json.indexOf('{', index);
            if (start < 0) break;
            int end = json.indexOf('}', start);
            if (end < 0) break;
            String objStr = json.substring(start + 1, end);

            String name = "Anônimo";
            int score = 0;
            String dataHora = "-";
            String dificuldade = "MEDIO";
            int passageirosResgatados = 0;

            String[] pairs = objStr.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length != 2) continue;
                String key = kv[0].trim().replace("\"", "");
                String val = kv[1].trim();

                if (val.startsWith("\"") && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
                }

                switch (key) {
                    case "name":
                        name = val;
                        break;
                    case "score":
                        try { score = Integer.parseInt(val); } catch (NumberFormatException ignored) {}
                        break;
                    case "dataHora":
                        dataHora = val;
                        break;
                    case "dificuldade":
                        dificuldade = val;
                        break;
                    case "passageirosResgatados":
                        try { passageirosResgatados = Integer.parseInt(val); } catch (NumberFormatException ignored) {}
                        break;
                }
            }
            list.add(new RankingEntry(name, score, dataHora, dificuldade, passageirosResgatados));
            index = end + 1;
        }

        list.sort(Comparator.comparingInt(RankingEntry::getScore).reversed());
        return list;
    }

    // Classe Interna para representar uma entrada do Ranking
    private static class RankingEntry {
        private final String name;
        private final int score;
        private final String dataHora;
        private final String dificuldade;
        private final int passageirosResgatados;

        public RankingEntry(String name, int score, String dataHora, String dificuldade, int passageirosResgatados) {
            this.name = name;
            this.score = score;
            this.dataHora = dataHora;
            this.dificuldade = dificuldade;
            this.passageirosResgatados = passageirosResgatados;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
        public String getDataHora() { return dataHora; }
        public String getDificuldade() { return dificuldade; }
        public int getPassageirosResgatados() { return passageirosResgatados; }
    }
}