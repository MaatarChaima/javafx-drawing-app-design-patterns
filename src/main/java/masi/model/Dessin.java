package masi.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Dessin : identifié par un id, un nom et une date.
 * Agrège une liste de Forme (cardinalité 1 Dessin — * Forme).
 */
public class Dessin {

    private int           id;
    private String        nom;
    private LocalDateTime date;
    private List<Forme>   formes;

    // ── Constructeurs ─────────────────────────────────────────────────────
    public Dessin() {
        this.formes = new ArrayList<>();
        this.date   = LocalDateTime.now();
        this.nom    = "Nouveau dessin";
    }

    public Dessin(String nom) {
        this.nom    = nom;
        this.formes = new ArrayList<>();
        this.date   = LocalDateTime.now();
    }

    // ── Méthodes ──────────────────────────────────────────────────────────
    public void ajouterForme(Forme f)    { formes.add(f); }
    public void supprimerForme(Forme f)  { formes.remove(f); }
    public List<Forme> getFormes()       { return formes; }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public int           getId()                    { return id; }
    public void          setId(int id)              { this.id = id; }

    public String        getNom()                   { return nom; }
    public void          setNom(String nom)         { this.nom = nom; }

    public LocalDateTime getDate()                  { return date; }
    public void          setDate(LocalDateTime d)   { this.date = d; }

    @Override
    public String toString() {
        return "Dessin[" + id + "] \"" + nom + "\" — " + formes.size() + " forme(s)";
    }
}
