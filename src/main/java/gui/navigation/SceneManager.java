
package gui.navigation;

import com.hotel.model.Guest;

import gui.controller.auth.WelcomeController;
import gui.controller.auth.LoginController;
import gui.controller.auth.RegisterController;
import gui.controller.guest.GuestMainController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private final Stage stage;

    private Guest currentGuest;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public Guest getCurrentGuest() {
        return currentGuest;
    }

    public void openGuestHome(Guest guest) {

        this.currentGuest = guest;

        switchToFXML("/fxml/guest/guest-main.fxml");
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

            URL location = getClass().getResource(fxmlPath);

            if (location == null) {
                throw new IOException(
                        "FXML file not found: " + fxmlPath
                );
            }

            FXMLLoader loader = new FXMLLoader(location);

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

            // Guest Main Controller
            if (controller instanceof GuestMainController) {

                GuestMainController guestController =
                        (GuestMainController) controller;

                guestController.setSceneManager(this);

                guestController.setGuest(currentGuest);
            }

            switchScene(root);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load FXML: " + fxmlPath, e
            );
        }
    }
}
