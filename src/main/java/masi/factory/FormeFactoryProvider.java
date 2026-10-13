package masi.factory;

import masi.model.Forme;
import javafx.scene.paint.Color;

/**
 * Point d'entrée public du package factory.
 * Le MainController appelle cette classe pour obtenir la factory correspondant
 * au type sélectionné dans la palette, sans connaître les classes concrètes.
 *
 * Conforme au principe Factory (ch.08 slide 3) :
 * "Le client ne crée pas directement les objets via new."
 */
public class FormeFactoryProvider {

    // Types de formes disponibles dans la palette
    public enum TypeForme { RECTANGLE, CERCLE, LIGNE }

    /**
     * Retourne la factory adaptée au type demandé.
     * Paramètres de dessin passés selon le type.
     */
    public static FormeFactory getFactory(TypeForme type,
                                          double x,  double y,
                                          double x2, double y2,
                                          Color  couleur) {
        switch (type) {
            case RECTANGLE:
                double largeur = Math.abs(x2 - x);
                double hauteur = Math.abs(y2 - y);
                return new RectangleFactory(Math.min(x, x2), Math.min(y, y2),
                                            largeur, hauteur, couleur);
            case CERCLE:
                double rayon = Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2)) / 2;
                return new CercleFactory(x, y, rayon, couleur);
            case LIGNE:
                return new LigneFactory(x, y, x2, y2, couleur);
            default:
                throw new IllegalArgumentException("Type de forme inconnu : " + type);
        }
    }

    /**
     * Crée directement la forme en une seule instruction.
     * Usage : FormeFactoryProvider.creer(RECTANGLE, x, y, x2, y2, color)
     */
    public static Forme creer(TypeForme type,
                               double x,  double y,
                               double x2, double y2,
                               Color  couleur) {
        return getFactory(type, x, y, x2, y2, couleur).creerForme();
    }
}
