package masi.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Forme concrète : Rectangle.
 * Encapsule un nœud JavaFX javafx.scene.shape.Rectangle
 * (conforme à l'approche « Nœuds JavaFX » choisie).
 */
public class FormeRectangle extends Forme {

    private double largeur;
    private double hauteur;

    // Le nœud JavaFX correspondant à cette forme
    private Rectangle noeudJFX;

    // ── Constructeurs ─────────────────────────────────────────────────────
    public FormeRectangle() {
        super();
        this.largeur = 100;
        this.hauteur = 60;
        this.noeudJFX = new Rectangle();
        synchroniserNoeud();
    }

    public FormeRectangle(double x, double y, double largeur, double hauteur, Color couleur) {
        super(x, y, couleur);
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.noeudJFX = new Rectangle();
        synchroniserNoeud();
    }

    // ── Forme abstraite ────────────────────────────────────────────────────
    @Override
    public void dessiner() {
        synchroniserNoeud();
    }

    @Override
    public String getType() {
        return "RECTANGLE";
    }

    /**
     * Synchronise les propriétés du nœud JavaFX avec l'état métier.
     */
    private void synchroniserNoeud() {
        noeudJFX.setX(getX());
        noeudJFX.setY(getY());
        noeudJFX.setWidth(largeur);
        noeudJFX.setHeight(hauteur);
        noeudJFX.setFill(getCouleur());
        noeudJFX.setStroke(getCouleurBordure());
        noeudJFX.setStrokeWidth(getEpaisseurBordure());
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public double    getLargeur()              { return largeur; }
    public void      setLargeur(double l)      { this.largeur = l; synchroniserNoeud(); }

    public double    getHauteur()              { return hauteur; }
    public void      setHauteur(double h)      { this.hauteur = h; synchroniserNoeud(); }

    public Rectangle getNoeudJFX()             { return noeudJFX; }

    @Override
    public java.util.Map<String, Double> getParametresPersistance() {
        return java.util.Map.of("largeur", largeur, "hauteur", hauteur);
    }

    @Override
    public void setX(double x) { super.setX(x); if (noeudJFX != null) noeudJFX.setX(x); }

    @Override
    public void setY(double y) { super.setY(y); if (noeudJFX != null) noeudJFX.setY(y); }

    @Override
    public void setCouleur(Color c) { super.setCouleur(c); if (noeudJFX != null) noeudJFX.setFill(c); }
}
