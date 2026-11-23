
package gui.view.auth;

import com.hotel.model.User;
import gui.controller.auth.LoginController;
import gui.navigation.SceneManager;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class LoginView {

    private final LoginController controller = new LoginController();

    public Parent createView(SceneManager sceneManager) {

        // Main StackPane
        StackPane root = new StackPane();
        root.getStyleClass().add("login-root");

        // Background image
        ImageView background = new ImageView(
                new Image(getClass().getResourceAsStream(
                        "/images/reception.png"
                ))
        );

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        // Hotel logo
        ImageView logo = new ImageView(
                new Image(getClass().getResourceAsStream(
                        "/images/hotel-logo.png"
                ))
        );

        logo.setFitWidth(180);
        logo.setPreserveRatio(true);

        // Heading
        Label heading = new Label("Login to Your Account");
        heading.getStyleClass().add("login-heading");
        heading.setWrapText(true);

        // Email
        TextField emailField = new TextField();
        emailField.setPromptText("✉   Email");
        emailField.getStyleClass().add("auth-field");
        emailField.setMaxWidth(Double.MAX_VALUE);

        // Password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("🔒   Password");
        passwordField.getStyleClass().add("auth-field");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Visible password
        TextField visiblePassword = new TextField();
        visiblePassword.setPromptText("🔒   Password");
        visiblePassword.getStyleClass().add("auth-field");
        visiblePassword.setMaxWidth(Double.MAX_VALUE);

        visiblePassword.textProperty().bindBidirectional(
                passwordField.textProperty()
        );

        visiblePassword.setVisible(false);
        visiblePassword.setManaged(false);

        // Eye icon - initially hidden password
        ImageView eyeIcon = new ImageView(
                new Image(getClass().getResourceAsStream(
                        "/images/eye-off.png"
                ))
        );

        eyeIcon.setFitWidth(20);
        eyeIcon.setFitHeight(20);
        eyeIcon.setPreserveRatio(true);

        // Eye button
        Button showPassword = new Button();
        showPassword.setGraphic(eyeIcon);
        showPassword.getStyleClass().add("eye-button");

        // Show / Hide password
        showPassword.setOnAction(e -> {

            boolean show = !visiblePassword.isVisible();

            visiblePassword.setVisible(show);
            visiblePassword.setManaged(show);

            passwordField.setVisible(!show);
            passwordField.setManaged(!show);

            String iconPath = show
                    ? "/images/eye.png"
                    : "/images/eye-off.png";

            eyeIcon.setImage(new Image(
                    getClass().getResourceAsStream(iconPath)
            ));
        });

        // Password container
        StackPane passwordBox = new StackPane(
                passwordField,
                visiblePassword
        );

        passwordBox.setMaxWidth(Double.MAX_VALUE);

        HBox passwordRow = new HBox(
                5,
                passwordBox,
                showPassword
        );

        HBox.setHgrow(passwordBox, Priority.ALWAYS);
        passwordRow.setAlignment(Pos.CENTER);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("gold-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Label message = new Label();
        message.getStyleClass().add("auth-message");
        message.setWrapText(true);

        // Register
        Label registerText = new Label("Don't have an account?");

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("link-button");

        HBox registerRow = new HBox(
                5,
                registerText,
                registerButton
        );

        registerRow.setAlignment(Pos.CENTER);

        registerButton.setOnAction(e ->
                sceneManager.switchScene(
                        new RegisterView().createView(sceneManager)
                )
        );

        // Login card
        VBox card = new VBox(
                14,
                heading,
                emailField,
                passwordRow,
                loginButton,
                message,
                registerRow
        );

        card.getStyleClass().add("login-card");
        card.setFillWidth(true);
        card.setMaxWidth(420);

        // Login event
        loginButton.setOnAction(e -> {

            String email = emailField.getText().trim();
            String password = passwordField.getText();

            loginButton.setDisable(true);
            message.setText("Checking your account...");

            Task<User> loginTask = new Task<>() {

                @Override
                protected User call() {
                    return controller.login(email, password);
                }
            };

            loginTask.setOnSucceeded(event -> {

                loginButton.setDisable(false);

                User user = loginTask.getValue();

                if (user == null) {
                    message.setText("Invalid email or password.");
                } else {

                    passwordField.clear();

                    message.setText(
                            "Welcome, " + user.getFirstName()
                                    + "! Role: " + user.getRole()
                    );
                }
            });

            loginTask.setOnFailed(event -> {

                loginButton.setDisable(false);
                message.setText("Login failed. Please try again.");

                loginTask.getException().printStackTrace();
            });

            Thread thread = new Thread(loginTask);
            thread.setDaemon(true);
            thread.start();
        });

        // Right panel
        VBox rightPanel = new VBox(25, logo, card);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.getStyleClass().add("login-right-panel");
        rightPanel.setMaxWidth(500);
        rightPanel.setPadding(new Insets(30));

        // Background below, form above
        root.getChildren().addAll(background, rightPanel);
        StackPane.setAlignment(rightPanel, Pos.CENTER_RIGHT);

        return root;
    }
}
