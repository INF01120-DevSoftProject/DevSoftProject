# DevSoftProject — Tradutor de Texto para Música

Aplicação desktop em Java (com interface gráfica em **JavaFX**) que converte textos livres ou formatados em composições musicais polifônicas no formato **MIDI**, permitindo configurar instrumentos por voz, personalizar o mapeamento de caracteres, visualizar a partitura gerada e reproduzir as músicas em um player integrado.

Desenvolvido para a disciplina **INF01120 — Técnicas de Construção de Programas** (UFRGS).

---

## Sumário

1. [Requisitos e Configuração do Ambiente](#1-requisitos-e-configuração-do-ambiente)
2. [Como Executar e Usar o Programa (Passo a Passo)](#2-como-executar-e-usar-o-programa-passo-a-passo)
3. [Regras de Tradução (Texto → Música)](#3-regras-de-tradução-texto--música)
4. [Arquitetura e Organização Atual do Projeto](#4-arquitetura-e-organização-atual-do-projeto)
5. [Executando os Testes e Outros Comandos Maven](#5-executando-os-testes-e-outros-comandos-maven)
6. [Convenções e Fluxo de Desenvolvimento](#6-convenções-e-fluxo-de-desenvolvimento)

---

## 1. Requisitos e Configuração do Ambiente

- **JDK 21 ou superior** (o projeto compila para Java 21).
- **Não é necessário instalar o Maven.** O projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`), que baixa a versão correta do Maven (`3.9.16`) e todas as dependências automaticamente na primeira execução (requer conexão com a internet na primeira vez).

Para conferir se o Java está instalado:

```bash
java -version
```

### Configurando o `JAVA_HOME`

O Maven Wrapper exige a variável de ambiente `JAVA_HOME` apontando para a **pasta raiz do JDK** (a que contém `bin/javac`), e não para a pasta `bin` nem para o `java.exe`. Sem ela, ou com um caminho incorreto, aparece o erro:

```text
The JAVA_HOME environment variable is not defined correctly,
this environment variable is needed to run this program.
```

Para conferir o valor atual:

```powershell
echo $env:JAVA_HOME          # Windows (PowerShell)
```

```bash
echo $JAVA_HOME              # Linux / macOS
```

**Windows (PowerShell)** — define de forma permanente para o seu usuário (feche e reabra o terminal depois):

```powershell
[Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\caminho\para\o\jdk-21', 'User')
```

Para definir apenas na sessão atual do terminal:

```powershell
$env:JAVA_HOME = 'C:\caminho\para\o\jdk-21'
```

**Linux / macOS** — adicione ao `~/.bashrc` ou `~/.zshrc`:

```bash
export JAVA_HOME=/caminho/para/o/jdk-21
```

> **Dica:** Se você não tem um JDK instalado separadamente, a extensão *Language Support for Java (Red Hat)* do VS Code traz um JDK 21 em `%USERPROFILE%\.vscode\extensions\redhat.java-<versão>\jre\<versão-do-jdk>`. Como o nome dessa pasta muda a cada atualização da extensão, prefira instalar um JDK 21 próprio (por exemplo, Eclipse Temurin) e apontar o `JAVA_HOME` para ele.

---

## 2. Como Executar e Usar o Programa (Passo a Passo)

Atualmente, todo o fluxo do programa acontece pela **interface gráfica (GUI)**, onde você compõe (ou importa) a música em texto, exporta o arquivo `.mid` e o reproduz no **Player** integrado.

### Passo 1: Iniciar a interface gráfica

Na raiz do projeto (onde está o `pom.xml`), execute o plugin do JavaFX pelo Maven Wrapper:

```powershell
# Windows (PowerShell)
.\mvnw.cmd javafx:run
```

```bash
# Linux / macOS
./mvnw javafx:run
```

A janela principal (`gui.ScreenManager`) será aberta com um menu lateral contendo três telas: **Composição**, **Mapeamento** e **Player**.

### Passo 2: Escrever ou importar uma música (Tela "Composição")

Ao abrir o programa, você estará na tela de **Composição**:

1. **Configurar as vozes:**
   - A composição suporta de **1 a 15 vozes** na interface (internamente o modelo garante no mínimo 4 faixas). Use os botões **`+`** e **`-`** para adicionar ou remover vozes.
   - Em cada voz (`Voz 1`, `Voz 2`, etc.), escolha o **Instrumento**, o **Volume** inicial (`0` a `100`) e a **Oitava** base. Cada nova voz adicionada alterna automaticamente entre as famílias de instrumentos (*Teclas*, *Cordas Dedilhadas*, *Cordas*, *Sopros* e *Percussão/Sintetizador*).
2. **Inserir o texto musical:**
   - **Opção A — Digitar manualmente:** Digite o texto diretamente na caixa `"Digite sua partitura"` de cada voz.
   - **Opção B — Importar de um arquivo `.txt`:** Clique no botão **"Importar música"** no rodapé e selecione um arquivo `.txt` (cada linha do arquivo preenche uma voz).
   - *Dica para avaliação:* Há um arquivo pronto para teste em `src/main/resources/input/exemplo_input.txt`.

### Passo 3 (Opcional): Consultar ou personalizar os caracteres (Tela "Mapeamento")

Caso queira verificar ou alterar quais caracteres ativam cada nota ou comando:

1. Clique em **"Mapeamento"** na barra lateral esquerda.
2. Navegue entre as abas **Notas**, **Pausas**, **Acidentes** e **Outros**.
3. Altere os caracteres desejados e clique em **"Salvar Mapeamento"** para aplicar, ou em **"Restaurar ao Padrão"** para redefinir as regras originais.

### Passo 4: Gerar e salvar a música para o Player

1. De volta à tela **"Composição"**, clique no botão **"Salvar música"**.
2. Uma janela de seleção de arquivo será aberta já posicionada por padrão na pasta **`src/main/resources/musics/`** com o nome sugerido `musica.mid`.
3. **Importante:** Salve o arquivo `.mid` **dentro dessa pasta padrão (`src/main/resources/musics/`)** para que o Player da aplicação o localize automaticamente.
4. Ao confirmar, o pipeline traduz o texto de todas as vozes, gera a sequência MIDI e grava o arquivo `.mid`.

### Passo 5: Reproduzir a música e ver a partitura (Tela "Player")

1. Clique em **"Player"** na barra lateral esquerda.
2. No menu suspenso (ComboBox) no topo da tela, selecione o arquivo `.mid` que você acabou de salvar (a lista de músicas da pasta `src/main/resources/musics/` é atualizada automaticamente a cada 2 segundos).
3. A **partitura musical** correspondente ao arquivo MIDI será renderizada na tela com um cursor azul indicando a posição atual da música.
4. Utilize os controles na parte inferior:
   - **`▶` / `⏸`:** Inicia ou pausa a reprodução do áudio MIDI.
   - **Barra de progresso (Slider):** Permite acompanhar o tempo ou arrastar para avançar/retroceder a música.
   - **`⏮` / `⏭`:** Alterna para a música anterior ou seguinte disponível na pasta `musics/`.

---

## 3. Regras de Tradução (Texto → Música)

O tradutor (`TextToMusicTranslator`) analisa o texto de cada voz usando expressões regulares configuradas em `TextToMusicRules`. No mapeamento padrão, os comandos reconhecidos são:

| Elemento / Ação | Sintaxe / Caracteres Padrão | Comportamento |
|---|---|---|
| **Notas musicais** | `C`, `D`, `E`, `F`, `G`, `A`, `B` | Tocam respectivamente Dó, Ré, Mi, Fá, Sol, Lá e Si na oitava atual. |
| **Acidentes** | `#` (sustenido) ou `b` (bemol) logo após a nota | Exemplo: `C#`, `Eb`. Altera a nota em meio-tom. |
| **Duração da nota** | `:<1 a 7>` após a nota (ou acidente) | `1` = semibreve, `2` = mínima, `3` = semínima (padrão), `4` = colcheia, `5` = semicolcheia, `6` = fusa, `7` = semifusa. Exemplo: `D:2`, `Bb:4`. |
| **Pausa** | `a`, `c`, `d`, `e`, `f`, `g`, `h` | Insere uma pausa de semínima. |
| **Atraso inicial da voz** | `[n]` no início da linha (`n` de 1 a 3 dígitos) | Faz a voz esperar `n` tempos (semínimas) antes de começar a tocar. Exemplo: `[4] C D E`. |
| **Subir oitava** | `?` ou `.` | Aumenta a oitava atual em +1 (se passar da oitava máxima `8`, retorna à oitava base da voz). |
| **Descer oitava** | `V` | Diminui a oitava atual em -1 (limitado à oitava mínima `0`). |
| **Aumentar / Diminuir BPM** | `>` (aumenta) / `<` (diminui) | Altera o andamento em `+10 BPM` ou `-10 BPM` (entre `20` e `999` BPM; padrão inicial: `120` BPM). |
| **Dobrar volume** | Espaço (`' '`) | Dobra o volume atual da voz (limitado ao máximo MIDI de `127`). |
| **Trocar instrumento** | Dígitos pares: `0`, `2`, `4`, `6`, `8` | Avança para o próximo instrumento dentro da mesma família da voz (fazendo ciclo ao chegar ao fim). |
| **Outros caracteres** | Qualquer outro caractere não mapeado | Se o último elemento musical foi uma nota, **repete essa nota**; caso contrário, insere uma **pausa**. |

---

## 4. Arquitetura e Organização Atual do Projeto

O sistema foi projetado de forma modular em camadas bem definidas, separando a leitura de entrada, a tradução de texto para o modelo de domínio musical, a conversão/exportação para MIDI, a reprodução de áudio e a interface gráfica.

### Fluxo de Dados (Pipeline)

```text
Texto (GUI ou .txt) ──► TXTInputReader
                    ──► TextToMusicTranslator (+ TextToMusicRules / TextToMusicContext)
                    ──► Music (Voices + SheetMusic + VoicePresets + MusicElements)
                    ──► MusicToMidiParser (+ MidiSequenceBuilder)
                    ──► Sequence (javax.sound.midi)
                    ──► MidiFileExporter ──► Arquivo .mid (src/main/resources/musics/)
                    ──► MidiPlayer & SheetMusicWidget (PlayerScreen)
```

O processo de tradução, conversão e gravação é orquestrado pela classe genérica `MusicExportPipeline<T>`.

### Estrutura de Pastas e Pacotes

```text
DevSoftProject/
├── .mvn/wrapper/                  # Configuração do Maven Wrapper (Maven 3.9.16)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── builders/          # Construção de sequências MIDI (MidiSequenceBuilder)
│   │   │   ├── exporters/         # Exportação para arquivos e pipeline (MusicExportPipeline, MidiFileExporter, FileExporter)
│   │   │   ├── gui/               # Interface gráfica em JavaFX
│   │   │   │   ├── builders/      # Builders fluentes de layout e controles (BoxBuilder, ButtonBuilder, etc.)
│   │   │   │   ├── factory/       # Fábrica de componentes visuais (ControlFactory)
│   │   │   │   ├── screens/       # Telas da aplicação (CompositionScreen, MapperScreen, PlayerScreen, Screen)
│   │   │   │   ├── widgets/       # Componentes customizados (VoiceWidget, SheetMusicWidget, MusicSliderWidget, etc.)
│   │   │   │   └── ScreenManager.java # Ponto de entrada (Main Class) da aplicação JavaFX
│   │   │   ├── input/             # Leitura de arquivos de entrada (InputReader, TXTInputReader)
│   │   │   ├── music/             # Modelo de domínio da música (Music, Voice, SheetMusic, VoicePreset)
│   │   │   ├── musicElements/     # Elementos musicais (MusicElement, NoteElement, PauseElement, BPMElement, VolumeElement, InstrumentElement, Octave, Notes, DurationTime, Accidentals, InstrumentType)
│   │   │   ├── parsers/           # Conversão do domínio Music para formato alvo (MusicParser, MusicToMidiParser)
│   │   │   ├── player/            # Reprodução de áudio (Player, MidiPlayer)
│   │   │   ├── translators/
│   │   │   │   └── TextToMusic/   # Tradutor de texto para Music (TextToMusicTranslator, TextToMusicRules, TextToMusicContext, TextToMusicActions)
│   │   │   └── utils/             # Utilitários de diretórios e arquivos (MusicPathUtils, MidiMusicPathUtils)
│   │   └── resources/
│   │       ├── fonts/             # Fonte tipográfica utilizada na interface (Inter-Regular.ttf)
│   │       ├── input/             # Arquivo .txt de exemplo para importação (exemplo_input.txt)
│   │       ├── musics/            # Pasta padrão onde os arquivos .mid são salvos e lidos pelo Player
│   │       └── styles/            # Folhas de estilo CSS das telas e componentes JavaFX
│   └── test/
│       └── java/                  # Testes unitários e de integração (espelhando os pacotes de src/main/java + integration/FullExecutionTest)
├── mvnw / mvnw.cmd                # Scripts do Maven Wrapper
├── pom.xml                        # Configuração do build, plugins e dependências
└── README.md
```

### Responsabilidades de Cada Módulo

- **`music` e `musicElements` (Domínio Musical):**
  - `Music` agrega uma lista de vozes (`Voice`, entre 4 e 15) e o BPM padrão (`120`).
  - Cada `Voice` possui um `VoicePreset` inicial (instrumento, volume e oitava padrão de sua família) e uma `SheetMusic` (lista sequencial de `MusicElement`).
  - `MusicElement` é a interface comum implementada por notas (`NoteElement`), pausas (`PauseElement`) e eventos de controle (`BPMElement`, `VolumeElement`, `InstrumentElement`), apoiados pelos enums e classes de valor `Notes`, `Octave`, `DurationTime`, `Accidentals` e `InstrumentType`.
- **`input` (Entrada):**
  - A interface `InputReader` e sua implementação `TXTInputReader` realizam a leitura e validação de arquivos `.txt`.
- **`translators.TextToMusic` (Tradução Texto → Domínio):**
  - `TextToMusicTranslator` divide o texto em linhas (uma por voz) e aplica o tokenizador regex de `TextToMusicRules`.
  - `TextToMusicContext` mantém o estado mutável durante a tradução (voz atual, oitava, volume, BPM, instrumento e último elemento inserido).
- **`parsers`, `builders` e `exporters` (Conversão Domínio → MIDI e Gravação):**
  - `MusicToMidiParser` percorre as vozes e elementos de um objeto `Music` e delega a criação de tracks e eventos MIDI (`ShortMessage` e `MetaMessage`) ao `MidiSequenceBuilder`.
  - `MidiFileExporter` grava o objeto `Sequence` gerado em um arquivo `.mid` no disco.
  - `MusicExportPipeline` conecta tradutor, parser e exportador em uma única chamada usada pela GUI.
- **`player` e `utils` (Reprodução e Gerenciamento de Arquivos):**
  - `MidiPlayer` implementa `Player<Sequencer>`, encapsulando o `Sequencer` do Java Sound para carregar arquivos `.mid`, tocar, pausar, parar, buscar posições (`seek`) e informar o tempo/progresso para a partitura.
  - `MidiMusicPathUtils` gerencia a listagem e navegação entre os arquivos `.mid` localizados em `src/main/resources/musics/`.
- **`gui` (Interface Gráfica JavaFX):**
  - `ScreenManager` inicializa o palco principal e gerencia a navegação lateral entre `CompositionScreen`, `MapperScreen` e `PlayerScreen`.
  - Utiliza o componente `StripSheetView` da biblioteca `SheetMusic4J` (dentro de `SheetMusicWidget`) para desenhar a partitura sincronizada com o `MidiPlayer`.

### O que o `pom.xml` define

| Item | Valor |
|---|---|
| `groupId` / `artifactId` | `br.ufrgs.inf01120` / `devsoftproject` |
| Versão | `0.1.0-SNAPSHOT` |
| Java | 21 (`maven.compiler.release`) |
| Codificação | UTF-8 |
| JavaFX Controls | `27` (interface gráfica) |
| SheetMusic4J (`fxviewer`) | `0.0.5` (visualização de partitura no JavaFX) |
| JavaFX Maven Plugin | `0.0.8` (`mainClass`: `gui.ScreenManager`) |
| JUnit Jupiter | `6.1.3` (escopo `test`) |
| AssertJ | `3.27.7` (escopo `test`) |
| Maven Surefire Plugin | `3.6.0` (execução dos testes) |

---

## 5. Executando os Testes e Outros Comandos Maven

Todos os comandos devem ser executados na raiz do projeto:

| Sistema | Como chamar o wrapper |
|---|---|
| Windows (PowerShell) | `.\mvnw.cmd <comando>` |
| Linux / macOS | `./mvnw <comando>` |

### Comandos principais

| Comando | O que faz |
|---|---|
| `javafx:run` | Compila e inicia a interface gráfica do programa |
| `compile` | Compila o código de `src/main/java` |
| `test` | Compila e executa todos os testes unitários e de integração |
| `-Dtest=NomeDaClasseTest test` | Executa apenas uma classe de teste específica |
| `package` | Executa os testes e gera o JAR em `target/` |
| `clean` | Apaga a pasta `target/` |
| `clean verify` | Build completo e execução de todos os testes do zero |

Exemplos:

```powershell
# Windows (PowerShell)
.\mvnw.cmd javafx:run
.\mvnw.cmd test
.\mvnw.cmd -Dtest=FullExecutionTest test
.\mvnw.cmd clean verify
```

```bash
# Linux / macOS
./mvnw javafx:run
./mvnw test
./mvnw -Dtest=FullExecutionTest test
./mvnw clean verify
```

O resultado dos testes aparece no terminal, e os relatórios detalhados ficam em `target/surefire-reports/`.

> **Destaque de Teste:** A classe `src/test/java/integration/FullExecutionTest.java` executa um teste de ponta a ponta (integração) de todo o pipeline sem depender da interface gráfica: lê um `.txt`, traduz para `Music`, converte para `Sequence` MIDI, exporta o arquivo `.mid` e valida a reprodução no `MidiPlayer`.

---

## 6. Convenções e Fluxo de Desenvolvimento

### Convenções de Código

- **Código e testes ficam separados:** classes em `src/main/java`, testes em `src/test/java`. Os testes nunca vão para o JAR final.
- **Toda classe pertence a um pacote**, tanto em `src/main/java` quanto em `src/test/java`.
- **O teste usa o mesmo pacote da classe testada:** por exemplo, `Music` fica em `src/main/java/music/Music.java` e `MusicTest` em `src/test/java/music/MusicTest.java`, ambos com `package music;`.
- **Nome do teste:** `<Classe>Test`, por exemplo `NoteElementTest` para `NoteElement`.
- **Um tipo público por arquivo**, com o arquivo tendo o mesmo nome da classe ou interface.

### Branches

| Branch | Uso |
|---|---|
| `main` | Versão anterior do projeto |
| `develop` | Versão atual reestruturada (branch principal de desenvolvimento e avaliação) |
| `feat/<nome>` | Desenvolvimento de uma classe ou funcionalidade a partir da `develop` |
| `refactor/<nome>`, `chore/<nome>`, `docs/<nome>` | Refatorações, configuração e documentação |

### Padrão de Commits

O histórico segue o padrão *Conventional Commits* (`tipo(escopo): descrição`), por exemplo:

```text
feat(core): adds the translator with HashMap
build: add Maven Wrapper (Maven 3.9.16)
chore: remove legacy code
```
