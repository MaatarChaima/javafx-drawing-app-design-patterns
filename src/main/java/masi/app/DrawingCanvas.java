package masi.app;

import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.shape.*;
import masi.adapter.FormeJFXAdapter;
import masi.adapter.FormeMetier;
import masi.decorator.FormeDecorator;
import masi.memento.Historique;
import masi.model.*;
import masi.observer.Observable;
import masi.observer.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * DrawingCanvas — zone de dessin JavaFX.
 *
 * Trois rôles selon les patrons enseignés :
 *   • Receiver        du patron Command  (ch.09) : ajouterForme / supprimerForme
 *   • ConcreteSubject du patron Observer (ch.05) : notifie Logger et l'IHM
 *   • Client          du patron Adapter  (ch.04) : manipule les formes via
 *                     FormeMetier (Target) grâce à FormeJFXAdapter
 *
 * Intègre également Historique (Caretaker Memento ch.10).
 */
public class DrawingCanvas implements Observable {

    // ── Pane JavaFX (zone de dessin) ──────────────────────────────────────
    private final Pane pane;

    // ── Formes métier ──────────────────────────────────────────────────────
    private final List<Forme> formes = new ArrayList<>();

    // ── Observateurs (ch.05) ──────────────────────────────────────────────
    private final List<Observer> observateurs = new ArrayList<>();

    // ── Forme sélectionnée ────────────────────────────────────────────────
    private Forme formeSelectionnee = null;

    // ── Caretaker Memento (ch.10) ─────────────────────────────────────────
    private final Historique historique = new Historique();

    // ── Constructeur ──────────────────────────────────────────────────────
    public DrawingCanvas(Pane pane) {
        this.pane = pane;
        pane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");
    }

    // ── Receiver Command : ajout ───────────────────────────────────────────
    /**
     * Ajoute une forme au canvas via l'Adapter (ch.04) et notifie les
     * observateurs (ch.05 + ch.09).
     * FormeMetier est la Target ; FormeJFXAdapter traduit vers Forme (Adaptee).
     */
    public void ajouterForme(Forme f) {
        formes.add(f);
        // Patron Adapter (ch.04) : utiliser FormeMetier comme contrat de dessin
        FormeMetier adaptee = new FormeJFXAdapter(f);
        adaptee.dessiner();
        Node noeud = obtenirNoeudJFX(f);
        if (noeud != null) {
            configurerSelectionSouris(noeud, f);
            pane.getChildren().add(noeud);
        }
        notifyAllObs("Forme ajoutée : " + adaptee.getType()
                     + " [x=" + adaptee.getX() + ", y=" + adaptee.getY() + "]");
    }

    // ── Receiver Command : suppression ────────────────────────────────────
    /**
     * Retire une forme du canvas via l'Adapter (ch.04).
     */
    public void supprimerForme(Forme f) {
        formes.remove(f);
        Node noeud = obtenirNoeudJFX(f);
        if (noeud != null) pane.getChildren().remove(noeud);
        if (formeSelectionnee == f) formeSelectionnee = null;
        // Adapter pour accéder au type sans dépendre de la classe concrète
        FormeMetier adaptee = new FormeJFXAdapter(f);
        notifyAllObs("Forme supprimée : " + adaptee.getType());
    }

    /**
     * Supprime la forme actuellement sélectionnée.
     */
    public void supprimerFormeSelectionnee() {
        if (formeSelectionnee != null) {
            supprimerForme(formeSelectionnee);
        }
    }

    /**
     * Vide le canvas.
     */
    public void vider() {
        pane.getChildren().clear();
        formes.clear();
        formeSelectionnee = null;
        notifyAllObs("Canvas vidé.");
    }

    /**
     * Recharge des formes depuis la base (ouverture d'un dessin).
     */
    public void chargerFormes(List<Forme> nouvellesFormes) {
        vider();
        for (Forme f : nouvellesFormes) {
            ajouterForme(f);
        }
        notifyAllObs("Dessin chargé : " + nouvellesFormes.size() + " forme(s).");
    }

    // ── Sélection souris ───────────────────────────────────────────────────
    private void configurerSelectionSouris(Node noeud, Forme f) {
        noeud.setOnMouseClicked(e -> {
            // Réinitialiser la mise en évidence de l'ancienne sélection
            if (formeSelectionnee != null) {
                reinitialiserSurlignage(formeSelectionnee);
            }
            formeSelectionnee = f;
            surligner(f);
            // Adapter (ch.04) : accès au type via l'interface Target
            FormeMetier adaptee = new FormeJFXAdapter(f);
            notifyAllObs("Forme sélectionnée : " + adaptee.getType());
            e.consume();
        });
    }

    private void surligner(Forme f) {
        Node n = obtenirNoeudJFX(f);
        if (n != null) n.setStyle("-fx-effect: dropshadow(gaussian, #ff6600, 8, 0.5, 0, 0);");
    }

    private void reinitialiserSurlignage(Forme f) {
        Node n = obtenirNoeudJFX(f);
        if (n != null) n.setStyle("");
    }

    // ── Utilitaire : nœud JavaFX d'une Forme ──────────────────────────────
    /**
     * Remonte la chaîne de décorateurs jusqu'à trouver la forme concrète,
     * puis retourne son nœud JavaFX. Corrige le NPE sur les décorateurs (ch.07).
     */
    private Node obtenirNoeudJFX(Forme f) {
        // Dérouler les décorateurs pour atteindre la forme concrète
        Forme base = f;
        while (base instanceof FormeDecorator d) {
            base = d.getForme();
        }
        if (base instanceof FormeRectangle r) return r.getNoeudJFX();
        if (base instanceof FormeCercle   c) return c.getNoeudJFX();
        if (base instanceof FormeLigne    l) return l.getNoeudJFX();
        return null;
    }

    // ── Observable (ch.05) ────────────────────────────────────────────────
    @Override
    public void addObs(Observer o) {
        observateurs.add(o);
    }

    @Override
    public void removeObs(Observer o) {
        observateurs.remove(o);
    }

    @Override
    public void notifyAllObs(String message) {
        for (Observer o : observateurs) {
            o.update(message);
        }
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public List<Forme>  getFormes()                { return formes; }
    public Forme        getFormeSelectionnee()     { return formeSelectionnee; }
    public Pane         getPane()                  { return pane; }

    /**
     * Retourne le Caretaker Memento pour que le contrôleur puisse
     * déclencher undo/redo sur la forme sélectionnée (ch.10).
     */
    public Historique   getHistorique()            { return historique; }

    /**
     * Sauvegarde l'état de la forme sélectionnée avant de la modifier.
     * Appelé depuis le contrôleur avant toute opération de déplacement/couleur.
     */
    public void sauvegarderEtatFormeSelectionnee() {
        if (formeSelectionnee != null) {
            historique.sauvegarder(formeSelectionnee);
        }
    }
}
