# Ponto Facial

Sistema de ponto que reconhece o funcionário comparando a foto tirada na hora com as fotos cadastradas, e registra a hora de quem foi reconhecido. Tem cadastro de funcionários, registros de ponto, avisos e relatórios.

<p align="center">
  <img src="https://raw.githubusercontent.com/guavovic/ponto-facial/main/docs/assets/ponto-facial.gif" alt="Tela do Ponto Facial reconhecendo dois funcionários e recusando um rosto que não está cadastrado" width="560"><br>
  <sub>Rostos de exemplo: avatares do <a href="https://www.dicebear.com">DiceBear</a> (estilo Lorelei), licença <a href="https://creativecommons.org/publicdomain/zero/1.0/">CC0</a>.</sub>
</p>

## Como foi feito

Nasceu como trabalho de faculdade, em 2022, e foi retomado e finalizado em 2026. A primeira versão só rodava na máquina do autor, com caminhos fixos no código e vários botões sem função. A segunda foi refeita em etapas, cada uma com a decisão registrada num ADR.

- **Dados portáteis:** funcionários e pontos ficam num banco SQLite numa pasta ao lado do programa, e as fotos viram arquivos de imagem. O truque do IP e dos arquivos de texto, que só funcionava no Windows e dentro da IDE, saiu.
- **Comparação medida:** a nota somava um histograma só do canal vermelho, que podia dar negativa, e registrava o ponto uma vez para cada foto parecida. Medindo com fotos de exemplo, o histograma era dominado pela cor do fundo e confundia as pessoas, então a nota passou a ser a semelhança pixel a pixel, com o limite ajustado por esses dados. O ponto vai uma vez só, para a pessoa mais parecida.
- **Câmera:** o ponto é batido com a foto tirada na hora, com uma prévia e um oval para enquadrar o rosto. A escolha de um arquivo continua existindo.
- **Telas que funcionam:** cadastro, edição e remoção de funcionário, gerenciamento de pontos, avisos de foto não reconhecida e relatórios em planilha.
- **Visual refeito:** layouts que se ajustam no lugar de posições fixas, tema claro, tabelas ordenáveis e as fontes Inter e JetBrains Mono.
- **Testes sem mock:** 87 testes que usam o banco de verdade numa pasta temporária. As fotos de exemplo protegem o limite da comparação, e quebras propositais no código confirmaram que os testes pegam o erro.
- **Entrega:** build e testes a cada mudança no GitHub Actions, e o `.jar` publicado na release a cada versão.

## Tecnologias

- **Aplicação:** Java 25, Swing com FlatLaf (fontes Inter e JetBrains Mono).
- **Dados:** SQLite via JDBC.
- **Câmera:** OpenCV, no Windows.
- **Testes:** JUnit.
- **Entrega:** Maven e GitHub Actions.

## Documentação

- [Decisões de arquitetura](docs/decisions): o porquê de cada escolha, com as alternativas consideradas.
