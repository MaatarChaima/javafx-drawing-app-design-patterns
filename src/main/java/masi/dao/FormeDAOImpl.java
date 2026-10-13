package masi.dao;

import masi.decorator.FormeBordure;
import masi.decorator.FormeColoree;
import masi.decorator.FormeDecorator;
import masi.model.*;
import masi.singleton.DatabaseConnection;
import javafx.scene.paint.Color;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC de FormeDAO — utilise le Singleton DatabaseConnection (ch.03).
 *
 * Corrections appliquées :
 *  - Les colonnes couleur_bordure, epaisseur_bordure et decorateurs sont
 *    persistées afin de reconstruire fidèlement les formes décorées (ch.07).
 *  - Tous les ResultSet sont fermés via try-with-resources.
 *  - La sérialisation de couleur utilise un format #RRGGBBAA interprétable
 *    de façon fiable par Color.web().
 */
public class FormeDAOImpl implements FormeDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnexion();
    }

    // ── Sauvegarder un dessin complet ──────────────────────────────────────
    @Override
    public void sauvegarder(Dessin dessin) {
        String sqlD = "INSERT INTO DESSIN(nom, date) VALUES(?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sqlD, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dessin.getNom());
            ps.setString(2, dessin.getDate().toString());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idDessin = rs.getInt(1);
                    dessin.setId(idDessin);
                    for (Forme f : dessin.getFormes()) {
                        sauvegarderForme(f, idDessin);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erreur sauvegarder dessin : " + e.getMessage());
        }
    }

    private void sauvegarderForme(Forme f, int dessinId) throws SQLException {
        // Extraire la forme de base en remontant la chaîne de décorateurs
        Forme base = extraireBase(f);

        // Encoder les décorateurs appliqués sous forme de chaîne
        String decorateurs = encoderDecorateurs(f);

        // Récupérer les paramètres géométriques via la méthode polymorphe (Open-Closed)
        java.util.Map<String, Double> params = base.getParametresPersistance();

        String sql = "INSERT INTO FORME(dessin_id,type,x,y,x2,y2,largeur,hauteur,rayon," +
                     "couleur,couleur_bordure,epaisseur_bordure,decorateurs)" +
                     " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, dessinId);
            ps.setString(2, base.getType());
            ps.setDouble(3, base.getX());
            ps.setDouble(4, base.getY());
            setDoubleOrNull(ps, 5, params.get("x2"));
            setDoubleOrNull(ps, 6, params.get("y2"));
            setDoubleOrNull(ps, 7, params.get("largeur"));
            setDoubleOrNull(ps, 8, params.get("hauteur"));
            setDoubleOrNull(ps, 9, params.get("rayon"));
            ps.setString(10, couleurVersString(base.getCouleur()));
            ps.setString(11, couleurVersString(base.getCouleurBordure()));
            ps.setDouble(12, base.getEpaisseurBordure());
            ps.setString(13, decorateurs);
            ps.executeUpdate();
        }
    }

    /** Helper : insère un Double nullable dans un PreparedStatement. */
    private void setDoubleOrNull(PreparedStatement ps, int idx, Double val) throws SQLException {
        if (val != null) ps.setDouble(idx, val);
        else             ps.setNull(idx, Types.REAL);
    }

    // ── Charger un dessin par id ───────────────────────────────────────────
    @Override
    public Dessin charger(int id) {
        String sqlD = "SELECT * FROM DESSIN WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sqlD)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Dessin d = new Dessin(rs.getString("nom"));
                    d.setId(rs.getInt("id"));
                    d.setDate(LocalDateTime.parse(rs.getString("date")));
                    chargerFormes(d);
                    return d;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erreur charger dessin : " + e.getMessage());
        }
        return null;
    }

    private void chargerFormes(Dessin d) throws SQLException {
        String sql = "SELECT * FROM FORME WHERE dessin_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, d.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Forme f = construireForme(rs);
                    if (f != null) d.ajouterForme(f);
                }
            }
        }
    }

    private Forme construireForme(ResultSet rs) throws SQLException {
        String type    = rs.getString("type");
        double x       = rs.getDouble("x");
        double y       = rs.getDouble("y");
        Color  couleur = stringVersCouleur(rs.getString("couleur"));

        // Reconstruire la forme de base
        Forme base;
        switch (type) {
            case "RECTANGLE":
                base = new FormeRectangle(x, y, rs.getDouble("largeur"), rs.getDouble("hauteur"), couleur);
                break;
            case "CERCLE":
                base = new FormeCercle(x, y, rs.getDouble("rayon"), couleur);
                break;
            case "LIGNE":
                base = new FormeLigne(x, y, rs.getDouble("x2"), rs.getDouble("y2"), couleur);
                break;
            default:
                return null;
        }

        // Restaurer les propriétés de bordure directement sur la base
        Color couleurBordure = stringVersCouleur(rs.getString("couleur_bordure"));
        double epaisseur = rs.getDouble("epaisseur_bordure");
        if (epaisseur > 0) {
            base.setCouleurBordure(couleurBordure);
            base.setEpaisseurBordure(epaisseur);
        }

        // Reconstruire la chaîne de décorateurs
        String decorateurs = rs.getString("decorateurs");
        return reappliquerDecorateurs(base, decorateurs);
    }

    // ── Lister tous les dessins ────────────────────────────────────────────
    @Override
    public List<Dessin> listerTous() {
        List<Dessin> liste = new ArrayList<>();
        String sql = "SELECT id, nom, date FROM DESSIN ORDER BY id DESC";
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Dessin d = new Dessin(rs.getString("nom"));
                d.setId(rs.getInt("id"));
                d.setDate(LocalDateTime.parse(rs.getString("date")));
                liste.add(d);
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erreur lister dessins : " + e.getMessage());
        }
        return liste;
    }

    // ── Supprimer un dessin ────────────────────────────────────────────────
    @Override
    public void supprimer(int id) {
        try {
            try (PreparedStatement ps = conn().prepareStatement(
                    "DELETE FROM FORME WHERE dessin_id = ?")) {
                ps.setInt(1, id); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn().prepareStatement(
                    "DELETE FROM DESSIN WHERE id = ?")) {
                ps.setInt(1, id); ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erreur supprimer dessin : " + e.getMessage());
        }
    }

    // ── Utilitaires décorateurs ────────────────────────────────────────────

    /**
     * Remonte la chaîne de décorateurs et retourne la forme de base concrète.
     */
    private Forme extraireBase(Forme f) {
        while (f instanceof FormeDecorator d) {
            f = d.getForme();
        }
        return f;
    }

    /**
     * Encode la liste des décorateurs appliqués sous forme de chaîne.
     * Format : "COLOREE:#rrggbbaa|BORDURE:#rrggbbaa:epaisseur"
     * L'ordre reflète l'ordre d'application (de l'intérieur vers l'extérieur).
     */
    private String encoderDecorateurs(Forme f) {
        // Collecter la chaîne de décorateurs (du plus externe vers le plus interne)
        List<String> couches = new ArrayList<>();
        Forme courant = f;
        while (courant instanceof FormeDecorator d) {
            if (d instanceof FormeColoree fc) {
                couches.add(0, "COLOREE:" + couleurVersString(fc.getNouvelleCouleur()));
            } else if (d instanceof FormeBordure fb) {
                couches.add(0, "BORDURE:" + couleurVersString(fb.getCouleurBordure())
                               + ":" + fb.getEpaisseur());
            }
            courant = d.getForme();
        }
        return String.join("|", couches);
    }

    /**
     * Recrée la chaîne de décorateurs sur une forme de base à partir de la
     * chaîne encodée.
     */
    private Forme reappliquerDecorateurs(Forme base, String encoded) {
        if (encoded == null || encoded.isBlank()) return base;
        Forme result = base;
        for (String token : encoded.split("\\|")) {
            if (token.startsWith("COLOREE:")) {
                Color c = stringVersCouleur(token.substring(8));
                result = new FormeColoree(result, c);
            } else if (token.startsWith("BORDURE:")) {
                String[] parts = token.split(":");
                // parts[0]=BORDURE, parts[1]=couleur hex, parts[2]=epaisseur
                if (parts.length == 3) {
                    Color c = stringVersCouleur(parts[1]);
                    double ep = Double.parseDouble(parts[2]);
                    result = new FormeBordure(result, c, ep);
                }
            }
        }
        return result;
    }

    // ── Utilitaires couleur ────────────────────────────────────────────────

    /**
     * Sérialise une couleur en format #RRGGBBAA, fiablement relu par Color.web().
     */
    private String couleurVersString(Color c) {
        if (c == null) return "#3399ffff";
        return String.format("#%02x%02x%02x%02x",
                (int) Math.round(c.getRed()     * 255),
                (int) Math.round(c.getGreen()   * 255),
                (int) Math.round(c.getBlue()    * 255),
                (int) Math.round(c.getOpacity() * 255));
    }

    private Color stringVersCouleur(String s) {
        if (s == null || s.isBlank()) return Color.DODGERBLUE;
        try { return Color.web(s); }
        catch (Exception e) { return Color.DODGERBLUE; }
    }
}
