package masi.strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stratégie concrète : journalisation dans la console (ch.06).
 */
public class ConsoleLogStrategy implements LogStrategy {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void log(String message) {
        System.out.println("[LOG-CONSOLE] " +
                LocalDateTime.now().format(FMT) + " — " + message);
    }
}
