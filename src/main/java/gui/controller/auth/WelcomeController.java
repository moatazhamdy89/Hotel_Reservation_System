
package gui.controller.auth;

import gui.navigation.SceneManager;
import gui.view.auth.LoginView;
import gui.view.auth.RegisterView;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

public class WelcomeController {

    @FXML private BorderPane root;
    @FXML private StackPane header;
    @FXML private StackPane contentContainer;
    @FXML private VBox leftContent;
    @FXML private VBox buttons;

    @FXML private ImageView logoView;
    @FXML private Label welcome;
    @FXML private Label hotelName;
    @FXML private Label description;

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {

        root.widthProperty().addListener(
                (obs, oldWidth, newWidth) -> {

                    double width = newWidth.doubleValue();
                    boolean compact = width < 850;

                    logoView.setFitWidth(
                            Math.min(350, Math.max(130, width * 0.23))
                    );

                    double sidePadding =
                            Math.min(70, Math.max(18, width * 0.05));

                    header.setPadding(new Insets(
                            12, sidePadding, 12, sidePadding
                    ));

                    contentContainer.setPadding(new Insets(
                            25, sidePadding, 30, sidePadding
                    ));

                    welcome.setStyle(
                            "-fx-font-size: " +
                                    Math.min(50, Math.max(27, width * 0.039)) +
                                    "px;"
                    );

                    hotelName.setStyle(
                            "-fx-font-size: " +
                                    Math.min(60, Math.max(34, width * 0.047)) +
                                    "px;"
                    );

                    description.setStyle(
                            "-fx-font-size: " +
                                    Math.min(19, Math.max(14, width * 0.015)) +
                                    "px;"
                    );

                    buttons.setPrefWidth(
                            Math.min(280, Math.max(180, width * 0.25))
                    );

                    leftContent.setSpacing(compact ? 12 : 18);
                }
        );
    }

    @FXML
    private void openLogin() {
        sceneManager.switchScene(
                new LoginView().createView(sceneManager)
        );
    }

    @FXML
    private void openRegister() {
        sceneManager.switchToFXML("/fxml/auth/register.fxml");
    }
}
