package masi.dao;

import masi.model.Dessin;
import java.util.List;

/**
 * Interface DAO pour la persistance des dessins (Target de l'Adapter DAO).
 */
public interface FormeDAO {
    void         sauvegarder(Dessin dessin);
    Dessin       charger(int id);
    List<Dessin> listerTous();
    void         supprimer(int id);
}
