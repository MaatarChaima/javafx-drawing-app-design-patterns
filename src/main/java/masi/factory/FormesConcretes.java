package masi.factory;

import masi.model.Forme;
import masi.model.FormeRectangle;
import masi.model.FormeCercle;
import masi.model.FormeLigne;
import javafx.scene.paint.Color;

/**
 * Creator concret : crée un Rectangle (ch.08).
 */
class RectangleFactory extends FormeFactory {
    private final double x, y, largeur, hauteur;
    private final Color  couleur;

    public RectangleFactory(double x, double y, double largeur, double hauteur, Color couleur) {
        this.x       = x;
        this.y       = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.couleur = couleur;
    }

    @Override
    public Forme creerForme() {
        return new FormeRectangle(x, y, largeur, hauteur, couleur);
    }
}

/**
 * Creator concret : crée un Cercle (ch.08).
 */
class CercleFactory extends FormeFactory {
    private final double cx, cy, rayon;
    private final Color  couleur;

    public CercleFactory(double cx, double cy, double rayon, Color couleur) {
        this.cx     = cx;
        this.cy     = cy;
        this.rayon  = rayon;
        this.couleur = couleur;
    }

    @Override
    public Forme creerForme() {
        return new FormeCercle(cx, cy, rayon, couleur);
    }
}

/**
 * Creator concret : crée une Ligne (ch.08).
 */
class LigneFactory extends FormeFactory {
    private final double x1, y1, x2, y2;
    private final Color  couleur;

    public LigneFactory(double x1, double y1, double x2, double y2, Color couleur) {
        this.x1     = x1;
        this.y1     = y1;
        this.x2     = x2;
        this.y2     = y2;
        this.couleur = couleur;
    }

    @Override
    public Forme creerForme() {
        return new FormeLigne(x1, y1, x2, y2, couleur);
    }
}
