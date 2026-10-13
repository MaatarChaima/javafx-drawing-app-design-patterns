package masi.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Forme concrète : Cercle.
 * Encapsule un nœud JavaFX javafx.scene.shape.Circle.
 */
public class FormeCercle extends Forme {

    private double rayon;
    private Circle noeudJFX;

    // ── Constructeurs ─────────────────────────────────────────────────────
    public FormeCercle() {
        super();
        this.rayon = 40;
        this.noeudJFX = new Circle();
        synchroniserNoeud();
    }

    public FormeCercle(double cx, double cy, double rayon, Color couleur) {
        super(cx, cy, couleur);
        this.rayon = rayon;
        this.noeudJFX = new Circle();
        synchroniserNoeud();
    }

    // ── Forme abstraite ────────────────────────────────────────────────────
    @Override
    public void dessiner() {
        synchroniserNoeud();
    }

    @Override
    public String getType() {
        return "CERCLE";
    }

    private void synchroniserNoeud() {
        // Pour Circle JavaFX : centerX/centerY représentent le centre
        noeudJFX.setCenterX(getX());
        noeudJFX.setCenterY(getY());
        noeudJFX.setRadius(rayon);
        noeudJFX.setFill(getCouleur());
        noeudJFX.setStroke(getCouleurBordure());
        noeudJFX.setStrokeWidth(getEpaisseurBordure());
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public double getRayon()           { return rayon; }
    public void   setRayon(double r)   { this.rayon = r; synchroniserNoeud(); }

    public Circle getNoeudJFX()        { return noeudJFX; }

    @Override
    public java.util.Map<String, Double> getParametresPersistance() {
        return java.util.Map.of("rayon", rayon);
    }

    @Override
    public void setX(double x) { super.setX(x); if (noeudJFX != null) noeudJFX.setCenterX(x); }

    @Override
    public void setY(double y) { super.setY(y); if (noeudJFX != null) noeudJFX.setCenterY(y); }

    @Override
    public void setCouleur(Color c) { super.setCouleur(c); if (noeudJFX != null) noeudJFX.setFill(c); }
}
