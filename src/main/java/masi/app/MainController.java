package masi.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import masi.command.*;
import masi.dao.FormeDAO;
import masi.dao.FormeDAOImpl;
import masi.factory.FormeFactoryProvider;
import masi.factory.FormeFactoryProvider.TypeForme;
import masi.graphe.*;
import masi.model.*;
import masi.singleton.Logger;
import masi.strategy.*;

import java.net.URL;
import java.util.*;

/**
 * MainController — Invoker du patron Command (ch.09) + Contrôleur FXML.
 *
 * Gère :
 *   • Les deux piles Undo/Redo (Command ch.09)
 *   • La sélection de forme dans la palette (Factory ch.08)
 *   • Le dessin à la souris (nœuds JavaFX)
 *   • La journalisation (Observer ch.05 + Strategy ch.06)
 *   • La persistance (DAO + Singleton ch.03)
 *   • Le calcul de PCC (Strategy ch.06 — partie optionnelle)
 */
public class MainController implements Initializable {

    // ── Composants FXML ────────────────────────────────────────────────────
    @FXML private Pane             zoneDessin;
    @FXML private ToggleButton     btnRectangle;
    @FXML private ToggleButton     btnCercle;
    @FXML private ToggleButton     btnLigne;
    @FXML private Button           btnUndo;
    @FXML private Button           btnRedo;
    @FXML private Button           btnSupprimer;
    @FXML private Button           btnSauvegarder;
    @FXML private Button           btnOuvrir;
    @FXML private Button           btnCalculerPCC;
    @FXML private ColorPicker      colorPicker;
    @FXML private ComboBox<String> cbLogStrategy;
    @FXML private ComboBox<String> cbAlgorithme;
    @FXML private ComboBox<String> cbNoeudDepart;
    @FXML private ComboBox<String> cbNoeudArrivee;
    @FXML private ComboBox<String> cbAreteSource;
    @FXML private ComboBox<String> cbAreteDest;
    @FXML private TextField        txtPoids;
    @FXML private TextField        txtNomDessin;
    @FXML private TextArea         txtLog;
    @FXML private Label            lblStatus;
    @FXML private ListView<String> listeDessins;
    @FXML private TextField        txtNomNoeud;
    @FXML private Button           btnAjouterNoeud;
    @FXML private Button           btnAjouterArete;
    @FXML private Button           btnViderGraphe;

    // ── Objets métier ──────────────────────────────────────────────────────
    private DrawingCanvas            canvas;
    private final Stack<Command>     pileUndo  = new Stack<>();
    private final Stack<Command>     pileRedo  = new Stack<>();
    private TypeForme                typeCourant = TypeForme.RECTANGLE;
    private double                   xDepart, yDepart;
    private boolean                  enTrain = false;

    // ── DAO + Graphe ───────────────────────────────────────────────────────
    private final FormeDAO      dao     = new FormeDAOImpl();
    private final GrapheContext graphe  = new GrapheContext();

    // ── Initialisation ────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        canvas = new DrawingCanvas(zoneDessin);

        // Enregistrer le Logger comme observateur (ch.05)
        canvas.addObs(Logger.getInstance());

        // Enregistrer un observateur IHM pour afficher les logs dans txtLog
        canvas.addObs(msg -> {
            if (txtLog != null) {
                txtLog.appendText(msg + "\n");
            }
        });

        // Stratégies de log disponibles (Strategy ch.06)
        if (cbLogStrategy != null) {
            cbLogStrategy.getItems().addAll("Console", "Fichier", "Base de données");
            cbLogStrategy.setValue("Console");
            cbLogStrategy.setOnAction(e -> changerStrategieLog());
        }

        // Algorithmes PCC
        if (cbAlgorithme != null) {
            cbAlgorithme.getItems().addAll("Dijkstra", "BFS");
            cbAlgorithme.setValue("Dijkstra");
        }

        // Initialiser ColorPicker
        if (colorPicker != null) colorPicker.setValue(Color.DODGERBLUE);

