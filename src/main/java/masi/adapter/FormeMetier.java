package masi.adapter;

/**
 * Interface Target du patron Adapter (ch.04 slide 3).
 * Le reste de l'application ne dépend que de cette interface,
 * pas des classes JavaFX concrètes.
 */
public interface FormeMetier {
    void   dessiner();
    double getX();
    double getY();
    void   setX(double x);
    void   setY(double y);
    String getType();
}
