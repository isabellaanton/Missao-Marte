package solidexercicio10.service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import solidexercicio10.model.Asteroide;
import solidexercicio10.model.Astronauta;
import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Engenheiro;
import solidexercicio10.model.Inimigo;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Nave;
import solidexercicio10.model.Passageiro;
import solidexercicio10.model.Professor;
import solidexercicio10.presentation.MapaRenderer;
import solidexercicio10.repository.RankingEntry;
import solidexercicio10.repository.RankingRepository;

/** Orquestra os casos de uso; não conhece detalhes de arquivo do ranking. */
public final class JogoService {
    private final RankingRepository ranking;
    private final MapaRenderer renderer;
    private final Random random;

    public JogoService(RankingRepository ranking) {
        this(ranking, new MapaRenderer(), new Random());
    }
    JogoService(RankingRepository ranking, MapaRenderer renderer, Random random) {
        this.ranking = ranking;
        this.renderer = renderer;
        this.random = random;
    }

    public void executarMenu(Scanner scanner) {
        boolean ativo = true;
        while (ativo) {
            exibirMenu();
            String opcao = ler(scanner, "Escolha uma opção: ", "4");
            switch (opcao) {
                case "1": jogar(scanner); break;
                case "2": exibirRanking(); break;
                case "3": resetarRanking(); break;
                case "4": ativo = false; System.out.println("Até a próxima missão!"); break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Iniciar Nova Missão");
        System.out.println("2. Visualizar Ranking Top 5");
        System.out.println("3. Resetar Histórico de Ranking");
        System.out.println("4. Sair do Jogo");
        System.out.println("----------------------");
    }

    private void jogar(Scanner scanner) {
        String piloto = ler(scanner, "Nome do piloto: ", "Piloto");
        Dificuldade dificuldade = Dificuldade.deString(ler(scanner, "Dificuldade (facil, medio, dificil): ", "medio"));
        int tamanho = lerTamanho(scanner);
        Missao missao = criarNovaMissao(dificuldade, tamanho);
        int pontos = dificuldade.getPontosIniciais();
        int movimentos = 0;
        long inicio = System.currentTimeMillis();
        System.out.println("Missão iniciada. Recolha todos os passageiros e retorne à base (0,0).");

        while (true) {
            renderer.desenhar(missao, pontos, piloto, -tamanho, tamanho);
            System.out.printf("A bordo: %d/%d | Restantes: %d%n", missao.getNave().getPassageiros().size(),
                    missao.getNave().getCapacidade(), missao.getPassageiros().size());
            char comando = lerComando(scanner);
            if (comando == 'q') {
                exibirEstatisticas(false, piloto, pontos, movimentos, inicio, dificuldade, missao.getNave());
                return;
            }
            if (comando == 'c') {
                Passageiro passageiro = missao.getPassageiroNaPosicaoDaNave();
                if (passageiro != null && missao.embarcarPassageiroNaPosicao()) {
                    pontos += passageiro.getPontuacao();
                    System.out.println(passageiro.getTipo() + " embarcado: +" + passageiro.getPontuacao() + " pontos.");
                } else System.out.println("Não há passageiro para embarcar nesta posição.");
            } else if ("wasd".indexOf(comando) >= 0) {
                if (missao.getNave().moverComLimites(comando, -tamanho, tamanho)) {
                    movimentos++;
                    pontos--;
                } else System.out.println("Limite do mapa alcançado.");
            } else System.out.println("Comando inválido.");

            missao.moverInimigos(random, -tamanho, tamanho);
            if (missao.verificaColisao()) {
                missao.getNave().perderVida();
                System.out.println("Colisão detectada! Uma vida foi perdida.");
            }
            if (pontos <= 0 || missao.getNave().getVidas() == 0) {
                exibirEstatisticas(false, piloto, pontos, movimentos, inicio, dificuldade, missao.getNave());
                return;
            }
            if (missao.todosEmbarcados() && missao.getNave().getX() == 0 && missao.getNave().getY() == 0) {
                exibirEstatisticas(true, piloto, pontos, movimentos, inicio, dificuldade, missao.getNave());
                return;
            }
        }
    }

    private Missao criarNovaMissao(Dificuldade dificuldade, int tamanho) {
        Nave nave = new Nave("Unifor-1", 0, 0, dificuldade.getPassageiros());
        Missao missao = new Missao(nave);
        Set<String> ocupadas = new HashSet<String>();
        ocupadas.add(chave(0, 0));
        for (int i = 0; i < dificuldade.getPassageiros(); i++) {
            int[] posicao = posicaoLivre(tamanho, ocupadas);
            if (i % 3 == 0) missao.adicionarPassageiro(new Professor("Professor " + (i + 1), posicao[0], posicao[1]));
            else if (i % 3 == 1) missao.adicionarPassageiro(new Engenheiro("Engenheiro " + (i + 1), posicao[0], posicao[1]));
            else missao.adicionarPassageiro(new Astronauta("Astronauta " + (i + 1), posicao[0], posicao[1]));
        }
        for (int i = 0; i < dificuldade.getAsteroides(); i++) {
            int[] posicao = posicaoLivre(tamanho, ocupadas);
            missao.adicionarAsteroide(new Asteroide(posicao[0], posicao[1]));
        }
        for (int i = 0; i < dificuldade.getInimigos(); i++) {
            int[] posicao = posicaoLivre(tamanho, ocupadas);
            missao.adicionarInimigo(new Inimigo(posicao[0], posicao[1]));
        }
        return missao;
    }

    private int[] posicaoLivre(int tamanho, Set<String> ocupadas) {
        int x;
        int y;
        do {
            x = random.nextInt(tamanho * 2 + 1) - tamanho;
            y = random.nextInt(tamanho * 2 + 1) - tamanho;
        } while (!ocupadas.add(chave(x, y)));
        return new int[] {x, y};
    }
    private String chave(int x, int y) { return x + ":" + y; }

    private void exibirEstatisticas(boolean concluida, String piloto, int pontos, int movimentos, long inicio,
                                     Dificuldade dificuldade, Nave nave) {
        long tempo = (System.currentTimeMillis() - inicio) / 1000;
        boolean recorde = ranking.listar().isEmpty() || pontos > ranking.listar().get(0).getPontos();
        System.out.println("\n--- FIM DA MISSÃO ---");
        System.out.println(concluida ? "Missão concluída com sucesso!" : "Missão encerrada.");
        System.out.printf("Pontuação final: %d%nMovimentos: %d%nTempo total: %d segundos%n", pontos, movimentos, tempo);
        if (concluida) {
            ranking.salvar(piloto, pontos, dificuldade, nave.getPassageiros().size(), tempo);
            if (recorde) System.out.println("Novo recorde absoluto!");
        }
    }

    private void exibirRanking() {
        List<RankingEntry> entradas = ranking.listar();
        System.out.println("\n--- RANKING TOP 5 ---");
        if (entradas.isEmpty()) { System.out.println("Ainda não há missões concluídas."); return; }
        for (int i = 0; i < Math.min(5, entradas.size()); i++) {
            RankingEntry entrada = entradas.get(i);
            System.out.printf("%d. %s - %d pontos | %s | %d passageiro(s) | %ds%n", i + 1,
                    entrada.getNome(), entrada.getPontos(), entrada.getDificuldade(),
                    entrada.getPassageiros(), entrada.getTempoSegundos());
        }
    }
    private void resetarRanking() { ranking.limpar(); System.out.println("Histórico de ranking resetado."); }
    private int lerTamanho(Scanner scanner) {
        try { return Math.max(2, Integer.parseInt(ler(scanner, "Tamanho do mapa (mínimo 2): ", "5"))); }
        catch (NumberFormatException e) { System.out.println("Tamanho inválido; usando 5."); return 5; }
    }
    private char lerComando(Scanner scanner) { return ler(scanner, "Comando: ", "q").toLowerCase().charAt(0); }
    private String ler(Scanner scanner, String mensagem, String padrao) {
        System.out.print(mensagem);
        if (!scanner.hasNextLine()) return padrao;
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? padrao : texto;
    }
}
