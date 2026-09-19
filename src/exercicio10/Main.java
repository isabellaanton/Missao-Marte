package exercicio10;

import exercicio10.model.*;
import exercicio10.presentation.MapaView;
import exercicio10.repository.RankingJsonRepository;
import exercicio10.service.PassageiroFactory;
import exercicio10.repository.RankingRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Solução Completa do Exercício 10: Mini-jogo com todas as features e boas práticas
 * 
 * Modificações e adições realizadas:
 * 1. Menu inicial estruturado com opções (Jogar, Ver Ranking, Resetar Ranking, Sair) ✓
 * 2. Utilização de Enum (Dificuldade) para representar as dificuldades ✓
 * 3. Estatísticas de jogo (Tempo de partida em segundos, movimentos efetuados, recordes) ✓
 * 4. Opção de resetar/limpar o histórico de ranking ✓
 * 5. Refatoração da classe Main em métodos menores e coesos (Separation of Concerns) ✓
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        RankingRepository repositorio = new RankingJsonRepository();
        List<RankingEntry> ranking = repositorio.carregar();

        exibirBoasVindas();

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            String opcao = lerLinha(scanner, "Escolha uma opção: ", "1");
            switch (opcao) {
                case "1":
                    jogarPartida(scanner, random, ranking, repositorio);
                    // Atualiza a lista em memória após jogar
                    ranking = repositorio.carregar();
                    break;
                case "2":
                    exibirRankingCompleto(ranking);
                    break;
                case "3":
                    ranking = resetarRanking(scanner, repositorio);
                    break;
                case "4":
                    rodando = false;
                    System.out.println("\nObrigado por jogar a Missão Marte Unifor!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }

    private static void exibirBoasVindas() {
        System.out.println("================================================================");
        System.out.println("             MISSÃO MARTE UNIFOR - VERSÃO 10.0                  ");
        System.out.println("================================================================");
        System.out.println("  Pilote sua nave, salve os passageiros e desvie dos perigos!   ");
        System.out.println("================================================================");
    }

    private static void exibirMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Iniciar Nova Missão");
        System.out.println("2. Visualizar Ranking Top 5");
        System.out.println("3. Resetar Histórico de Ranking");
        System.out.println("4. Sair do Jogo");
        System.out.println("----------------------");
    }

    private static void jogarPartida(Scanner scanner, Random random, List<RankingEntry> ranking, RankingRepository repositorio) {
        String pilotoNome = lerLinha(scanner, "\nDigite o nome do piloto: ", "Piloto Anônimo");
        if (pilotoNome.isEmpty()) {
            pilotoNome = "Piloto Anônimo";
        }

        Dificuldade dificuldade = lerDificuldade(scanner);
        int tamanhoMapa = lerTamanhoMapa(scanner);

        int minX = -tamanhoMapa;
        int maxX = tamanhoMapa;
        int minY = -tamanhoMapa;
        int maxY = tamanhoMapa;

        System.out.println("\nIniciando missão na dificuldade " + dificuldade + "...");
        System.out.println("Pressione Enter para decolar!");
        scanner.nextLine();

        Missao missao = criarNovaMissao(random, minX, maxX, minY, maxY, dificuldade);
        Nave nave = missao.getNave();
        int score = definirPontuacaoInicial(dificuldade);
        int movimentos = 0;
        boolean partidaAtiva = true;

        long tempoInicio = System.currentTimeMillis();

        while (partidaAtiva) {
            MapaView.desenharMapa(missao, minX, maxX, minY, maxY, score, pilotoNome);
            System.out.printf("Nave em (%d,%d) | Pontos: %d | Vidas: %d | A bordo: %d/%d | Restantes: %d%n",
                    nave.getX(), nave.getY(), score, nave.getVidas(),
                    nave.getPassageiros().size(), nave.getCapacidade(),
                    missao.todosEmbarcados() ? 0 : missao.getPassageiros().size());

            String direcaoInput = lerLinha(scanner, "Comando (w/s/a/d/c/q): ", "").toLowerCase();
            if (direcaoInput.isEmpty()) continue;

            char cmd = direcaoInput.charAt(0);
            if (cmd == 'q') {
                System.out.println("Missão abortada pelo piloto.");
                partidaAtiva = false;
                break;
            } else if (cmd == 'c') {
                Passageiro p = missao.getPassageiroNaPosicaoDaNave();
                if (p == null) {
                    System.out.println("Nenhum passageiro nesta posição.");
                } else {
                    boolean embarcou = missao.embarcarPassageiroNaPosicao();
                    if (embarcou) {
                        int bonus = p.getPontuacao();
                        score += bonus;
                        System.out.printf("Passageiro %s embarcado com sucesso! +%d pontos!%n", p.getNome(), bonus);
                    } else {
                        System.out.println("Nave cheia! Não há espaço para mais passageiros.");
                    }
                }
            } else if (cmd == 'w' || cmd == 's' || cmd == 'a' || cmd == 'd') {
                nave.moverComLimites(cmd, minX, maxX);
                score--;
                movimentos++;
            } else {
                System.out.println("Comando inválido.");
                continue;
            }

            // Mover inimigos no mapa
            missao.moverInimigos(random, minX, maxX);

            // Verificar colisões
            if (missao.verificaColisao()) {
                nave.perderVida();
                if (nave.getVidas() > 0) {
                    System.out.printf("Alerta! Colisão detectada! Vidas restantes: %d%n", nave.getVidas());
                } else {
                    System.out.println("GAME OVER! A nave foi destruída.");
                    partidaAtiva = false;
                }
            }

            // Verificar pontuação zerada
            if (score <= 0) {
                System.out.println("Combustível/Pontuação zerada! Missão perdida.");
                partidaAtiva = false;
            }

            // Verificar vitória (todos resgatados E nave retornou à Plataforma de Pouso em (0,0))
            if (missao.todosEmbarcados() && partidaAtiva) {
                if (nave.getX() == 0 && nave.getY() == 0) {
                    long tempoFim = System.currentTimeMillis();
                    long tempoJogoSegundos = (tempoFim - tempoInicio) / 1000;

                    System.out.println("\n================================================================");
                    System.out.println("🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).");
                    System.out.println("Retornando à órbita marciana com todos os passageiros. Missão cumprida!");
                    System.out.println("================================================================");
                    exibirEstatisticas(score, movimentos, tempoJogoSegundos, nave.getPassageiros().size(), ranking);

                    if (score > 0 && isTopScore(ranking, score)) {
                        RankingEntry novaEntrada = new RankingEntry(
                                pilotoNome,
                                score,
                                dificuldade,
                                nave.getPassageiros().size(),
                                java.time.LocalDateTime.now().toString().substring(0, 19).replace('T', ' '),
                                tempoJogoSegundos
                        );
                        ranking.add(novaEntrada);
                        List<RankingEntry> rankingFiltrado = ranking.stream()
                                .sorted(Comparator.comparingInt((RankingEntry e) -> e.score).reversed())
                                .limit(5)
                                .collect(Collectors.toList());
                        repositorio.salvar(rankingFiltrado);
                        System.out.println("Parabéns! Você entrou para o Top 5 de pilotos!");
                    }
                    partidaAtiva = false;
                } else {
                    System.out.println("✨ ALERTA: Todos os passageiros resgatados! Retorne para a Plataforma de Pouso 'L' em (0,0) para completar a missão.");
                }
            }
        }
    }

    private static void exibirEstatisticas(int score, int movimentos, long tempoSegundos, int passageiros, List<RankingEntry> ranking) {
        System.out.println("Estatísticas da Partida:");
        System.out.printf(" - Pontuação Final: %d pontos%n", score);
        System.out.printf(" - Movimentos Efetuados: %d%n", movimentos);
        System.out.printf(" - Tempo de Jogo: %d segundos%n", tempoSegundos);
        System.out.printf(" - Passageiros Resgatados: %d%n", passageiros);

        int recorde = ranking.isEmpty() ? 0 : ranking.get(0).score;
        if (score > recorde && recorde > 0) {
            System.out.println("🏆 Novo recorde absoluto do sistema!");
        } else if (recorde > 0) {
            System.out.printf(" - Recorde atual a ser batido: %d pontos (Piloto: %s)%n", recorde, ranking.get(0).name);
        }
        System.out.println("================================================================");
    }

    private static Dificuldade lerDificuldade(Scanner scanner) {
        System.out.print("Escolha a Dificuldade (facil/medio/dificil): ");
        String difStr = lerLinha(scanner, "", "medio");
        return Dificuldade.deString(difStr);
    }

    private static int lerTamanhoMapa(Scanner scanner) {
        try {
            int tamanho = Integer.parseInt(lerLinha(scanner, "Tamanho do mapa (ex: 5 para mapa de -5 a +5): ", "5"));
            return tamanho > 0 ? tamanho : 5;
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida, usando tamanho padrão (5).");
            return 5;
        }
    }

    private static int definirPontuacaoInicial(Dificuldade dificuldade) {
        switch (dificuldade) {
            case FACIL: return 30;
            case DIFICIL: return 15;
            default: return 20;
        }
    }

    private static Missao criarNovaMissao(Random random, int minX, int maxX, int minY, int maxY, Dificuldade dificuldade) {
        Nave nave = new Nave("A-1", 0, 0, 5);
        Missao missao = new Missao(nave);

        int qtdPassageiros = 5;
        int qtdAsteroides = 2;
        int qtdInimigos = 2;

        if (dificuldade == Dificuldade.FACIL) {
            qtdPassageiros = 4;
            qtdAsteroides = 1;
            qtdInimigos = 1;
        } else if (dificuldade == Dificuldade.DIFICIL) {
            qtdPassageiros = 5;
            qtdAsteroides = 3;
            qtdInimigos = 3;
        }

        // Adicionar passageiros aleatórios
        while (missao.getPassageiros().size() < qtdPassageiros) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            if (x == nave.getX() && y == nave.getY()) continue;
            if (posicaoOcupada(missao, x, y)) continue;

            int index = missao.getPassageiros().size();
            missao.adicionarPassageiro(PassageiroFactory.criar(index, x, y));
        }

        // Adicionar asteroides aleatórios
        while (missao.getAsteroides().size() < qtdAsteroides) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            if (x == nave.getX() && y == nave.getY()) continue;
            if (posicaoOcupada(missao, x, y)) continue;

            missao.adicionarAsteroide(new Asteroide(x, y));
        }

        // Adicionar inimigos aleatórios
        while (missao.getInimigos().size() < qtdInimigos) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            if (x == nave.getX() && y == nave.getY()) continue;
            if (posicaoOcupada(missao, x, y)) continue;

            missao.adicionarInimigo(new Inimigo(x, y));
        }

        return missao;
    }

    private static boolean posicaoOcupada(Missao missao, int x, int y) {
        List<Posicionavel> entidades = new ArrayList<>();
        entidades.add(missao.getNave());
        entidades.addAll(missao.getPassageiros());
        entidades.addAll(missao.getAsteroides());
        entidades.addAll(missao.getInimigos());

        for (Posicionavel entidade : entidades) {
            if (entidade.getX() == x && entidade.getY() == y) {
                return true;
            }
        }
        return false;
    }


    private static String lerLinha(Scanner scanner, String prompt, String fallback) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return fallback;
    }

    private static void exibirRankingCompleto(List<RankingEntry> ranking) {
        System.out.println("\n====== RANKING TOP 5 PILOTOS ======");
        if (ranking.isEmpty()) {
            System.out.println(" - Nenhum registro encontrado. Seja o primeiro a jogar!");
        } else {
            int pos = 1;
            for (RankingEntry entry : ranking) {
                System.out.printf("%d. %s - %d pts | Dificuldade: %s | Coletados: %d | Tempo: %ds | %s%n",
                        pos++, entry.name, entry.score, entry.dificuldade, entry.passageirosColetados, entry.tempoJogo, entry.dataHora);
            }
        }
        System.out.println("===================================");
    }


    private static List<RankingEntry> resetarRanking(Scanner scanner, RankingRepository repositorio) {
        System.out.print("Você realmente deseja limpar o histórico de ranking? (s/n): ");
        String confirmacao = lerLinha(scanner, "", "n").toLowerCase();
        if (confirmacao.equals("s") || confirmacao.equals("sim")) {
            repositorio.resetar();
            System.out.println("Ranking resetado com sucesso!");
            return new ArrayList<>();
        }
        System.out.println("Operação cancelada.");
        return repositorio.carregar();
    }
    // -------------------------------------------------

    private static boolean isTopScore(List<RankingEntry> ranking, int score) {
        if (ranking.size() < 5) {
            return true;
        }
        return score > ranking.get(ranking.size() - 1).score;
    }
}
