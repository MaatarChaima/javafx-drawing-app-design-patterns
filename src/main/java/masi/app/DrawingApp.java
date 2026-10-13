package masi.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import masi.singleton.DatabaseConnection;
import masi.singleton.Logger;

/**
 * Classe principale JavaFX (ch. JavaFX slide 4-5).
 * Hérite de Application et implémente start(Stage).
 */
public class DrawingApp extends Application {

    public static void main(String[] args) {
        // Initialise la connexion Singleton avant le lancement (ch.03)
        DatabaseConnection.getInstance();
        Logger.getInstance().log("Application démarrée.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chargement du fichier FXML (ch. JavaFX slide 32)
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/masi/view/main.fxml"));
        BorderPane root = loader.load();

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setTitle("Application de Dessin — MASI S2 2025/2026");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        // Fermeture propre de la connexion BD (Singleton ch.03)
        primaryStage.setOnCloseRequest(e -> {
            Logger.getInstance().log("Application fermée.");
            DatabaseConnection.getInstance().fermer();
        });

        primaryStage.show();
    }
}
