package masi.observer;

/**
 * Interface Observer (ch.05).
 * Tout observateur doit implémenter update() pour recevoir les notifications.
 */
public interface Observer {
    void update(String message);
}
