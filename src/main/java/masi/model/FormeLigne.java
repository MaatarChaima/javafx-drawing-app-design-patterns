package masi.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Forme concrète : Ligne.
 * Encapsule un nœud JavaFX javafx.scene.shape.Line.
 * x,y représentent le point de départ ; x2,y2 le point d'arrivée.
 */
public class FormeLigne extends Forme {

    private double x2;
    private double y2;
    private Line   noeudJFX;

    // ── Constructeurs ─────────────────────────────────────────────────────
    public FormeLigne() {
        super();
        this.x2 = 100;
        this.y2 = 100;
        this.noeudJFX = new Line();
        synchroniserNoeud();
    }

    public FormeLigne(double x1, double y1, double x2, double y2, Color couleur) {
        super(x1, y1, couleur);
        this.x2 = x2;
        this.y2 = y2;
        this.noeudJFX = new Line();
        synchroniserNoeud();
    }

    // ── Forme abstraite ────────────────────────────────────────────────────
    @Override
    public void dessiner() {
        synchroniserNoeud();
    }

    @Override
    public String getType() {
        return "LIGNE";
    }

    private void synchroniserNoeud() {
        noeudJFX.setStartX(getX());
        noeudJFX.setStartY(getY());
        noeudJFX.setEndX(x2);
        noeudJFX.setEndY(y2);
        // Pour une ligne : pas de fill, seulement le stroke
        noeudJFX.setStroke(getCouleur());
        noeudJFX.setStrokeWidth(getEpaisseurBordure() + 1);
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public double getX2()              { return x2; }
    public void   setX2(double x2)     { this.x2 = x2; synchroniserNoeud(); }

    public double getY2()              { return y2; }
    public void   setY2(double y2)     { this.y2 = y2; synchroniserNoeud(); }

    public Line   getNoeudJFX()        { return noeudJFX; }

    @Override
    public java.util.Map<String, Double> getParametresPersistance() {
        return java.util.Map.of("x2", x2, "y2", y2);
    }

    @Override
    public void setX(double x) { super.setX(x); if (noeudJFX != null) noeudJFX.setStartX(x); }

    @Override
    public void setY(double y) { super.setY(y); if (noeudJFX != null) noeudJFX.setStartY(y); }

    @Override
    public void setCouleur(Color c) { super.setCouleur(c); if (noeudJFX != null) noeudJFX.setStroke(c); }
}
