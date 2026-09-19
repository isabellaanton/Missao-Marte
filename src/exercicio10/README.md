# Revisão Crítica - Missão Marte (Princípios SOLID)

### Princípio: Single Responsibility Principle (SRP)
* **Local:** `exercicio10.presentation.MapaView` e `exercicio10.Main`
* **Observação:** O código original misturava a lógica de controle da partida com a renderização visual do mapa no console.
* **Impacto:** Dificultava a alteração do visual do jogo (ex: adicionar cores ANSI) sem correr o risco de quebrar o loop do jogo.
* **Proposta:** Isolamento da renderização estritamente na classe `MapaView`, deixando o `Main` responsável apenas pelo fluxo do menu e orquestração.
* **Prioridade:** Alta

### Princípio: Open/Closed Principle (OCP)
* **Local:** `exercicio10.service.PassageiroFactory`
* **Observação:** A instanciação direta de passageiros no construtor da Missão exigia modificação na classe principal toda vez que um novo tipo de passageiro fosse criado.
* **Impacto:** Risco de regressão na regra de negócios central ao adicionar novos elementos ao jogo.
* **Proposta:** Criação de uma Factory. Agora, o código está aberto para extensão (novas classes filhas de Passageiro) mas fechado para modificação no loop do jogo.
* **Prioridade:** Média

### Princípio: Liskov Substitution Principle (LSP)
* **Local:** `exercicio10.model.Passageiro` e suas subclasses (`Professor`, `Engenheiro`, `Astronauta`)
* **Observação:** As subclasses substituem perfeitamente a classe base `Passageiro` dentro das coleções e lógicas da `Missao`.
* **Impacto:** Garante que o método `embarcar()` da Nave funcione de forma polimórfica, sem precisar checar o tipo específico do passageiro com `instanceof` para a ação básica de resgate.
* **Proposta:** Manter o contrato estrito, garantindo que toda nova profissão implemente `getPontuacao()` corretamente.
* **Prioridade:** Alta

### Princípio: Interface Segregation Principle (ISP)
* **Local:** `exercicio10.model.Movel` e `exercicio10.model.Posicionavel`
* **Observação:** Originalmente, entidades estáticas (como Asteroides) poderiam ser forçadas a herdar métodos de movimentação.
* **Impacto:** Classes implementando métodos vazios ou lançando exceções não suportadas, sujando o design.
* **Proposta:** Segregação rigorosa. `Posicionavel` apenas exige X e Y. `Movel` exige `mover()`. Asteroides são apenas Posicionáveis, enquanto a Nave e Inimigos são Móveis.
* **Prioridade:** Alta

### Princípio: Dependency Inversion Principle (DIP)
* **Local:** `exercicio10.Main` e `exercicio10.repository.RankingRepository`
* **Observação:** O fluxo principal dependia diretamente da implementação concreta de salvamento em arquivo.
* **Impacto:** Impossibilidade de trocar o sistema de salvamento (ex: para um Banco de Dados SQL) sem reescrever a classe `Main`.
* **Proposta:** Injeção da interface `RankingRepository` no `Main`. O repositório concreto `RankingJsonRepository` é instanciado apenas uma vez e passado via parâmetro.
* **Prioridade:** Alta

---

### Melhoria Adicional
* **Local:** `exercicio10.presentation.MapaView`
* **Observação:** A interface do console em texto puro branco prejudica a UX (User Experience) e dificulta a rápida identificação de ameaças (Inimigos).
* **Impacto:** Navegação confusa durante partidas em mapas grandes.
* **Proposta:** Injeção de códigos de escape ANSI na renderização do terminal, mapeando cores específicas: Verde (Passageiros), Vermelho (Inimigos), Amarelo (Asteroides) e Azul (Nave).
* **Prioridade:** Média

---

### Decisão do Tutorial: Concordância
* **Observação:** Concordo plenamente com a criação da camada `presentation` separada do `model`.
* **Benefício:** A separação permite que o motor do jogo (`model` e `service`) rode de forma "headless" (sem interface gráfica). Isso possibilita rodar milhares de simulações para testes automatizados ou treinar uma IA sem o gargalo de imprimir texto no console a cada frame.

### Decisão do Tutorial: Discordância Técnica
* **Observação:** Discordo da instrução de "criar uma versão refatorada em um pacote separado (`solidexercicio10`) mantendo o código inicial na mesma codebase".
* **Justificativa:** Em um ambiente corporativo real, manter pacotes legados e pacotes refatorados convivendo no mesmo repositório gera poluição de namespace, confusão de importações na IDE (como presenciamos durante o desenvolvimento) e fere o princípio DRY (Don't Repeat Yourself). A abordagem profissional é refatorar o próprio pacote `exercicio10` e confiar no histórico do Git (commits antigos) para comparação de "Antes e Depois".

---

### Testes Realizados
1. **Teste de Colisão:** Movimentação intencional da nave contra um asteroide. **Resultado:** Vida deduzida corretamente; jogo encerrado após 3 vidas perdidas.
2. **Teste de Embarque (Limite):** Tentativa de embarcar 6 passageiros em uma nave com capacidade 5. **Resultado:** Rejeição do embarque excedente operando conforme o esperado.
3. **Teste de Persistência DIP:** Salvamento do recorde, encerramento do console e reinício da aplicação. **Resultado:** Top 5 pilotos carregados corretamente da memória JSON persistida via interface.