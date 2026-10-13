package masi.graphe;

import java.util.*;

/**
 * Context du patron Strategy pour le calcul de plus court chemin (ch.06).
 * Maintient le graphe (liste d'adjacence) et délègue le calcul à l'algorithme actif.
 */
public class GrapheContext {
    private AlgorithmePCC                     algorithme;
    private final Map<String, List<Arete>>    adjacences = new HashMap<>();

    public GrapheContext() {
        this.algorithme = new DijkstraStrategy(); // algorithme par défaut
    }

    // ── Strategy ───────────────────────────────────────────────────────────
    public void setAlgorithme(AlgorithmePCC algorithme) {
        this.algorithme = algorithme;
    }

    // ── Gestion du graphe ──────────────────────────────────────────────────
    public void ajouterNoeud(String noeud) {
        adjacences.putIfAbsent(noeud, new ArrayList<>());
    }

    public void ajouterArete(String source, String dest, double poids) {
        adjacences.computeIfAbsent(source, k -> new ArrayList<>())
                  .add(new Arete(dest, poids));
        // Graphe non orienté : on ajoute les deux sens
        adjacences.computeIfAbsent(dest, k -> new ArrayList<>())
                  .add(new Arete(source, poids));
    }

    public void vider() {
        adjacences.clear();
    }

    public Set<String> getNoeuds() {
        return Collections.unmodifiableSet(adjacences.keySet());
    }

    // ── Calcul du plus court chemin ────────────────────────────────────────
    public List<String> calculerPCC(String depart, String arrivee) {
        return algorithme.calculer(adjacences, depart, arrivee);
    }
}
