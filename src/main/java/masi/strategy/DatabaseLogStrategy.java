package masi.strategy;

import masi.singleton.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stratégie concrète : journalisation en base de données SQLite (ch.06).
 * Utilise le Singleton DatabaseConnection (ch.03) pour insérer dans LOG.
 */
public class DatabaseLogStrategy implements LogStrategy {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void log(String message) {
        String sql = "INSERT INTO LOG(date, message) VALUES(?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnexion();
        if (conn == null) {
            System.err.println("[DatabaseLogStrategy] Connexion indisponible, log ignoré : " + message);
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, LocalDateTime.now().format(FMT));
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DatabaseLogStrategy] Erreur insertion log : " + e.getMessage());
        }
    }
}
