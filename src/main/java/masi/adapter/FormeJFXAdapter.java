package masi.adapter;

import masi.model.Forme;

/**
 * Adapter (ch.04 slide 3).
 * Traduit l'interface de Forme (Adaptee) vers FormeMetier (Target).
 * Le client (DrawingCanvas) ne connaît que FormeMetier.
 *
 * Rôles selon le cours ch.04 :
 *   Target  → FormeMetier
 *   Adaptee → Forme (modèle métier)
 *   Adapter → FormeJFXAdapter
 *   Client  → DrawingCanvas
 */
public class FormeJFXAdapter implements FormeMetier {

    // Référence vers l'Adaptee (ch.04 slide 4)
    private final Forme forme;

    public FormeJFXAdapter(Forme forme) {
        this.forme = forme;
    }

    // ── Délégation vers l'Adaptee ──────────────────────────────────────────
    @Override
    public void dessiner() {
        forme.dessiner();          // délègue à la méthode de Forme
    }

    @Override
    public double getX()         { return forme.getX(); }

    @Override
    public double getY()         { return forme.getY(); }

    @Override
    public void setX(double x)   { forme.setX(x); }

    @Override
    public void setY(double y)   { forme.setY(y); }

    @Override
    public String getType()      { return forme.getType(); }

    /**
     * Accès à l'Adaptee sous-jacent quand les DAO en ont besoin.
     */
    public Forme getForme()      { return forme; }
}
