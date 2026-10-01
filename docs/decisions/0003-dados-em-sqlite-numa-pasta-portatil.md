# 3. Dados em SQLite numa pasta portátil

Data: 01/10/2026

Status: Aceito

## Contexto

Os dados viviam dentro da pasta das classes compiladas. A cada execução, o `SalvarDiretorio` gravava o IP da máquina em `ipPC.txt` e a pasta das classes em `diretorio.txt`, e cada parte do programa montava o próprio caminho com `replace("salvar/", "\\processamento\\...")`. Por isso:

- O programa só funcionava no Windows, porque as barras invertidas estavam fixas no código.
- Pelo `.jar`, os caminhos apontavam para dentro do próprio arquivo, e nada era encontrado.
- O funcionário mostrado na tela vinha de um `funcionario.txt` fixo, e o ponto ia sempre para a mesma pessoa, escrita no código.
- Para bater o ponto, era preciso colocar a foto numa pasta `validar/`, e o programa renomeava o arquivo para `funcionario.png`.
- Cada ponto era um `.txt` com a palavra "Acrescentar", sem o funcionário nem a nota da comparação.

O FacePoint é um programa pequeno, que deve rodar de qualquer pasta, sem instalação.

## Opções consideradas

Onde guardar:

- **Pasta do usuário** (`%APPDATA%`, `~/Library/Application Support`, `~/.local/share`), o padrão de aplicativo instalado.
- **Pasta `data/` ao lado do programa**: copiar a pasta leva tudo junto.
- **Pasta escolhida na primeira execução**, guardada nas preferências do Java.

Como guardar funcionários e pontos:

- **Arquivos de texto** (`.properties` e `.csv`), sem dependência, mas com filtros e relatórios escritos à mão.
- **SQLite** via JDBC, num arquivo só.
- **H2**, banco em Java puro, também via JDBC.

## Decisão

SQLite numa pasta `data/` ao lado do programa.

- A pasta é a do `.jar`. Rodando pelas classes compiladas (na IDE), é a pasta de onde o programa foi aberto. `-Dfacepoint.data=<pasta>` troca o local.
- `data/facepoint.db` guarda os funcionários e os pontos. Cada ponto tem o funcionário, a data e hora e a nota da comparação.
- As fotos continuam arquivos de imagem, em `data/photos/<id do funcionário>/`, e um funcionário pode ter mais de uma.
- O esquema do banco é versionado com `PRAGMA user_version`: cada mudança futura entra como um novo passo na lista de migrações, e o banco de quem já usa o programa é atualizado ao abrir.
- O driver `sqlite-jdbc` é a única dependência. O `.jar` final inclui o driver (`maven-shade-plugin`) e declara `Enable-Native-Access` no manifesto, porque o driver carrega uma biblioteca nativa e o Java 25 avisaria a cada execução.
- Na primeira execução, o programa cadastra cinco funcionários fictícios com avatares do [DiceBear](https://www.dicebear.com) (estilo Lorelei, licença CC0) e copia para `data/test-photos/` uma foto de teste de cada um, com pequenas variações, mais um rosto que não está cadastrado.
- **Bater Ponto** abre uma janela para escolher a foto, que não é mais movida nem renomeada. O ponto vai para o funcionário reconhecido, que aparece na tela, e o botão **Registros** mostra os pontos dele.

Junto com os dados, o código foi reorganizado em pacotes por responsabilidade, sob `io.github.guavovic.facepoint`, com os nomes de código em inglês (o texto das telas continua em português):

- `config`: onde ficam os dados.
- `domain`: `Employee` e `AttendanceRecord`.
- `storage`: banco, repositórios, fotos e dados de exemplo.
- `recognition`: a comparação de imagens, sem acesso a arquivo nem a tela.
- `service`: o ponto, ligando a comparação aos dados.
- `ui`: as telas, com as partes repetidas (faixas, logo, relógio) num lugar só.

O programa abre na tela do funcionário. A tela de gerenciamento, que antes tinha um `main` próprio, abre com `--admin`. Como o item de telas vai mexer nisso, ficou o caminho mais simples.

## Consequências

- O programa roda de qualquer pasta, em qualquer sistema, pelo `.jar`.
- O `.jar` passou de 60 KB para 12 MB, porque o driver traz a biblioteca nativa de todos os sistemas.
- Relatórios por período e por funcionário viram consultas SQL.
- A comparação continua igual. Com as fotos de exemplo, ela confunde rostos e recusa fotos parecidas, e isso é assunto do item de comparação.
- As telas de adicionar, editar e remover funcionário continuam sem ação até o item de telas.
