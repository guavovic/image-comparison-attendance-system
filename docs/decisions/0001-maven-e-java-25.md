# 1. Maven e Java 25

Data: 01/10/2026

Status: Aceito

## Contexto

O FacePoint era um projeto do Eclipse: os fontes em `sistema-bater-ponto-ia-projeto/src`, a configuração em `.classpath`, `.project` e `.settings/`, e o Java 17 escolhido nas preferências da IDE. Sem a IDE não havia como compilar nem gerar o programa, e nada disso rodaria num CI.

## Opções consideradas

- **Maven**: o build mais usado em projetos Java, com estrutura de pastas padrão e configuração declarativa em XML. Toda IDE abre o projeto sem configuração extra.
- **Gradle**: mais flexível e mais rápido em projetos grandes, com o build escrito em Kotlin ou Groovy. Para um app sem dependências, a flexibilidade não compensa ter um script de build para manter.
- **Continuar só com o Eclipse**: nada muda, mas o projeto segue preso à IDE e sem CI.

Para a versão do Java:

- **Java 17**, que é o que o projeto já usava. Ainda tem suporte, mas é a LTS mais antiga das três em uso.
- **Java 21**, LTS de 2023.
- **Java 25**, LTS de setembro de 2025, a mais recente.

## Decisão

Maven com Java 25.

- Os fontes vão para `src/main/java`, na raiz do repositório, e os arquivos do Eclipse saem. A pasta `sistema-bater-ponto-ia-projeto/` deixa de existir.
- O Maven Wrapper (`mvnw`, `mvnw.cmd`) vai junto, fixando o Maven 3.9.16. Quem clona só precisa do JDK 25.
- `maven.compiler.release` em 25. O `maven-enforcer-plugin` recusa o build com JDK anterior ao 25 ou Maven anterior ao 3.9, com uma mensagem clara em vez de um erro de compilação.
- As versões de todos os plugins ficam fixas no `pom.xml`, para o build não mudar sozinho quando o Maven troca os padrões.
- `-Xlint:all` com aviso tratado como erro. O lint `serial` fica de fora: todo `JFrame` é `Serializable`, e as telas nunca são serializadas.
- O `.jar` sai como `target/facepoint.jar`, com `com.visuais.TelaCentral` como classe principal no manifesto.

Os avisos que o compilador apontou foram corrigidos na mesma mudança:

- Cinco métodos estáticos chamados por uma instância criada só para isso (`new TelaADUsuario().main()` e parecidos). Agora a chamada é pela classe. Nas telas, isso também deixa de criar uma janela invisível a cada clique.
- `SalvarDiretorio`, `TelaRegistroCentral` e `TelaRegistroGerenciamento` chamam métodos que poderiam ser sobrescritos dentro do construtor (`this-escape`). Nenhuma delas tem subclasse, então viraram `final`.

## Consequências

- O projeto compila e gera o `.jar` com `./mvnw package`, sem IDE.
- O CI pode rodar o mesmo comando.
- Quem for compilar precisa do JDK 25.
- Pelo `.jar`, o app ainda não acha os dados, porque o `SalvarDiretorio` monta os caminhos a partir do local da classe. Isso é assunto do item de dados e estrutura.
