package gui.controller.auth;

import com.hotel.manager.UserManager;
import com.hotel.model.User;
import gui.navigation.SceneManager;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class LoginController {

    private final UserManager userManager = new UserManager();

    private SceneManager sceneManager;

    @FXML private StackPane root;
    @FXML private ImageView background;
    @FXML private ImageView eyeIcon;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePassword;

    @FXML private Button loginButton;
    @FXML private Label message;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {

        // Responsive background
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        // Synchronize hidden and visible password
        visiblePassword.textProperty().bindBidirectional(
                passwordField.textProperty()
        );
    }

    // Existing login logic - unchanged
    public User login(String email, String password) {

        if (email == null || password == null){
            email.isBlank();
            password.isBlank();
            return null;
        }

        for (User user : userManager.findAllUsers()) {

            if (user.getEmail() != null
                    && user.getEmail().equalsIgnoreCase(email.trim())) {

                if (user.login(email, password)) {
                    return user;
                }

                return null;
            }
        }

        return null;
    }

    @FXML
    private void togglePassword() {

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
    }

    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        loginButton.setDisable(true);
        message.setText("Checking your account...");

        Task<User> loginTask = new Task<>() {

            @Override
            protected User call() {
                return login(email, password);
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
    }

    @FXML
    private void openRegister() {
        sceneManager.switchToFXML("/fxml/auth/register.fxml");
    }
}