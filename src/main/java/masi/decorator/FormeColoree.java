package masi.decorator;

import masi.model.Forme;
import javafx.scene.paint.Color;

/**
 * Décorateur concret : enrichit une forme avec une couleur personnalisée (ch.07).
 * Usage : new FormeColoree(rect, Color.RED)
 */
public class FormeColoree extends FormeDecorator {

    private Color nouvelleCouleur;

    public FormeColoree(Forme forme, Color nouvelleCouleur) {
        super(forme);
        this.nouvelleCouleur = nouvelleCouleur;
    }

    @Override
    public void dessiner() {
        // Applique la nouvelle couleur avant de déléguer
        forme.setCouleur(nouvelleCouleur);
        forme.dessiner();
    }

    public Color getNouvelleCouleur()               { return nouvelleCouleur; }
    public void  setNouvelleCouleur(Color couleur)  { this.nouvelleCouleur = couleur; }
}
