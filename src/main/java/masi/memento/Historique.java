package masi.memento;

import masi.model.Forme;

import java.util.Stack;

/**
 * Historique — Caretaker du patron Memento (ch.10 slide 8).
 * Conserve une pile de Forme.Memento sans connaître leur contenu.
 * Supporte Undo (pile undo) et Redo (pile redo).
 *
 * Encapsulation respectée (ch.10) :
 * "Aucun getter/setter exposé, structure interne cachée."
 */
public class Historique {

    private final Stack<Forme.Memento> pileUndo = new Stack<>();
    private final Stack<Forme.Memento> pileRedo = new Stack<>();

    /**
     * Sauvegarde l'état courant de la forme avant modification.
     * Vide la pile redo (une nouvelle action invalide le redo).
     */
    public void sauvegarder(Forme f) {
        pileUndo.push(f.sauvegarder());   // ch.10 : Forme crée elle-même son Memento
        pileRedo.clear();
    }

    /**
     * Restaure le dernier état sauvegardé (Undo).
     * Retourne true si la restauration a eu lieu.
     */
    public boolean annuler(Forme f) {
        if (pileUndo.isEmpty()) return false;
        // Avant de restaurer, sauvegarder l'état actuel pour un éventuel Redo
        pileRedo.push(f.sauvegarder());
        Forme.Memento memento = pileUndo.pop();
        f.restaurer(memento);             // ch.10 : Forme restaure depuis son Memento
        return true;
    }

    /**
     * Rétablit la dernière action annulée (Redo).
     */
    public boolean retablir(Forme f) {
        if (pileRedo.isEmpty()) return false;
        pileUndo.push(f.sauvegarder());
        Forme.Memento memento = pileRedo.pop();
        f.restaurer(memento);
        return true;
    }

    public boolean peutAnnuler()  { return !pileUndo.isEmpty(); }
    public boolean peutRetablir() { return !pileRedo.isEmpty(); }

    public void vider() {
        pileUndo.clear();
        pileRedo.clear();
    }
}
