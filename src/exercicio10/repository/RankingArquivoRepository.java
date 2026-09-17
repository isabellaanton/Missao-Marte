package solidexercicio10.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import solidexercicio10.model.Dificuldade;

/** Persistência simples em arquivo texto; o serviço depende apenas do contrato. */
public final class RankingArquivoRepository implements RankingRepository {
    private final Path arquivo;

    public RankingArquivoRepository(Path arquivo) { this.arquivo = arquivo; }

    @Override
    public void salvar(String nome, int pontos, Dificuldade dificuldade, int passageiros, long tempoSegundos) {
        List<String> linhas = new ArrayList<String>();
        if (Files.exists(arquivo)) {
            try { linhas.addAll(Files.readAllLines(arquivo, StandardCharsets.UTF_8)); }
            catch (IOException e) { throw new IllegalStateException("Não foi possível ler o ranking.", e); }
        }
        String nomeSeguro = nome.replace("|", " ").trim();
        linhas.add(nomeSeguro + "|" + pontos + "|" + dificuldade.name() + "|" + passageiros + "|" + tempoSegundos);
        try {
            Path pasta = arquivo.getParent();
            if (pasta != null) Files.createDirectories(pasta);
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível salvar o ranking.", e);
        }
    }

    @Override
    public List<RankingEntry> listar() {
        List<RankingEntry> entradas = new ArrayList<RankingEntry>();
        if (!Files.exists(arquivo)) return entradas;
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                String[] partes = linha.split("\\|", -1);
                if (partes.length != 5) continue;
                try {
                    entradas.add(new RankingEntry(partes[0], Integer.parseInt(partes[1]),
                            Dificuldade.deString(partes[2]), Integer.parseInt(partes[3]), Long.parseLong(partes[4])));
                } catch (NumberFormatException ignored) { }
            }
        } catch (IOException e) { throw new IllegalStateException("Não foi possível ler o ranking.", e); }
        entradas.sort(Comparator.comparingInt(RankingEntry::getPontos).reversed());
        return entradas;
    }

    @Override
    public void limpar() {
        try { Files.deleteIfExists(arquivo); }
        catch (IOException e) { throw new IllegalStateException("Não foi possível resetar o ranking.", e); }
    }
}
