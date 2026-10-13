package masi.graphe;

import java.util.*;

/**
 * Stratégie concrète : BFS — plus court chemin en nombre d'arêtes (ch.06).
 */
public class BFSStrategy implements AlgorithmePCC {

    @Override
    public List<String> calculer(Map<String, List<Arete>> adjacences,
                                  String depart, String arrivee) {
        Map<String, String> predecesseur = new HashMap<>();
        Queue<String>       file         = new LinkedList<>();
        Set<String>         visites      = new HashSet<>();

        file.add(depart);
        visites.add(depart);

        while (!file.isEmpty()) {
            String courant = file.poll();
            if (courant.equals(arrivee)) break;

            for (Arete a : adjacences.getOrDefault(courant, Collections.emptyList())) {
                if (!visites.contains(a.getDestination())) {
                    visites.add(a.getDestination());
                    predecesseur.put(a.getDestination(), courant);
                    file.add(a.getDestination());
                }
            }
        }

        List<String> chemin = new ArrayList<>();
        // Si l'arrivée n'a pas de prédécesseur et n'est pas le départ, aucun chemin
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
