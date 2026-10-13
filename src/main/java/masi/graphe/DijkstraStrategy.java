package masi.graphe;

import java.util.*;

/**
 * Stratégie concrète : algorithme de Dijkstra (ch.06).
 */
public class DijkstraStrategy implements AlgorithmePCC {

    @Override
    public List<String> calculer(Map<String, List<Arete>> adjacences,
                                  String depart, String arrivee) {
        Map<String, Double>   dist         = new HashMap<>();
        Map<String, String>   predecesseur = new HashMap<>();
        // Critère de départage par nom pour des résultats reproductibles
        // quand deux nœuds ont la même distance estimée
        PriorityQueue<String> file = new PriorityQueue<>(
                Comparator.comparingDouble((String n) -> dist.getOrDefault(n, Double.MAX_VALUE))
                          .thenComparing(Comparator.naturalOrder()));

        for (String n : adjacences.keySet()) dist.put(n, Double.MAX_VALUE);
        dist.put(depart, 0.0);
        file.add(depart);

        while (!file.isEmpty()) {
            String courant = file.poll();
            if (courant.equals(arrivee)) break;

            List<Arete> voisins = adjacences.getOrDefault(courant, Collections.emptyList());
            for (Arete a : voisins) {
                double nouvelleDist = dist.get(courant) + a.getPoids();
                if (nouvelleDist < dist.getOrDefault(a.getDestination(), Double.MAX_VALUE)) {
                    dist.put(a.getDestination(), nouvelleDist);
                    predecesseur.put(a.getDestination(), courant);
                    file.add(a.getDestination());
                }
            }
        }

        // Reconstruction du chemin
        List<String> chemin = new ArrayList<>();
        // Si l'arrivée est inatteignable (distance infinie), retourner vide
        if (!arrivee.equals(depart) && !predecesseur.containsKey(arrivee)) {
            return new ArrayList<>();
        }
        String etape = arrivee;
        while (etape != null) {
            chemin.add(0, etape);
            etape = predecesseur.get(etape);
        }
        if (chemin.isEmpty() || !chemin.get(0).equals(depart)) return new ArrayList<>();
        return chemin;
    }
}
