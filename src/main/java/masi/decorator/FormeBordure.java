package masi.decorator;

import masi.model.Forme;
import javafx.scene.paint.Color;

/**
 * Décorateur concret : enrichit une forme avec une bordure (ch.07).
 * Usage : new FormeBordure(new FormeColoree(rect, Color.RED), Color.BLACK, 3.0)
 */
public class FormeBordure extends FormeDecorator {

    private Color  couleurBordure;
    private double epaisseur;

    public FormeBordure(Forme forme, Color couleurBordure, double epaisseur) {
        super(forme);
        this.couleurBordure = couleurBordure;
        this.epaisseur      = epaisseur;
    }

    @Override
    public void dessiner() {
        // Applique la bordure avant de déléguer
        forme.setCouleurBordure(couleurBordure);
        forme.setEpaisseurBordure(epaisseur);
        forme.dessiner();
    }

    public Color  getCouleurBordure()                       { return couleurBordure; }
    public void   setCouleurBordure(Color c)                { this.couleurBordure = c; }
    public double getEpaisseur()                            { return epaisseur; }
    public void   setEpaisseur(double e)                    { this.epaisseur = e; }
}
