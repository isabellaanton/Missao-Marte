package solidexercicio10;

import java.nio.file.Paths;
import java.util.Scanner;
import solidexercicio10.repository.RankingArquivoRepository;
import solidexercicio10.repository.RankingRepository;
import solidexercicio10.service.JogoService;

public final class Main {
    private Main() { }
    public static void main(String[] args) {
        RankingRepository ranking = new RankingArquivoRepository(Paths.get("ranking-solid-exercicio10.txt"));
        JogoService jogo = new JogoService(ranking);
        try (Scanner scanner = new Scanner(System.in)) { jogo.executarMenu(scanner); }
    }
}
