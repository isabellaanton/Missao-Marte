# Revisão crítica da refatoração SOLID

As observações abaixo descrevem a versão em `src/solidexercicio10`. As melhorias propostas são oportunidades de evolução; a prioridade indica o custo de deixá-las para uma próxima versão.

## SRP

Local: `JogoService.executarMenu` e `JogoService.jogar`  
Princípio relacionado: SRP  
Observação: o desenho do mapa e a persistência estão separados, mas `JogoService` ainda concentra entrada do menu, criação/posicionamento de entidades e coordenação do turno.  
Impacto para manutenção, testes ou evolução: alterações na interface de console e nas regras de geração podem exigir mudanças na mesma classe.  
Proposta: extrair a geração da missão para uma classe própria se surgirem variações de mapa ou de regras.  
Prioridade: média.

## OCP

Local: `JogoService.criarNovaMissao`  
Princípio relacionado: OCP  
Observação: o restante do fluxo trata os personagens como `Passageiro`, porém a seleção cíclica dos tipos está codificada em condicionais neste método. Um novo tipo exige editar esse trecho.  
Impacto para manutenção, testes ou evolução: a extensão polimórfica é simples, mas a composição dos tipos ainda não é aberta para registro.  
Proposta: receber uma fábrica ou lista configurável de criadores de passageiros ao adicionar novos tipos.  
Prioridade: baixa.

## LSP

Local: `Passageiro`, `Professor`, `Engenheiro` e `Astronauta`  
Princípio relacionado: LSP  
Observação: as subclasses fornecem pontuação e símbolo sem alterar o contrato esperado para embarque, apresentação ou pontuação.  
Impacto para manutenção, testes ou evolução: qualquer passageiro pode ocupar a lista de `Passageiro` e ser processado pelo mesmo fluxo.  
Proposta: manter o contrato pequeno e acrescentar verificações automatizadas quando houver infraestrutura de testes no projeto.  
Prioridade: baixa.

## ISP

Local: `Posicionavel` e `Movel`  
Princípio relacionado: ISP  
Observação: as capacidades de ocupar coordenadas e de se mover têm contratos pequenos; asteroides não precisam implementar movimento.  
Impacto para manutenção, testes ou evolução: classes implementam somente as operações que usam.  
Proposta: manter as capacidades separadas caso novas entidades sejam introduzidas.  
Prioridade: baixa.

## DIP

Local: construtor de `JogoService` e `RankingRepository`  
Princípio relacionado: DIP  
Observação: o serviço recebe o contrato do repositório, enquanto `Main` escolhe `RankingArquivoRepository`.  
Impacto para manutenção, testes ou evolução: o formato de arquivo pode ser trocado sem colocar operações de arquivo dentro das regras do jogo.  
Proposta: criar um repositório em memória se o projeto ganhar testes automatizados para o serviço.  
Prioridade: média.

## Decisões e melhoria adicional

Local: `RankingRepository` e `RankingArquivoRepository`  
Princípio relacionado: DIP  
Observação: concordo com a interface pequena para o ranking, pois arquivo é um detalhe substituível e o contrato tem um consumidor claro. Não criaria uma interface para `MapaRenderer` agora: há somente uma apresentação de console, e uma segunda abstração não resolveria uma variação existente.  
Impacto para manutenção, testes ou evolução: o ponto variável de persistência é isolado sem espalhar interfaces pela aplicação.  
Proposta: melhoria adicional: impedir que inimigos se sobreponham a passageiros, asteroides ou entre si depois de se moverem; atualmente somente os limites do mapa são verificados.  
Prioridade: média.

## Testes realizados

| Teste | Resultado esperado | Resultado |
|---|---|---|
| Compilação do pacote refatorado | Sem erros | Aprovado com `javac` (JDK 24). |
| Menu: ranking, reset e saída | Listar ranking, limpar dados e encerrar | Aprovado em execução automatizada por entrada de console. |
| Missão: embarque e vitória | Resgatar passageiros, retornar à base, mostrar estatísticas e gravar pontuação | Aprovado com missão determinística de dificuldade fácil. |
| Ranking: persistir e consultar | Salvar e reabrir entrada com pontuação e metadados | Aprovado com arquivo temporário. |
| Reset do ranking | Remover os registros e retornar lista vazia | Aprovado pelo menu e pelo repositório. |
| Regras de domínio | Embarcar passageiro e detectar colisão com asteroide | Aprovado com verificações diretas no modelo. |

## Limitação do material de origem

O diretório recebido continha a versão refatorada e arquivos `.class`, mas não continha `src/exercicio10`. O código original foi solicitado pela atividade, porém não foi possível baixá-lo do GitHub neste ambiente. Portanto, esta entrega não afirma que preservou ou executou a versão inicial; o pacote original precisa ser adicionado antes da comparação final.
