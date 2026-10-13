package masi.graphe;

/**
 * Représente une arête du graphe avec son destination et son poids.
 */
public class Arete {
    private final String destination;
    private final double poids;

    public Arete(String destination, double poids) {
        this.destination = destination;
        this.poids       = poids;
    }

    public String getDestination() { return destination; }
    public double getPoids()       { return poids; }

    @Override
    public String toString() { return destination + "(" + poids + ")"; }
}
