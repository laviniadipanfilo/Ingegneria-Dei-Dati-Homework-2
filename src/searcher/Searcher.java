package searcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    public static void main(String[] args) {
        
        long appStartTime = System.currentTimeMillis(); 
        
        try (Scanner consoleInputScanner = new Scanner(System.in)) {

            Path indexFilePath = Paths.get("./Index");
            Directory indexDirectory = FSDirectory.open(indexFilePath);
            IndexReader indexDataAccessor = DirectoryReader.open(indexDirectory);
            IndexSearcher searchEngine = new IndexSearcher(indexDataAccessor);
            StoredFields docStoredFields = searchEngine.storedFields();

            
            Analyzer defaultTextAnalyzer = new ItalianAnalyzer();
            Map<String, Analyzer> fieldAnalyzerMap = new HashMap<>();
            fieldAnalyzerMap.put("filename", new KeywordAnalyzer());
            PerFieldAnalyzerWrapper finalAnalyzerWrapper = new PerFieldAnalyzerWrapper(defaultTextAnalyzer, fieldAnalyzerMap);

            System.out.println("---  Console di Ricerca Lucene ---");
            System.out.println("Sintassi supportata:");
            System.out.println("  • Ricerca per nome:       nome <termini>       (es: nome file1_java.txt)");
            System.out.println("  • Ricerca per contenuto:  contenuto <termini>  (es: contenuto \"sviluppo software\")");
            System.out.println("  • Ricerca globale:        <termini>            (es: java OR database)");
            System.out.println("  • Esci dal programma:     esci\n");

            while (true) {
                System.out.print(">>> Inserisci la tua query: ");
                String userInput = consoleInputScanner.nextLine().trim();

                if (userInput.equalsIgnoreCase("esci")) {
                    System.out.println("Chiusura del motore. Arrivederci!");
                    break;
                }

                if (userInput.isEmpty()) {
                    System.out.println(" Attenzione: query vuota. Inserisci dei termini.\n");
                    continue;
                }

                try {
                    String targetField = null;
                    String queryString = userInput;

                    if (userInput.matches("(?i)^(nome[:\\s]).*")) {
                        targetField = "filename";
                        queryString = userInput.replaceFirst("(?i)^nome[:\\s]+", "").trim();
                    } else if (userInput.matches("(?i)^(contenuto[:\\s]).*")) {
                        targetField = "content";
                        queryString = userInput.replaceFirst("(?i)^contenuto[:\\s]+", "").trim();
                    }

                    if (queryString.isEmpty()) {
                        System.out.println(" Attenzione: nessun termine specificato dopo il prefisso.\n");
                        continue;
                    }

                    Query finalLuceneQuery;
                    if (targetField != null) {
                        System.out.println("-> Ricerca su campo [" + targetField + "]: " + queryString);
                        QueryParser fieldParser = new QueryParser(targetField, finalAnalyzerWrapper);
                        fieldParser.setDefaultOperator(QueryParser.Operator.OR);
                        fieldParser.setAllowLeadingWildcard(true);
                        finalLuceneQuery = fieldParser.parse(queryString);
                    } else {
                        System.out.println("-> Ricerca globale (content, filename): " + queryString);
                        MultiFieldQueryParser multiParser = new MultiFieldQueryParser(
                                new String[]{"content", "filename"}, finalAnalyzerWrapper);
                        multiParser.setDefaultOperator(QueryParser.Operator.OR);
                        multiParser.setAllowLeadingWildcard(true);
                        finalLuceneQuery = multiParser.parse(queryString);
                    }

                    long searchStartTime = System.currentTimeMillis(); 
                    TopDocs topResults = searchEngine.search(finalLuceneQuery, 20);
                    long searchEndTime = System.currentTimeMillis(); 

                    System.out.println("\n--- Risultati ---");
                    System.out.println("Trovati " + topResults.totalHits + 
                                       " documenti in " + (searchEndTime - searchStartTime) + " ms.");

                    for (ScoreDoc resultScore : topResults.scoreDocs) {
                        Document resultDoc = docStoredFields.document(resultScore.doc);
                        float scoreValue = resultScore.score;
                        
                        System.out.printf(" %s | Punteggio: %.4f\n", resultDoc.get("filename"), scoreValue); 
                    }
                    System.out.println("-----------------\n");

                } catch (ParseException e) {
                    System.err.println("Errore di sintassi nella query Lucene. Dettagli: " + e.getMessage() + "\n");
                } catch (Exception e) {
                    System.err.println(" Errore critico durante l'esecuzione della ricerca: " + e.getMessage() + "\n");
                }
            }

            indexDataAccessor.close();
            indexDirectory.close();

        } catch (IOException e) {
            System.err.println(" Errore all'apertura o chiusura dell'indice Lucene: " + e.getMessage());
        }
        long appEndTime = System.currentTimeMillis();
        System.out.println("Tempo di attività totale: " + (appEndTime - appStartTime) + " ms.");
    }
}