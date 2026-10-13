package masi.command;

import masi.app.DrawingCanvas;
import masi.model.Forme;

/**
 * Commande concrète : ajout d'une forme (ch.09).
 *   execute() → ajoute la forme au canvas (Receiver)
 *   undo()    → retire la forme du canvas
 *
 * Le Memento est géré par Historique ; ici Command gère l'action structurelle.
 */
public class AjouterFormeCommand implements Command {

    private final DrawingCanvas canvas;   // Receiver
    private final Forme         forme;    // Forme à ajouter

    public AjouterFormeCommand(DrawingCanvas canvas, Forme forme) {
        this.canvas = canvas;
        this.forme  = forme;
    }

    @Override
    public void execute() {
        canvas.ajouterForme(forme);
    }

    @Override
    public void undo() {
        canvas.supprimerForme(forme);
    }

    public Forme getForme() { return forme; }
}
