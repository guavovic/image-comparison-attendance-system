# 5. Telas de gerenciamento

Data: 01/10/2026

Status: Aceito

## Contexto

Na versão da faculdade, boa parte dos botões das telas de gerenciamento não fazia nada: adicionar, editar e remover funcionário, gerenciamento de pontos, lista de avisos e os dois relatórios. Nenhum deles dizia o que devia acontecer, então algumas decisões foram necessárias.

## Decisão

- **Adicionar funcionário:** nome, turno, função e pelo menos uma foto. O ID deixou de ser digitado, porque o banco gera. Sem foto o funcionário nunca seria reconhecido, então o cadastro recusa. Os arquivos escolhidos precisam abrir como imagem, e nada é gravado quando algum falha.
- **Editar funcionário:** escolhe a pessoa numa lista, muda os dados e pode juntar mais fotos. Fotos com o mesmo nome de arquivo não se sobrescrevem.
- **Remover funcionário:** escolhe numa lista (antes era pelo ID digitado), com confirmação. Somem junto os pontos e as fotos dele.
- **Gerenciamento de pontos:** lista de todos os pontos, com busca e ordem invertida, e **Remover registro selecionado**, para o caso de ponto batido por engano.
- **Lista de avisos:** aviso é a **foto que não foi reconhecida** ao bater o ponto, com o nome do arquivo, o funcionário mais parecido e a nota. É o rastro de tentativa que fica faltando quando nenhum ponto é registrado. Os avisos ficam numa tabela `notices` do banco (migração 2 do esquema) e podem ser apagados.
- **Relatório de pontos:** por funcionário (ou todos) e por período (`dd/mm/aaaa`, em branco pega tudo), gravado em CSV. Relatório de funcionários: lista de cadastro em CSV.
- **Formato do CSV:** separador `;`, UTF-8 com marca de ordem de bytes, nota com vírgula (`93,23%`), campos entre aspas quando têm `;` ou aspas. É o que o Excel em português abre sem pedir nada.
- As telas continuam com a aparência de antes. O visual vai ser refeito numa etapa própria, no fim da trilha.

## Consequências

- Todos os botões fazem alguma coisa.
- O gerenciamento não tem usuário nem senha: quem abre o programa com `--admin` mexe em tudo. Para o uso do programa, que é de estudo, não foi tratado.
- Remover funcionário apaga o histórico dele. Não há como desfazer.
- O aviso só existe para foto não reconhecida. Outros avisos (por exemplo, quem não bateu o ponto no dia) ficam para uma ideia futura.