        // Grouper les ToggleButton en exclusion mutuelle (un seul actif à la fois)
        if (btnRectangle != null && btnCercle != null && btnLigne != null) {
            ToggleGroup groupe = new ToggleGroup();
            btnRectangle.setToggleGroup(groupe);
            btnCercle.setToggleGroup(groupe);
            btnLigne.setToggleGroup(groupe);
            btnRectangle.setSelected(true); // Rectangle sélectionné par défaut

            // Empêcher de décocher tous les boutons : au moins un doit rester sélectionné
            groupe.selectedToggleProperty().addListener((obs, ancienToggle, nouveauToggle) -> {
                if (nouveauToggle == null) ancienToggle.setSelected(true);
            });
        }

        // Dessin à la souris — on vérifie que le press vient bien du zoneDessin lui-même
        zoneDessin.setOnMousePressed(e -> {
            if (e.getTarget() == zoneDessin) {   // ignorer les clics sur des enfants (nœuds graphe)
                xDepart = e.getX();
                yDepart = e.getY();
                enTrain = true;
            }
        });

        zoneDessin.setOnMouseReleased(e -> {
            if (enTrain) {
                double x2 = e.getX();
                double y2 = e.getY();

                // Taille minimale : évite les formes invisibles sur simple clic (20px min)
                if (Math.abs(x2 - xDepart) < 10 && Math.abs(y2 - yDepart) < 10) {
                    x2 = xDepart + 60;
                    y2 = yDepart + 40;
                }

                Color c = colorPicker != null ? colorPicker.getValue() : Color.DODGERBLUE;
                Forme f = FormeFactoryProvider.creer(typeCourant,
                                                      xDepart, yDepart,
                                                      x2, y2, c);
                executer(new AjouterFormeCommand(canvas, f));
                enTrain = false;
                Logger.getInstance().log("Forme dessinée : " + typeCourant);
            }
        });

        // Annuler le dessin en cours si la souris quitte la zone
        zoneDessin.setOnMouseExited(e -> enTrain = false);

