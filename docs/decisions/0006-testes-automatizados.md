# 6. Testes automatizados

Data: 01/10/2026

Status: Aceito

## Contexto

O projeto não tinha nenhum teste. Até aqui, cada mudança foi conferida à mão ou com programas soltos que não ficaram no repositório. A comparação de imagens, em especial, tem um limite (0,91) que só vale enquanto as fotos de exemplo continuarem sendo reconhecidas direito.

## Decisão

- **JUnit Jupiter 6.1.3**, a linha atual do JUnit (a API `org.junit.jupiter` é a mesma da 5). Nenhuma outra biblioteca de teste.
- **Sem mock.** O banco é SQLite, que cria um arquivo numa pasta temporária (`@TempDir`) em milésimos, então os testes usam o banco de verdade. Isso pega erro de SQL, de migração e de chave estrangeira que um mock esconderia. O relógio é fixo (`Clock.fixed`), para a hora do ponto ser previsível.
- **O que é testado:**
  - Comparação: o histograma (três cores, normalizado), as distâncias, as notas (entre 0 e 1, simétricas, iguais para a mesma imagem em tamanhos diferentes) e os casos que justificaram o ADR 0004, como "as mesmas cores em outro lugar enganam o histograma, mas não a nota".
  - As fotos de exemplo: cada cadastrado é reconhecido só como ele mesmo, o rosto desconhecido não passa com ninguém e ninguém é confundido com outra pessoa. Se o limite ou a conta mudarem e isso quebrar, o teste avisa.
  - Banco e repositórios: criação, migração (inclusive de um banco antigo, só com a primeira versão), filtros de funcionário e período (o início conta e o fim não), remoção em cascata.
  - Fotos e dados de exemplo, pasta de dados.
  - Serviços: bater o ponto (reconhecido, desconhecido, sem funcionário, arquivo inválido), cadastro com validação, edição, remoção e relatórios em CSV.
- **O que não é testado:** as telas. O layout é com posições fixas e a aparência vai ser refeita no fim da trilha, então um teste de tela hoje seria jogado fora. As telas ficam com conferência manual.
- O driver do SQLite imprimia um aviso do Java 25 durante os testes. O `argLine` do Surefire passa `--enable-native-access=ALL-UNNAMED`, como o manifesto do `.jar` já faz.
- O CI roda os testes junto do build (`./mvnw verify`).

Para ter certeza de que os testes pegam erro de verdade, foram feitas cinco quebras propositais no código (limite de volta para 0,85, fim do período inclusivo, histograma sem normalizar, nota pelo histograma e remoção de funcionário sem apagar as fotos). Cada uma derrubou de 1 a 13 testes.

## Consequências

- 78 testes, em cerca de 3 segundos.
- O limite de 0,91 e a comparação ficam protegidos por dados, não só pelo ADR.
- Quem trocar a conta de comparação precisa passar pelas fotos de exemplo.
- As telas continuam sem teste automático.
