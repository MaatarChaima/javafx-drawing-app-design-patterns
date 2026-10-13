package masi.singleton;

import masi.observer.Observer;
import masi.strategy.LogStrategy;
import masi.strategy.ConsoleLogStrategy;

/**
 * Logger — triple rôle (ch.03 + ch.05 + ch.06) :
 *   • Singleton    : une seule instance dans toute l'application (ch.03)
 *   • Observer     : s'abonne à DrawingCanvas pour recevoir les notifications (ch.05)
 *   • Context Strategy : délègue l'écriture à la stratégie active (ch.06)
 *
 * La stratégie peut être changée à l'exécution depuis l'IHM (Usage A confirmé).
 */
public class Logger implements Observer {

    // ── Singleton (ch.03 slide 8 : initialisation à la demande) ──────────
    private static final Logger INSTANCE = new Logger();

    private LogStrategy strategy;

    // ── Constructeur privé ─────────────────────────────────────────────────
    private Logger() {
        // Stratégie par défaut : console
        this.strategy = new ConsoleLogStrategy();
    }

    // ── Point d'accès global ───────────────────────────────────────────────
    public static Logger getInstance() {
        return INSTANCE;
    }

    // ── Patron Strategy (ch.06) ────────────────────────────────────────────
    /**
     * Permet de changer dynamiquement la stratégie depuis l'IHM (Usage A).
     */
    public void setStrategy(LogStrategy strategy) {
        this.strategy = strategy;
        log("Stratégie de journalisation changée : " +
                strategy.getClass().getSimpleName());
    }

    /**
     * Délègue l'écriture à la stratégie active.
     */
    public void log(String message) {
        strategy.log(message);
    }

    // ── Patron Observer (ch.05) ────────────────────────────────────────────
    /**
     * Reçoit la notification de DrawingCanvas (le sujet observable).
     * Délègue à log() → respecte SRP.
     */
    @Override
    public void update(String message) {
        log(message);
    }
}