        rafraichirListeDessins();
        majBoutons();
    }

    // ── Sélection de forme dans la palette ────────────────────────────────
    @FXML private void selectionnerRectangle() {
        typeCourant = TypeForme.RECTANGLE;
        enTrain = false; // couper tout dessin en cours
        majStatut("Mode : Rectangle");
        Logger.getInstance().log("Sélection de forme : RECTANGLE");
    }

    @FXML private void selectionnerCercle() {
        typeCourant = TypeForme.CERCLE;
        enTrain = false;
        majStatut("Mode : Cercle");
        Logger.getInstance().log("Sélection de forme : CERCLE");
    }

    @FXML private void selectionnerLigne() {
        typeCourant = TypeForme.LIGNE;
        enTrain = false;
        majStatut("Mode : Ligne");
        Logger.getInstance().log("Sélection de forme : LIGNE");
    }

    // ── Invoker Command ────────────────────────────────────────────────────
    /**
     * Exécute une commande, pousse dans pileUndo, vide pileRedo (ch.09).
     */
    private void executer(Command cmd) {
        cmd.execute();
        pileUndo.push(cmd);
        pileRedo.clear();
        majBoutons();
    }

    /**
     * Undo (ch.09 + ch.10) — Option B : historique complet.
     */
    @FXML private void annuler() {
        if (!pileUndo.isEmpty()) {
            Command cmd = pileUndo.pop();
            cmd.undo();
            pileRedo.push(cmd);
            majBoutons();
            majStatut("Action annulée.");
            Logger.getInstance().log("Undo effectué.");
        }
    }

    /**
     * Redo.
     */
    @FXML private void retablir() {
        if (!pileRedo.isEmpty()) {
            Command cmd = pileRedo.pop();
            cmd.execute();
            pileUndo.push(cmd);
            majBoutons();
            majStatut("Action rétablie.");
            Logger.getInstance().log("Redo effectué.");
        }
    }

    /**
     * Effacer la forme sélectionnée (Option A).
     */
    @FXML private void effacerForme() {
        Forme f = canvas.getFormeSelectionnee();
        if (f != null) {
            executer(new SupprimerFormeCommand(canvas, f));
            majStatut("Forme supprimée.");
            Logger.getInstance().log("Suppression : " + f.getType());
        } else {
            majStatut("Aucune forme sélectionnée.");
        }
    }

    // ── Persistance ────────────────────────────────────────────────────────
    @FXML private void sauvegarderDessin() {
        String nom = txtNomDessin != null ? txtNomDessin.getText() : "Dessin";
        if (nom == null || nom.isBlank()) nom = "Dessin sans titre";
        Dessin d = new Dessin(nom);
        for (Forme f : canvas.getFormes()) d.ajouterForme(f);
        dao.sauvegarder(d);
        majStatut("Dessin \"" + nom + "\" sauvegardé (id=" + d.getId() + ").");
        Logger.getInstance().log("Dessin sauvegardé : " + nom);
        rafraichirListeDessins();
    }

    @FXML private void ouvrirDessin() {
        if (listeDessins == null) return;
        String sel = listeDessins.getSelectionModel().getSelectedItem();
        if (sel == null) { majStatut("Sélectionnez un dessin dans la liste."); return; }
        try {
            int id = Integer.parseInt(sel.split(" ")[0].trim());
            Dessin d = dao.charger(id);
            if (d != null) {
                // Vider le graphe avant de charger un nouveau dessin
                viderGraphe();
                canvas.chargerFormes(d.getFormes());
                pileUndo.clear(); pileRedo.clear(); majBoutons();
                majStatut("Dessin \"" + d.getNom() + "\" ouvert.");
                Logger.getInstance().log("Dessin ouvert : " + d.getNom());
            }
        } catch (NumberFormatException e) {
            majStatut("Erreur : impossible de lire l'identifiant du dessin.");
        }
    }

    private void rafraichirListeDessins() {
        if (listeDessins == null) return;
        listeDessins.getItems().clear();
        for (Dessin d : dao.listerTous()) {
            listeDessins.getItems().add(d.getId() + " — " + d.getNom());
        }
    }

    // ── Stratégie de journalisation (Strategy ch.06 — Usage A) ────────────
    private void changerStrategieLog() {
        if (cbLogStrategy == null) return;
        String choix = cbLogStrategy.getValue();
        switch (choix) {
            case "Console"        -> Logger.getInstance().setStrategy(new ConsoleLogStrategy());
            case "Fichier"        -> Logger.getInstance().setStrategy(new FichierLogStrategy());
            case "Base de données"-> Logger.getInstance().setStrategy(new DatabaseLogStrategy());
        }
        majStatut("Stratégie de log : " + choix);
    }

    // ── Graphe + PCC (Strategy ch.06 — partie optionnelle) ────────────────

    // Associe chaque nom de nœud graphe à son cercle visuel sur le canvas
    private final Map<String, FormeCercle>                  noeudsVisuels   = new LinkedHashMap<>();
    // Label de nom affiché par-dessus chaque cercle de nœud
    private final Map<String, javafx.scene.control.Label>   labelsNoeuds    = new LinkedHashMap<>();

    // Associe chaque arête à sa ligne visuelle : clé = "source→dest"
    private final Map<String, FormeLigne>                   aretesVisuelles = new LinkedHashMap<>();
    // Label de poids affiché au milieu de chaque arête
    private final Map<String, javafx.scene.control.Label>   labelsAretes    = new LinkedHashMap<>();

    @FXML private void ajouterNoeud() {
        if (txtNomNoeud == null) return;
        String nom = txtNomNoeud.getText().trim();
        if (nom.isEmpty()) return;
        if (noeudsVisuels.containsKey(nom)) {
            majStatut("Nœud \"" + nom + "\" existe déjà.");
            return;
        }

        graphe.ajouterNoeud(nom);

        // Positionner les nœuds en cercle pour éviter les chevauchements
        int index = noeudsVisuels.size();
        double angleRad = index * (2 * Math.PI / Math.max(8, index + 1));
        double cx = 400 + 150 * Math.cos(angleRad);
        double cy = 300 + 120 * Math.sin(angleRad);

        FormeCercle cercleNoeud = new FormeCercle(cx, cy, 22, Color.ORANGE);
        noeudsVisuels.put(nom, cercleNoeud);
        canvas.ajouterForme(cercleNoeud);

        // Afficher le nom du nœud via un Label JavaFX sur le canvas
        javafx.scene.control.Label lblNom = new javafx.scene.control.Label(nom);
        lblNom.setLayoutX(cx - 10);
        lblNom.setLayoutY(cy - 8);
        lblNom.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #333;");
        canvas.getPane().getChildren().add(lblNom);
        labelsNoeuds.put(nom, lblNom);   // sauvegarde la référence pour nettoyage

        majComboNoeuds();
        Logger.getInstance().log("Nœud ajouté : " + nom);
        txtNomNoeud.clear();
    }

    @FXML private void ajouterArete() {
        if (cbAreteSource == null || cbAreteDest == null || txtPoids == null) return;

        String source = cbAreteSource.getValue();
        String dest   = cbAreteDest.getValue();
        if (source == null || dest == null) {
            majStatut("Sélectionnez source et destination.");
            return;
        }
        if (source.equals(dest)) {
            majStatut("Source et destination doivent être différentes.");
            return;
        }

        // Lecture du poids (défaut = 1.0 si vide ou invalide)
        double poids = 1.0;
        String poidsText = txtPoids.getText().trim();
        if (!poidsText.isEmpty()) {
            try {
                poids = Double.parseDouble(poidsText);
                if (poids <= 0) { majStatut("Le poids doit être positif."); return; }
            } catch (NumberFormatException e) {
                majStatut("Poids invalide (nombre attendu).");
                return;
            }
        }

        // Éviter les arêtes en double (non orienté : A→B = B→A)
        String cleAB = source + "→" + dest;
        String cleBA = dest   + "→" + source;
        if (aretesVisuelles.containsKey(cleAB) || aretesVisuelles.containsKey(cleBA)) {
            majStatut("Arête " + source + " — " + dest + " existe déjà.");
            return;
        }

        graphe.ajouterArete(source, dest, poids);

        // Dessiner la ligne entre les deux cercles
        FormeCercle cercleS = noeudsVisuels.get(source);
        FormeCercle cercleD = noeudsVisuels.get(dest);
        if (cercleS != null && cercleD != null) {
            FormeLigne ligne = new FormeLigne(
                    cercleS.getX(), cercleS.getY(),
                    cercleD.getX(), cercleD.getY(),
                    Color.SLATEGRAY);
            ligne.setEpaisseurBordure(1.5);
            aretesVisuelles.put(cleAB, ligne);
            // Ajouter la ligne sous les cercles (insérer en position 0 du pane)
            ligne.dessiner();
            canvas.getPane().getChildren().add(0, ligne.getNoeudJFX());

            // Label du poids au milieu de l'arête
            double mx = (cercleS.getX() + cercleD.getX()) / 2;
            double my = (cercleS.getY() + cercleD.getY()) / 2;
            String poidsStr = (poids == Math.floor(poids)) ? String.valueOf((int) poids) : String.valueOf(poids);
            javafx.scene.control.Label lblPoids = new javafx.scene.control.Label(poidsStr);
            lblPoids.setLayoutX(mx + 3);
            lblPoids.setLayoutY(my - 14);
            lblPoids.setStyle("-fx-font-size: 10; -fx-text-fill: #555; -fx-background-color: rgba(255,255,255,0.7);");
            canvas.getPane().getChildren().add(lblPoids);
            labelsAretes.put(cleAB, lblPoids);   // sauvegarde la référence pour nettoyage
        }

        majStatut("Arête ajoutée : " + source + " — " + dest + " (poids=" + poids + ")");
        Logger.getInstance().log("Arête ajoutée : " + source + " → " + dest + " poids=" + poids);
        txtPoids.clear();
    }

    /** Vide complètement le graphe : modèle, visuels et Labels. */
    @FXML private void viderGraphe() {
        // Supprimer les Labels de poids d'arêtes
        labelsAretes.values().forEach(l -> canvas.getPane().getChildren().remove(l));
        labelsAretes.clear();

        // Supprimer les lignes d'arêtes (ajoutées directement dans le pane)
        aretesVisuelles.values().forEach(l -> canvas.getPane().getChildren().remove(l.getNoeudJFX()));
        aretesVisuelles.clear();

        // Supprimer les Labels de noms de nœuds
        labelsNoeuds.values().forEach(l -> canvas.getPane().getChildren().remove(l));
        labelsNoeuds.clear();

        // Supprimer les cercles de nœuds via canvas (propre, avec notification Observer)
        new ArrayList<>(noeudsVisuels.values()).forEach(c -> canvas.supprimerForme(c));
        noeudsVisuels.clear();

        graphe.vider();
        majComboNoeuds();
        majStatut("Graphe vidé.");
        Logger.getInstance().log("Graphe vidé.");
    }

    @FXML private void calculerPCC() {
        if (cbNoeudDepart == null || cbNoeudArrivee == null) return;
        String depart  = cbNoeudDepart.getValue();
        String arrivee = cbNoeudArrivee.getValue();
        if (depart == null || arrivee == null) { majStatut("Sélectionnez départ et arrivée."); return; }

        // Changer d'algorithme selon le choix (Strategy)
        if (cbAlgorithme != null) {
            String algo = cbAlgorithme.getValue();
            if ("BFS".equals(algo)) graphe.setAlgorithme(new BFSStrategy());
            else                    graphe.setAlgorithme(new DijkstraStrategy());
        }

        List<String> chemin = graphe.calculerPCC(depart, arrivee);
        String result = chemin.isEmpty()
                ? "Aucun chemin trouvé."
                : "Chemin : " + String.join(" → ", chemin);
        majStatut(result);
        Logger.getInstance().log("PCC calculé — " + result);
        if (txtLog != null) txtLog.appendText("[PCC] " + result + "\n");

        // Mettre en évidence les arêtes du chemin trouvé
        mettreEnEvidenceChemin(chemin);
    }

    /**
     * Colore en vert les arêtes qui font partie du chemin,
     * et remet en gris les autres.
     */
    private void mettreEnEvidenceChemin(List<String> chemin) {
        // Remettre toutes les arêtes en gris
        for (FormeLigne l : aretesVisuelles.values()) {
            l.setCouleur(Color.SLATEGRAY);
            l.setEpaisseurBordure(1.5);
            l.dessiner();
        }
        // Colorier les arêtes du chemin en vert vif
        for (int i = 0; i < chemin.size() - 1; i++) {
            String a = chemin.get(i);
            String b = chemin.get(i + 1);
            FormeLigne ligne = aretesVisuelles.get(a + "→" + b);
            if (ligne == null) ligne = aretesVisuelles.get(b + "→" + a);
            if (ligne != null) {
                ligne.setCouleur(Color.LIMEGREEN);
                ligne.setEpaisseurBordure(3.0);
                ligne.dessiner();
            }
        }
    }

    private void majComboNoeuds() {
        List<String> noeuds = new ArrayList<>(graphe.getNoeuds());
        if (cbNoeudDepart  != null) cbNoeudDepart.getItems().setAll(noeuds);
        if (cbNoeudArrivee != null) cbNoeudArrivee.getItems().setAll(noeuds);
        if (cbAreteSource  != null) cbAreteSource.getItems().setAll(noeuds);
        if (cbAreteDest    != null) cbAreteDest.getItems().setAll(noeuds);
    }

    // ── Utilitaires IHM ────────────────────────────────────────────────────
    private void majStatut(String msg) {
        if (lblStatus != null) lblStatus.setText(msg);
    }

    private void majBoutons() {
        if (btnUndo != null) btnUndo.setDisable(pileUndo.isEmpty());
        if (btnRedo != null) btnRedo.setDisable(pileRedo.isEmpty());
    }
}
