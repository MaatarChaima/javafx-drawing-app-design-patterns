package masi.graphe;

import java.util.List;
import java.util.Map;

/**
 * Interface Strategy AlgorithmePCC (ch.06 — partie optionnelle).
 * Chaque algorithme de plus court chemin implémente cette interface.
 * Le GrapheContext délègue le calcul à la stratégie active.
 */
public interface AlgorithmePCC {
    /**
     * Calcule le plus court chemin entre depart et arrivee dans le graphe.
     * @param adjacences map nœud → liste de (voisin, poids)
     * @param depart     nœud de départ
     * @param arrivee    nœud d'arrivée
     * @return liste ordonnée des nœuds du chemin, vide si aucun chemin
     */
    List<String> calculer(Map<String, List<Arete>> adjacences,
                          String depart,
                          String arrivee);
}
