# Missão Marte Unifor — Refatoração SOLID

## Integrantes

- Isabella Gaspar Anton — 2516267
- Anderson Herculano de Lima — 2516855

## Sobre o projeto

Jogo de console em Java no qual o piloto conduz uma nave por Marte, resgata passageiros, evita perigos e tenta retornar à base. Esta versão reorganiza o jogo em responsabilidades menores, preservando o fluxo principal da missão.

## Requisitos

- JDK instalado (Java 8 ou superior).
- PowerShell ou terminal compatível com os comandos abaixo.

## Compilar e executar

Abra o terminal na pasta raiz do repositório e compile os fontes:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java -Path src | ForEach-Object FullName)
```

Inicie o jogo:

```powershell
java -cp out solidexercicio10.Main
```

O jogo oferece opções para iniciar uma missão, consultar o ranking, resetá-lo e sair. Na missão, informe o nome do piloto, a dificuldade e o tamanho do mapa. Use `W`, `A`, `S` e `D` para mover a nave, `C` para embarcar um passageiro e `Q` para encerrar a missão.

As pontuações de missões concluídas são persistidas no arquivo `ranking-solid-exercicio10.txt`, criado na pasta em que o jogo é executado.

## Alterações realizadas

- Separação do domínio em `src/solidexercicio10/model`.
- Regras e fluxo do menu organizados em `src/solidexercicio10/service/JogoService.java`.
- Renderização do mapa isolada em `src/solidexercicio10/presentation/MapaRenderer.java`.
- Ranking separado em um contrato e uma implementação de arquivo, no pacote `repository`.
- Inclusão dos diagramas UML e desta revisão da solução.
- Remoção de um arquivo duplicado que impedia a compilação do pacote refatorado.

## Decisões de projeto

- `Main` monta as dependências e escolhe `RankingArquivoRepository`.
- `JogoService` depende da abstração `RankingRepository`; assim, a implementação de persistência pode ser trocada sem colocar operações de arquivo nas regras do jogo.
- A apresentação do mapa fica em `MapaRenderer`, separada das regras da missão.
- `Professor`, `Engenheiro` e `Astronauta` herdam de `Passageiro` e definem sua própria pontuação e símbolo. O fluxo trabalha com a classe base.
- `Posicionavel` e `Movel` são interfaces pequenas para capacidades diferentes.
- Não foi criada uma interface para o renderer porque há apenas uma apresentação e não existe uma segunda implementação que justifique essa abstração.

## Diagramas UML

Os arquivos-fonte PlantUML e as imagens estão em [`docs/uml`](docs/uml/):

- [Diagrama de classes do domínio (PNG)](docs/uml/diagrama-classes-model.png) e [fonte PlantUML](docs/uml/diagrama-classes-model.puml): apresenta as entidades, interfaces, heranças e associações de `model`.
- [Diagrama de pacotes (PNG)](docs/uml/diagrama-pacotes.png) e [fonte PlantUML](docs/uml/diagrama-pacotes.puml): apresenta as camadas e mostra o serviço dependendo do contrato `RankingRepository`.

## Testes e evidências

Na revisão final, os fontes de `solidexercicio10` compilaram com JDK 24. Também foram verificados os seguintes fluxos:

- início e conclusão de uma missão com embarque de passageiros, retorno à base, estatísticas e gravação no ranking;
- consulta e reset do ranking pelo menu;
- gravação, leitura e limpeza do repositório em arquivo temporário;
- embarque no modelo e detecção de colisão com asteroide.

Os resultados e as observações por princípio estão em [`REVISAO-SOLID.md`](REVISAO-SOLID.md).

## Limitações conhecidas

- A interface é de console e o ranking usa um arquivo texto simples.
- `JogoService` ainda coordena menu, turno e criação/posicionamento das entidades; a geração da missão pode ser extraída se houver novas variações.
- O movimento dos inimigos respeita os limites do mapa, mas ainda pode fazer inimigos se sobreporem a outras entidades.
- O pacote original `src/exercicio10` não estava no material local recebido. Portanto, a versão inicial não foi preservada nem comparada nesta cópia; ela deve ser adicionada sem alterações para completar essa parte da atividade.
