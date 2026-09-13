# Homework

## Struttura del Progetto

```text
├── Data/              # Documenti di testo (.txt) da indicizzare
├── Index/             # Indice invertito generato da Lucene
├── lib/               # Librerie JAR di Apache Lucene
├── src/
│   ├── index/         # Indexer.java (costruzione dell'indice)
│   └── searcher/      # Searcher.java (interrogazione da console)
├── RELAZIONE.md       # Relazione tecnica completa con le 10 query di test
└── README.md          # Guida rapida all'avvio
```

## Prerequisiti
* Java 21 JDK
* VS Code con estensione *Extension Pack for Java* (oppure Eclipse / IntelliJ)

## Come Eseguire

### 1. Indicizzazione
Esegui la classe `src/index/Indexer.java` per leggere i file in `Data/` e generare l'indice nella cartella `Index/`.

### 2. Ricerca
Esegui la classe `src/searcher/Searcher.java` per avviare la console interattiva.

**Esempi di query supportate:**
* `nome file1_java.txt` *(ricerca per nome file esatto)*
* `nome *csharp*` *(ricerca per nome file con wildcard)*
* `contenuto "sviluppo software"` *(ricerca per frase esatta tra virgolette)*
* `contenuto +protocollo +TCP` *(ricerca con operatori booleani)*
* `database` *(ricerca globale su tutti i campi)*
* `esci` *(per terminare il programma)*