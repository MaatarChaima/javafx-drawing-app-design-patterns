package masi.strategy;

/**
 * Interface Strategy LogStrategy (ch.06 slide 3).
 * Définit le contrat commun pour les trois stratégies de journalisation.
 */
public interface LogStrategy {
    void log(String message);
}
