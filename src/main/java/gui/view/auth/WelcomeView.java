
package gui.view.auth;

import gui.navigation.SceneManager;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class WelcomeView {

    public Parent createView(SceneManager sceneManager) {

        // Main layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("welcome-root");

        // Logo
        Image logo = new Image(
                getClass().getResourceAsStream("/images/hotel-logo.png")
        );

        ImageView logoView = new ImageView(logo);
        logoView.setPreserveRatio(true);

        // Home button
        Button homeButton = new Button("Home");
        homeButton.getStyleClass().add("home-button");

        // Header: logo left, Home exactly centered
        StackPane header = new StackPane();
        header.getStyleClass().add("header");

        StackPane.setAlignment(logoView, Pos.CENTER_LEFT);
        StackPane.setAlignment(homeButton, Pos.CENTER);

        header.getChildren().addAll(logoView, homeButton);
        root.setTop(header);

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
        description.getStyleClass().add("welcome-description");

        // Buttons
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnAction(e -> {

            LoginView loginView = new LoginView();

            sceneManager.switchScene(
                    loginView.createView(sceneManager)
            );

        });

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("register-button");

        loginButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setMaxWidth(Double.MAX_VALUE);

        registerButton.setOnAction(e -> {
            sceneManager.switchScene(
                    new RegisterView().createView(sceneManager)
            );
        });

        // Login navigation
        loginButton.setOnAction(e -> {
            LoginView loginView = new LoginView();

            sceneManager.switchScene(
                    loginView.createView(sceneManager)
            );
        });

        // Register will be connected later

        VBox buttons = new VBox(12, loginButton, registerButton);
        buttons.setFillWidth(true);
        buttons.setMaxWidth(320);

        // Left content
        VBox leftContent = new VBox(
                18,
                welcome,
                hotelName,
                description,
                buttons
        );

        leftContent.setAlignment(Pos.CENTER_LEFT);
        leftContent.setMaxWidth(Region.USE_PREF_SIZE);

        // Content container
        StackPane contentContainer = new StackPane(leftContent);
        contentContainer.setAlignment(Pos.CENTER_LEFT);

        // Scroll for smaller screens
        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("welcome-scroll");

        root.setCenter(scrollPane);

        // Responsive resizing
        root.widthProperty().addListener((obs, oldWidth, newWidth) -> {

            double width = newWidth.doubleValue();
            boolean compact = width < 850;

            // Responsive logo size
            logoView.setFitWidth(
                    Math.min(350, Math.max(130, width * 0.23))
            );

            // Responsive horizontal spacing
            double sidePadding = Math.min(70, Math.max(18, width * 0.05));

            header.setPadding(new Insets(
                    12, sidePadding, 12, sidePadding
            ));

            contentContainer.setPadding(new Insets(
                    25, sidePadding, 30, sidePadding
            ));

            // Responsive font sizes
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

            // Responsive button width
            buttons.setPrefWidth(
                    Math.min(280, Math.max(180, width * 0.25))
            );

            // Compact layout
            leftContent.setSpacing(compact ? 12 : 18);
        });

        return root;
    }
}
