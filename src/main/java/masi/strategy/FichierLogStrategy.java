package masi.strategy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stratégie concrète : journalisation dans un fichier texte (ch.06).
 * Ouvre le fichier en mode append pour ne pas écraser les anciens logs.
 */
public class FichierLogStrategy implements LogStrategy {

    private static final String     NOM_FICHIER = "log.txt";
    private static final DateTimeFormatter FMT  =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void log(String message) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(NOM_FICHIER, true))) {
            pw.println("[LOG-FICHIER] " +
                    LocalDateTime.now().format(FMT) + " — " + message);
        } catch (IOException e) {
            // Repli sur la console si le fichier n'est pas accessible
            System.err.println("[FichierLogStrategy] Impossible d'écrire dans " +
                    NOM_FICHIER + " : " + e.getMessage());
            System.out.println("[LOG-CONSOLE-REPLI] " +
                    LocalDateTime.now().format(FMT) + " — " + message);
        }
    }
}
