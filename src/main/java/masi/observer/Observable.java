package masi.observer;

/**
 * Interface Observable (ch.05 slide 3).
 * Le sujet expose les méthodes d'abonnement et de notification.
 */
public interface Observable {
    void addObs(Observer o);
    void removeObs(Observer o);
    void notifyAllObs(String message);
}
