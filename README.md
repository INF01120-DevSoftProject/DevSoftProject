# DevSoftProject

Tradutor de texto para música, desenvolvido para a disciplina INF01120 (UFRGS).

> **Status:** projeto em reestruturação. O código da versão anterior continua na branch `main`; a nova versão é construída na branch `develop`, a partir do novo UML.

## Requisitos

- **JDK 21 ou superior** (o projeto compila para Java 21).
- **Não é necessário instalar o Maven.** O projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`), que baixa a versão correta do Maven (3.9.16) automaticamente na primeira execução.

Para conferir se o Java está instalado:

```bash
java -version
```

## Como usar o Maven

Todos os comandos são executados na raiz do projeto, onde está o `pom.xml`.

| Sistema | Como chamar o wrapper |
|---|---|
| Windows (PowerShell) | `.\mvnw.cmd <comando>` |
| Linux / macOS | `./mvnw <comando>` |

> Se você já tem o Maven instalado, pode usar `mvn <comando>` no lugar do wrapper. Prefira o wrapper, que garante a mesma versão para todos.
>
> Na primeira execução é necessária conexão com a internet, para baixar o Maven e as dependências.

### Comandos principais

| Comando | O que faz |
|---|---|
| `compile` | Compila o código de `src/main/java` |
| `test` | Compila e executa todos os testes |
| `-Dtest=NomeDaClasseTest test` | Executa apenas uma classe de teste |
| `package` | Executa os testes e gera o JAR em `target/` |
| `clean` | Apaga a pasta `target/` |
| `clean verify` | Build completo, do zero |

Exemplos:

```bash
# Windows
.\mvnw.cmd test
.\mvnw.cmd -Dtest=NoteElementTest test
.\mvnw.cmd clean verify

# Linux / macOS
./mvnw test
./mvnw -Dtest=NoteElementTest test
./mvnw clean verify
```

O resultado dos testes aparece no terminal. Os relatórios ficam em `target/surefire-reports/`.

## Estrutura do projeto

```
DevSoftProject/
├── .mvn/wrapper/
│   └── maven-wrapper.properties   # versão do Maven usada pelo wrapper
├── src/
│   ├── main/
│   │   ├── java/                  # código-fonte do programa
│   │   └── resources/             # arquivos de recurso (se necessários)
│   └── test/
│       └── java/                  # testes unitários
├── .gitattributes                 # fim de linha dos scripts do wrapper
├── .gitignore
├── mvnw                           # wrapper para Linux / macOS
├── mvnw.cmd                       # wrapper para Windows
├── pom.xml                        # configuração do build e dependências
└── README.md
```

A pasta `target/` é gerada pelo Maven e não é versionada.

> O Git não registra pastas vazias. As pastas dentro de `src/` passam a existir no repositório conforme os primeiros arquivos forem adicionados.

### Convenções

- **Código e testes ficam separados:** classes em `src/main/java`, testes em `src/test/java`. Os testes nunca vão para o JAR final.
- **Pacotes seguem as pastas.** Uma classe do pacote `music` fica em `src/main/java/music/`, e o seu teste em `src/test/java/music/`.
- **Nome do teste:** `<Classe>Test`, por exemplo `NoteElementTest` para `NoteElement`.

### O que o `pom.xml` define

| Item | Valor |
|---|---|
| `groupId` / `artifactId` | `br.ufrgs.inf01120` / `devsoftproject` |
| Versão | `0.1.0-SNAPSHOT` |
| Java | 21 (`maven.compiler.release`) |
| Codificação | UTF-8 |
| JUnit | 6.1.3 (escopo `test`) |
| AssertJ | 3.27.7 (escopo `test`) |
| Maven Surefire Plugin | 3.6.0 (executa os testes) |

As versões ficam em `<properties>`. Para atualizar uma delas, basta alterar o valor no `pom.xml`.

## Arquitetura (UML)

O projeto está organizado em três partes:

- **Estrutura da música:** `Music` possui várias `Voice`. Cada `Voice` tem uma `SheetMusic`, que é uma lista de `MusicElement`, e um `VoicePreset` (instrumento, volume e oitava). `MusicElement` é uma interface, implementada por elementos como `NoteElement`, `OctaveElement`, `VolumeElement`, `BPMElement`, `PauseElement`, `NoteTempoElement`, `BemolElement`, `InstrumentElement` e `DefaultElement`.
- **Estrutura do input:** a interface `InputReader` é implementada por `TXTInputReader` e `TerminalInputReader`.
- **Estrutura do tradutor:** a classe abstrata `Translator` é estendida por `TextToSheetMusicTranslator` e `SheetMusicToMIDITranslator`. Cada tradutor usa uma implementação de `TranslatorRules`, e `CurrentState` guarda o estado atual durante a tradução.

## Fluxo de desenvolvimento

### Branches

| Branch | Uso |
|---|---|
| `main` | Versão antiga do projeto (mantida até a nova versão ficar pronta) |
| `develop` | Nova versão, em desenvolvimento |
| `feat/<nome>` | Uma classe ou funcionalidade, criada a partir da `develop` |
| `chore/<nome>`, `docs/<nome>` | Configuração e documentação |

### Passo a passo para cada classe ou interface

1. Criar a branch a partir da `develop`: `git checkout -b feat/<nome>`.
2. Escrever a classe em `src/main/java/...` e o teste em `src/test/java/...`.
3. Rodar os testes com `test` (ou só a classe com `-Dtest=...`).
4. Fazer o commit e integrar na `develop`.

### Mensagens de commit

O histórico segue o padrão `tipo(escopo): descrição`, por exemplo:

```
feat(core): adds the translator with HashMap
build: add Maven Wrapper (Maven 3.9.16)
chore: remove legacy code
```