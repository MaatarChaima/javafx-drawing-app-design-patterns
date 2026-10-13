package masi.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton DatabaseConnection (ch.03).
 * Garantit une unique connexion JDBC vers la base SQLite.
 *
 * Problème résolu : éviter plusieurs connexions simultanées incohérentes.
 * Solution (ch.03 slide 2) : constructeur privé + getInstance() statique.
 */
public class DatabaseConnection {

    // ── Instance unique statique (ch.03 slide 4) ──────────────────────────
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private static final String URL = "jdbc:sqlite:dessin.db";
    private Connection connexion;

    // ── Constructeur privé ─────────────────────────────────────────────────
    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connexion = DriverManager.getConnection(URL);
            initialiserSchema();
            System.out.println("[DatabaseConnection] Connexion SQLite établie.");
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Erreur connexion : " + e.getMessage());
        }
    }

    // ── Point d'accès global (ch.03 slide 4) ──────────────────────────────
    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnexion() {
        return connexion;
    }

    // ── Initialisation du schéma SQLite ───────────────────────────────────
    private void initialiserSchema() throws SQLException {
        String sqlDessin =
            "CREATE TABLE IF NOT EXISTS DESSIN (" +
            "  id   INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  nom  TEXT NOT NULL," +
            "  date TEXT NOT NULL" +
            ");";

        String sqlForme =
            "CREATE TABLE IF NOT EXISTS FORME (" +
            "  id                INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  dessin_id         INTEGER NOT NULL," +
            "  type              TEXT NOT NULL," +
            "  x                 REAL," +
            "  y                 REAL," +
            "  x2                REAL," +
            "  y2                REAL," +
            "  largeur           REAL," +
            "  hauteur           REAL," +
            "  rayon             REAL," +
            "  couleur           TEXT," +
            "  couleur_bordure   TEXT," +
            "  epaisseur_bordure REAL DEFAULT 1.5," +
            "  decorateurs       TEXT DEFAULT ''," +
            "  FOREIGN KEY(dessin_id) REFERENCES DESSIN(id)" +
            ");";

        // Migration : ajouter les colonnes manquantes si la table existait déjà
        String[] migrations = {
            "ALTER TABLE FORME ADD COLUMN couleur_bordure   TEXT",
            "ALTER TABLE FORME ADD COLUMN epaisseur_bordure REAL DEFAULT 1.5",
            "ALTER TABLE FORME ADD COLUMN decorateurs       TEXT DEFAULT ''"
        };

        String sqlLog =
            "CREATE TABLE IF NOT EXISTS LOG (" +
            "  id      INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  date    TEXT NOT NULL," +
            "  message TEXT NOT NULL" +
            ");";

        try (Statement stmt = connexion.createStatement()) {
            stmt.execute(sqlDessin);
            stmt.execute(sqlForme);
            stmt.execute(sqlLog);
            // Appliquer les migrations (ignorées si la colonne existe déjà)
            for (String migration : migrations) {
                try { stmt.execute(migration); }
                catch (SQLException ignored) { /* colonne déjà présente */ }
            }
        }
    }

    public void fermer() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
                System.out.println("[DatabaseConnection] Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Erreur fermeture : " + e.getMessage());
        }
    }
}
