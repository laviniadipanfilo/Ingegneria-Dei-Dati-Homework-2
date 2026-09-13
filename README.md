# Homework 2

## Prerequisiti
* Java 21 JDK
* VS Code con estensione Extension Pack for Java (oppure Eclipse / IntelliJ)

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