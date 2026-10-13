package masi.command;

import masi.app.DrawingCanvas;
import masi.model.Forme;

/**
 * Commande concrète : suppression d'une forme (ch.09).
 *   execute() → retire la forme du canvas (Receiver)
 *   undo()    → remet la forme dans le canvas
 */
public class SupprimerFormeCommand implements Command {

    private final DrawingCanvas canvas;
    private final Forme         forme;

    public SupprimerFormeCommand(DrawingCanvas canvas, Forme forme) {
        this.canvas = canvas;
        this.forme  = forme;
    }

    @Override
    public void execute() {
        canvas.supprimerForme(forme);
    }

    @Override
    public void undo() {
        canvas.ajouterForme(forme);
    }

    public Forme getForme() { return forme; }
}
