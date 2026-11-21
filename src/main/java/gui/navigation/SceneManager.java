
package gui.navigation;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
}
