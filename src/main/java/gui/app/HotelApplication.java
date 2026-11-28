
package gui.app;

import gui.navigation.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class HotelApplication extends Application {

    @Override
    public void start(Stage stage) {

        SceneManager sceneManager = new SceneManager(stage);

        stage.setTitle("Elite Hotel Reservation System");

        sceneManager.switchToFXML("/fxml/auth/welcome.fxml");    }

    public static void main(String[] args) {
        launch(args);
    }
}
