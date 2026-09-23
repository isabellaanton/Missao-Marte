# Missão Marte Unifor — Refatoração SOLID

## Integrantes

- Isabella Gaspar Anton — 2516267
- Anderson Herculano de Lima — 2516855

## Sobre o projeto

Jogo de console em Java no qual a pessoa pilota uma nave em Marte, resgata passageiros e tenta retornar à base. A versão refatorada está em `src/exercicio10`.

## Compilação e execução

Requer JDK instalado. A partir da raiz do repositório, compile os fontes do exercício:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java -Path src/exercicio10 | ForEach-Object FullName)
```

Depois, execute a classe principal:

```powershell
java -cp out solidexercicio10.Main
```

No menu, é possível iniciar uma missão, consultar ou resetar o ranking e sair. Durante a missão, `W`, `A`, `S` e `D` movimentam a nave, `C` tenta embarcar passageiros e `Q` encerra a partida.

> **Estado da versão publicada:** os comandos acima representam a forma prevista de compilação e execução. A árvore atual precisa ter as declarações de pacote e as classes referenciadas por `Main.java` alinhadas para que a compilação seja concluída; veja “Limitações”.

## Alterações e decisões de projeto

- Separação do código em `model`, `presentation` e `repository`, reduzindo a concentração de responsabilidades.
- Modelagem de elementos do jogo no domínio, com tipos próprios para nave, missão, passageiros, obstáculos e dificuldade.
- Uso de contratos para representar capacidades e operações, como posicionamento, movimento e acesso ao ranking.
- Herança e polimorfismo para representar variações de passageiros e seus comportamentos.
- A persistência do ranking é tratada por uma abstração de repositório, permitindo substituir sua implementação sem acoplar as regras do jogo ao formato de armazenamento.

## Diagramas UML

Os diagramas estão em [`../docs/uml/`](../docs/uml/), na pasta `src/docs/uml`:

- [`diagrama-classes-model.png`](../docs/uml/diagrama-classes-model.png) e [`diagrama-classes-model.puml`](../docs/uml/diagrama-classes-model.puml): mostram classes do domínio, atributos, operações, heranças e relações entre os elementos do modelo.
- [`diagrama-pacotes.png`](../docs/uml/diagrama-pacotes.png) e [`diagrama-pacotes.puml`](../docs/uml/diagrama-pacotes.puml): mostram a organização dos pacotes e suas dependências.

## Testes e evidências

As observações da revisão estão em [`REVISAO-SOLID.md`](REVISAO-SOLID.md). A compilação e a execução automatizada da versão publicada devem ser repetidas depois de corrigidas as inconsistências descritas abaixo; este README não afirma que esses testes passaram no estado atual.

## Limitações

- A interface é executada no console.
- No estado publicado, `Main.java` referencia `solidexercicio10.service.JogoService` e classes do pacote `solidexercicio10.repository`, mas a árvore apresentada não contém a pasta `service` nem todas as implementações referenciadas.
- Há incompatibilidade entre o pacote declarado em `Main.java` (`solidexercicio10`) e declarações encontradas em outros arquivos (`exercicio10`). É necessário alinhar os pacotes e completar as classes referenciadas antes de compilar e executar a versão publicada.
