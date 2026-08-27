# Missão Marte Unifor

## Equipe
- **Anderson** (Matrícula: 2516855)
- **Isabella** (Matrícula: 2516267)

## Repositório
[Insira o Link para o repositório Git da Equipe aqui]

## Sobre o Projeto
Este projeto é uma extensão do jogo em console "Missão Marte Unifor", desenvolvido para a disciplina de Projeto e Arquitetura de Sistemas da UNIFOR. O objetivo é aplicar conceitos avançados de Programação Orientada a Objetos (POO), tais como Herança, Polimorfismo, Encapsulamento, Composição e a persistência de dados em arquivos JSON.

## Escopo Desenvolvido
- **Nível 1**: Configuração de capacidade da nave, nova classe `Astronauta` herdando de `Passageiro`, e nova renderização visual do mapa.
- **Nível 2**: Polimorfismo aplicado às pontuações de resgate (`Professor`, `Engenheiro`, `Astronauta`), sistema de vidas da nave com detecção de colisão, e redimensionamento dinâmico do mapa.
- **Nível 3**: Implementação da classe `Inimigo` com inteligência de movimentação aleatória, menu através da Enum `Dificuldade`, e detalhamento no registro do arquivo `ranking.json`.
- **Nível 4**: Inclusão de menu interativo inicial e condição de vitória estrita (retorno à plataforma de pouso na coordenada 0,0 com todos resgatados).

## Como Compilar e Executar

1. Abra o terminal de sua preferência.
2. Navegue até a pasta raiz do projeto clonado.
3. Compile os arquivos Java (garanta que a pasta `bin/` existe ou deixe o `javac` criá-la se suportado, senão compile na raiz ou adapte o diretório):
   ```bash
   javac -d . src/missao/*.java
   ```
4. Execute o jogo a partir da classe Main:
   ```bash
   java missao.Main
   ```

*Nota: Certifique-se de configurar corretamente seu `.gitignore` para não incluir os binários/ `.class` no repositório final.*
