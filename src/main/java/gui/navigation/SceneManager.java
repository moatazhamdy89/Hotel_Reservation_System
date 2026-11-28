
package gui.navigation;

import gui.controller.auth.WelcomeController;
import gui.controller.auth.LoginController;
import gui.controller.auth.RegisterController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void switchScene(Parent root) {

        Scene scene = new Scene(root, 1050, 650);

        scene.getStylesheets().add(
                getClass().getResource(
                        "/css/style.css"
                ).toExternalForm()
        );

        stage.setScene(scene);
        stage.show();
    }

    public void switchToFXML(String fxmlPath) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );

            Parent root = loader.load();

            Object controller = loader.getController();

            // Welcome Controller
            if (controller instanceof WelcomeController) {
                ((WelcomeController) controller)
                        .setSceneManager(this);
            }

            // Login Controller
            if (controller instanceof LoginController) {
                ((LoginController) controller)
                        .setSceneManager(this);
            }

            // Register Controller
            if (controller instanceof RegisterController) {
                ((RegisterController) controller)
                        .setSceneManager(this);
            }

            switchScene(root);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load FXML: " + fxmlPath, e
            );
        }
    }
}
