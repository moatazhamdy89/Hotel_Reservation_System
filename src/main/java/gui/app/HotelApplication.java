
package gui.app;

import gui.navigation.SceneManager;
import gui.view.auth.WelcomeView;

import javafx.application.Application;
import javafx.stage.Stage;

public class HotelApplication extends Application {

    @Override
    public void start(Stage stage) {

        SceneManager sceneManager = new SceneManager(stage);

        WelcomeView welcomeView = new WelcomeView();

        sceneManager.switchToFXML("/fxml/auth/welcome.fxml");

        stage.setTitle("Elite Hotel Reservation System");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
