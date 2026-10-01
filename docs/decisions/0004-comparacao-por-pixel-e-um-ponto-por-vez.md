# 4. Comparação por pixel e um ponto por vez

Data: 01/10/2026

Status: Aceito

## Contexto

A nota de parecença somava três medidas com pesos fixos (0,4 histograma, 0,3 pixel a pixel, 0,3 distância entre histogramas), e o ponto era registrado quando a soma chegava a 0,85. Os problemas:

- O histograma olhava só o canal vermelho e usava a contagem de pixels sem normalizar (até 10.000, dividida por 255). A parte do histograma podia dar negativa, e a nota saía do intervalo de 0 a 1.
- Para cada foto cadastrada com nota acima do limite, um ponto era registrado. A mesma pessoa podia bater o ponto várias vezes numa única leitura, e duas pessoas parecidas batiam juntas.
- O limite de 0,85 nunca foi conferido com fotos reais.

## Opções consideradas

Corrigido o histograma (as três cores, cada canal dividido pelo total de pixels, nota por interseção) e a distância (por canal, `1 - L2/√2` e `1 - L1/2`), medi as seis fotos de teste contra as cinco cadastradas:

| Pesos (histograma / pixel / distância) | Resultado nas fotos de teste |
| --- | --- |
| 0,4 / 0,3 / 0,3 | Ana é confundida com o Bruno (0,59 contra 0,50 dela). O desconhecido tira 0,84 contra o Bruno, que tem o mesmo fundo. Com 0,85, só o Bruno é reconhecido. |
| 0,2 / 0,6 / 0,2 | A Ana vence o Bruno por 0,008. O desconhecido tira 0,863, acima da Carla (0,847) e da Elisa (0,851). Nenhum limite separa. |
| 0,1 / 0,8 / 0,1 | Todos são reconhecidos, mas o desconhecido (0,878) fica acima da Ana (0,837). |
| 0 / 1 / 0 (só pixel a pixel) | Os cinco cadastrados passam, entre 0,93 e 0,99. O desconhecido tira 0,89, parecido só com o Bruno. Limite 0,91 separa todos. |

O histograma mede a distribuição de cores da imagem inteira, e nas fotos de teste isso é quase só a cor do fundo. O rosto está na posição de cada traço, que só a comparação pixel a pixel enxerga.

## Decisão

- A nota é a **semelhança pixel a pixel**, de 0 a 1 (1 menos a diferença média por canal, dividida por 255), com a imagem reduzida para 100 x 100. O limite passa de 0,85 para **0,91**.
- O histograma (as três cores, normalizado) e a distância entre histogramas continuam sendo calculados e saem em `Comparison`, mas não entram na nota.
- O ponto é registrado **uma vez**, para o funcionário da foto cadastrada mais parecida, e só se a nota passar do limite.
- A diferença de cada pixel não é mais arredondada para número inteiro antes de somar (a divisão por 3 descartava a fração).

## Consequências

- Nas seis fotos de teste, acerta as seis: cinco pessoas reconhecidas e o desconhecido recusado. Com o limite antigo de 0,85, o desconhecido seria registrado como Bruno.
- A margem é curta em dois pontos: o Bruno tira 0,93 contra o limite 0,91, e o desconhecido 0,89. Os avatares são ilustrações com variações pequenas e controladas. Com fotos de verdade, o limite precisa ser conferido de novo.
- A comparação pixel a pixel é sensível a deslocamento e giro maiores que os do teste, e isso fica como limitação conhecida.
- Histograma e distância, hoje sem peso na nota, ficam disponíveis para uma combinação futura.
