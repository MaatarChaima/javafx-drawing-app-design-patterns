package masi.decorator;

import masi.model.Forme;
import javafx.scene.paint.Color;

/**
 * Décorateur abstrait (ch.07 slide 4).
 * Hérite de Forme ET contient une référence vers Forme.
 * Les décorateurs concrets enrichissent la forme sans modifier sa classe.
 */
public abstract class FormeDecorator extends Forme {

    // Référence vers le composant à décorer (ch.07 slide 5)
    protected Forme forme;

    protected FormeDecorator(Forme forme) {
        super(forme.getX(), forme.getY(), forme.getCouleur());
        this.forme = forme;
    }

    // Délégation par défaut vers le composant (ch.07 slide 5)
    @Override
    public void dessiner() {
        forme.dessiner();
    }

    @Override
    public String getType() {
        return forme.getType();
    }

    @Override
    public java.util.Map<String, Double> getParametresPersistance() {
        return forme.getParametresPersistance();
    }

    /**
     * Restaure l'état du décorateur ET de la forme concrète sous-jacente.
     * Sans cette surcharge, seul l'état du décorateur serait restauré,
     * laissant le nœud JavaFX de la forme concrète non mis à jour (ch.10).
     */
    @Override
    public void restaurer(Forme.Memento m) {
        super.restaurer(m);    // restaure x, y, couleur sur le décorateur
        forme.restaurer(m);    // propage sur la forme concrète → nœud JavaFX mis à jour
        dessiner();            // resynchronise les décorateurs appliqués
    }

    public Forme getForme() {
        return forme;
    }
}
