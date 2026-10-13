package masi.model;

import javafx.scene.paint.Color;

/**
 * Classe abstraite Forme — entité métier de base.
 * Joue le rôle d'Originator dans le patron Memento (ch.10).
 * Contient la classe statique interne Memento dont le constructeur
 * est privé : seule Forme peut créer un Memento (encapsulation respectée).
 */
public abstract class Forme {

    // ── Attributs privés (état interne) ──────────────────────────────────
    private double x;
    private double y;
    private Color couleur;
    private double epaisseurBordure;
    private Color couleurBordure;

    // ── Constructeurs ─────────────────────────────────────────────────────
    public Forme() {
        this.x = 0;
        this.y = 0;
        this.couleur = Color.DODGERBLUE;
        this.epaisseurBordure = 1.5;
        this.couleurBordure = Color.DARKBLUE;
    }

    public Forme(double x, double y, Color couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
        this.epaisseurBordure = 1.5;
        this.couleurBordure = Color.DARKBLUE;
    }

    // ── Méthode abstraite ─────────────────────────────────────────────────
    /**
     * Chaque sous-classe implémente son propre dessin JavaFX.
     */
    public abstract void dessiner();

    /**
     * Retourne le nom du type de forme (pour la BD et les logs).
     */
    public abstract String getType();

    /**
     * Retourne les paramètres géométriques spécifiques à persister en base.
     * Clé → valeur : "largeur", "hauteur", "rayon", "x2", "y2".
     * Les sous-classes n'exposent que les paramètres qui les concernent.
     * Élimine le besoin de instanceof dans le DAO (Open-Closed Principle).
     */
    public abstract java.util.Map<String, Double> getParametresPersistance();

    // ── Patron Memento (ch.10) ────────────────────────────────────────────
    /**
     * Crée un Memento capturant l'état courant de cette Forme.
     * Seule Forme peut appeler le constructeur privé de Memento.
     */
    public Memento sauvegarder() {
        return new Memento(x, y, couleur, epaisseurBordure, couleurBordure);
    }

    /**
     * Restaure l'état de cette Forme à partir d'un Memento.
     * Aucun getter de Memento n'est exposé à l'extérieur.
     */
    public void restaurer(Memento m) {
        this.x = m.x;
        this.y = m.y;
        this.couleur = m.couleur;
        this.epaisseurBordure = m.epaisseurBordure;
        this.couleurBordure = m.couleurBordure;
    }

    // ── Classe statique interne Memento ───────────────────────────────────
    /**
     * Memento : snapshot immuable de l'état d'une Forme.
     * Constructeur privé → seule la classe Forme peut en créer.
     * Historique (Caretaker) stocke des Memento sans connaître leur contenu.
     */
    public static class Memento {
        private final double x;
        private final double y;
        private final Color couleur;
        private final double epaisseurBordure;
        private final Color couleurBordure;

        // Constructeur privé : accès réservé à Forme
        private Memento(double x, double y, Color couleur,
                        double epaisseurBordure, Color couleurBordure) {
            this.x = x;
            this.y = y;
            this.couleur = couleur;
            this.epaisseurBordure = epaisseurBordure;
            this.couleurBordure = couleurBordure;
        }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public double getX()                      { return x; }
    public void   setX(double x)              { this.x = x; }

    public double getY()                      { return y; }
    public void   setY(double y)              { this.y = y; }

    public Color  getCouleur()                { return couleur; }
    public void   setCouleur(Color couleur)   { this.couleur = couleur; }

    public double getEpaisseurBordure()                       { return epaisseurBordure; }
    public void   setEpaisseurBordure(double epaisseurBordure){ this.epaisseurBordure = epaisseurBordure; }

    public Color  getCouleurBordure()                         { return couleurBordure; }
    public void   setCouleurBordure(Color couleurBordure)     { this.couleurBordure = couleurBordure; }

    @Override
    public String toString() {
        return getType() + " [x=" + x + ", y=" + y + "]";
    }
}
