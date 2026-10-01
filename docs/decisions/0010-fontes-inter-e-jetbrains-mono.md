# 10. Fontes Inter e JetBrains Mono

Data: 01/10/2026

Status: Aceito

## Contexto

Depois do visual novo (ADR 0007), as telas usavam a fonte do sistema, que no Windows é a Segoe UI, e o resultado da comparação usava o `Monospaced` do Java, que cai numa Courier. As duas ficavam datadas ao lado do resto, e a fonte do sistema muda de máquina para máquina.

## Decisão

- **Inter** para todos os textos e **JetBrains Mono** para o resultado da comparação, onde o alinhamento das colunas importa.
- As fontes vêm dos pacotes do próprio FlatLaf (`flatlaf-fonts-inter` e `flatlaf-fonts-jetbrains-mono`), que as registram e as ligam às famílias preferidas do tema em `Theme.install()`. Não é preciso instalar nada na máquina, porque as fontes vão dentro do `.jar`.
- O resultado usa o estilo `monospaced` do FlatLaf, em vez de uma fonte escrita no código.
- Ambas as fontes são livres, com licença SIL OFL, e os arquivos de licença seguem dentro do `.jar`. No empacotamento, o manifesto e o `META-INF/LICENSE` repetidos dos pacotes de fonte ficam de fora.

## Consequências

- O visual é o mesmo em qualquer máquina.
- O `.jar` passou de 33 MB para 35 MB.
- A barra de título da janela continua com a fonte do sistema, porque quem a desenha é o Windows.
