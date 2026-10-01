# 7. Visual novo com FlatLaf

Data: 01/10/2026

Status: Aceito

## Contexto

As telas eram a versão da faculdade: Swing com o visual padrão do Java e a posição de cada botão escrita em coordenadas (`setBounds`). Por isso a janela não se ajustava a nada (texto cortado, tamanho fixo) e cada tela repetia as faixas, o logo e o relógio.

## Opções consideradas

- **FlatLaf com layouts de verdade:** tema moderno para o Swing, com cantos arredondados, fontes limpas e escala certa em tela de alta resolução. É uma dependência só.
- **Swing puro, só arrumando:** sem dependência nova, mas o visual padrão do Swing continua datado, mesmo com cores e espaços melhores.
- **JavaFX:** visual mais flexível, mas reescreve todas as telas e obriga a empacotar o JavaFX no `.jar`.

## Decisão

FlatLaf (tema claro, só ele) e layouts no lugar das coordenadas.

- O tema vem de `Theme.install()` e usa um verde-água como cor de destaque, o da faixa de antes.
- Toda tela tem a mesma estrutura, montada em `Ui.screen`: cabeçalho colorido com o título (e o relógio nas telas principais), conteúdo no meio e botões embaixo. O botão principal responde ao Enter.
- Os formulários (adicionar, editar, remover, relatório) usam um `Form` de rótulo e campo, com `GridBagLayout`. Turno é uma lista editável com manhã, tarde e noite, e as datas do relatório mostram o formato esperado dentro do campo.
- Os gerenciamentos viram blocos clicáveis com título e descrição, e "Gerenciamento de Acessos" passou a se chamar **Funcionários**.
- Pontos e avisos viram **tabelas** que se ordenam pelo cabeçalho. A busca de pontos olha o nome e a data como aparecem na tela. Com a ordenação, o botão "Inverter Ordem" deixou de ser necessário.
- Na tela principal, a foto cadastrada de quem foi reconhecido aparece ao lado dos dados dele, e o resultado da comparação fica numa área de texto que quebra linha.
- As janelas se ajustam ao conteúdo e têm largura mínima.

O que faz cada botão não mudou, só a forma. Os serviços e as regras ficaram como estavam, e por isso os testes seguem valendo sem alteração (só ganhou um, para `firstPhoto`).

## Consequências

- O `.jar` passou de 12 MB para 13 MB.
- O programa tem só o tema claro. Um tema escuro seria trocar o `FlatLightLaf` por `FlatDarkLaf` e conferir as cores do cabeçalho.
- As telas continuam sem teste automático, e a conferência do visual é feita olhando as janelas.
