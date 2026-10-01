# 8. Novo nome: Ponto Facial

Data: 01/10/2026

Status: Aceito

## Contexto

O programa se chamava FacePoint, e o repositório, `image-comparison-attendance-system`, nome em inglês que descrevia a técnica e não o que o programa faz. O projeto nasceu como trabalho de faculdade e está sendo finalizado, e o nome pedido é sóbrio, que diga o que ele é.

## Decisão

**Ponto Facial.**

- O repositório passa a ser `guavovic/ponto-facial`. O GitHub redireciona os links antigos.
- O pacote Java passa de `io.github.guavovic.facepoint` para `io.github.guavovic.pontofacial`.
- O artefato Maven é `ponto-facial` e o `.jar` sai como `target/ponto-facial.jar`.
- As janelas, os diálogos e o cabeçalho mostram "Ponto Facial".
- A pasta de dados continua `data/`, e o banco passa de `facepoint.db` para `pontofacial.db`. A propriedade que troca a pasta passa de `-Dfacepoint.data` para `-Dpontofacial.data`.
- A descrição do repositório foi reescrita em português.

Os ADRs 0001 a 0007 não foram reescritos: eles registram as decisões com o nome que o projeto tinha na época, e por isso falam em FacePoint e no pacote antigo.

## Consequências

- Quem tinha dados da versão anterior precisa renomear o `facepoint.db` para `pontofacial.db`. Como o programa ainda não teve uma versão publicada, isso só afeta dados de teste.
- Os links para o nome antigo do repositório continuam funcionando pelo redirecionamento do GitHub.
