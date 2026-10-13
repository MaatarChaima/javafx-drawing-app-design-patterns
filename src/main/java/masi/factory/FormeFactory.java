package masi.factory;

import masi.model.Forme;

/**
 * Creator abstrait du patron Factory Method (ch.08 slide 5).
 * Déclare la factory method creerForme() que les sous-classes redéfinissent.
 * Le client ne dépend que de cette abstraction, jamais des classes concrètes.
 */
public abstract class FormeFactory {

    /**
     * Factory Method (ch.08) : chaque Creator concret décide quelle
     * classe concrète de Forme instancier.
     */
    public abstract Forme creerForme();

    /**
     * Méthode de commodité : crée et dessine immédiatement la forme.
     * Conforme au patron : le client utilise l'abstraction.
     */
    public Forme creerEtDessiner() {
        Forme f = creerForme();
        f.dessiner();
        return f;
    }
}
