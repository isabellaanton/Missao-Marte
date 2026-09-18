package exercicio10.repository;

import exercicio10.model.RankingEntry;
import java.util.List;

public interface RankingRepository {
    List<RankingEntry> carregar();
    void salvar(List<RankingEntry> ranking);
    void resetar();
}