
package gui.view.auth;

import gui.navigation.SceneManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class WelcomeView {

    public Parent createView(SceneManager sceneManager) {

        // Logo
        Image logo = new Image(
                getClass().getResourceAsStream(
                        "/images/hotel-logo.png"
                )
        );

        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(190);
        logoView.setPreserveRatio(true);

        // Header: logo on left, Home on right
        Button homeButton = new Button("Home");
        homeButton.getStyleClass().add("home-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(20);
        header.getChildren().addAll(
                logoView, spacer, homeButton
        );
        header.getStyleClass().add("header");

        // Welcome text
        Label welcome = new Label("Welcome to");
        welcome.getStyleClass().add("welcome-title");

        Label hotelName = new Label("Elite Hotel");
        hotelName.getStyleClass().add("hotel-name");

        Label description = new Label(
                "Book your stay, enjoy premium\n" +
                        "services, and experience\n" +
                        "true hospitality."
        );
        description.getStyleClass().add(
                "welcome-description"
        );

        // Buttons
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setPrefSize(280, 55);

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add(
                "register-button"
        );
        registerButton.setPrefSize(280, 55);

        // Login navigation
        loginButton.setOnAction(e -> {
            LoginView loginView = new LoginView();

            sceneManager.switchScene(
                    loginView.createView(sceneManager)
            );
        });

        // Register will be connected later

        VBox leftContent = new VBox(18);
        leftContent.getChildren().addAll(
                welcome,
                hotelName,
                description,
                loginButton,
                registerButton
        );

        leftContent.setAlignment(Pos.CENTER_LEFT);
        leftContent.setPadding(
                new Insets(30, 25, 40, 70)
        );

        HBox.setHgrow(leftContent, Priority.ALWAYS);

        // Hotel building image
        Image building = new Image(
                getClass().getResourceAsStream(
                        "/images/hotel-building.jpg"
                )
        );

        ImageView buildingView = new ImageView(building);
        buildingView.setFitWidth(470);
        buildingView.setFitHeight(470);
        buildingView.setPreserveRatio(false);

        // Main content
        HBox mainContent = new HBox(15);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(
                leftContent, buildingView
        );

        // Full page
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(mainContent);

        root.getStyleClass().add("welcome-root");

        return root;
    }
}
