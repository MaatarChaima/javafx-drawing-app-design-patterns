package masi.command;

/**
 * Interface Command (ch.09 slide 3).
 * Chaque action de l'utilisateur est encapsulée en objet Command.
 * execute() réalise l'action, undo() l'annule.
 */
public interface Command {
    void execute();
    void undo();
}
