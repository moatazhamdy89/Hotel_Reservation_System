
package gui.controller.auth;

import com.hotel.manager.UserManager;
import com.hotel.model.Guest;
import com.hotel.utils.Validation;
import gui.navigation.SceneManager;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class RegisterController {

    private final UserManager userManager = new UserManager();
    private SceneManager sceneManager;

    @FXML private StackPane root;
    @FXML private ImageView background;

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private TextField nationalId;
    @FXML private TextField nationality;

    @FXML private DatePicker birthDate;
    @FXML private ComboBox<String> gender;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPassword;

    @FXML private Label firstNameError;
    @FXML private Label lastNameError;
    @FXML private Label emailError;
    @FXML private Label phoneError;
    @FXML private Label nationalIdError;
    @FXML private Label birthDateError;
    @FXML private Label nationalityError;
    @FXML private Label genderError;
    @FXML private Label passwordError;
    @FXML private Label confirmPasswordError;

    @FXML private Button registerButton;
    @FXML private Label message;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        gender.getItems().addAll("Male", "Female");
    }

    @FXML
    private void handleRegister() {

        Label[] errorLabels = {
                firstNameError, lastNameError,
                emailError, phoneError,
                nationalIdError, birthDateError,
                nationalityError, genderError,
                passwordError, confirmPasswordError
        };

        for (Label errorLabel : errorLabels) {
            showFieldError(errorLabel, "");
        }

        message.setText("");
        boolean valid = true;

        if (firstName.getText().isBlank()) {
            showFieldError(firstNameError, "First name is required");
            valid = false;
        }

        if (lastName.getText().isBlank()) {
            showFieldError(lastNameError, "Last name is required");
            valid = false;
        }

        if (!Validation.isValidEmail(email.getText())) {
            showFieldError(emailError, "Invalid email format");
            valid = false;
        }

        if (!phone.getText().trim().matches("^\\+?[0-9]{11}$")) {
            showFieldError(phoneError, "Invalid phone number");
            valid = false;
        }

        if (!nationalId.getText().matches("[0-9]{14}")) {
            showFieldError(nationalIdError,
                    "National ID must be 14 digits");
            valid = false;
        }

        int age = 0;

        if (birthDate.getValue() == null) {
            showFieldError(birthDateError, "Date of birth is required");
            valid = false;
        } else if (birthDate.getValue().isAfter(LocalDate.now())) {
            showFieldError(birthDateError, "Invalid date of birth");
            valid = false;
        } else {
            age = Period.between(
                    birthDate.getValue(), LocalDate.now()
            ).getYears();

            if (age < 18) {
                showFieldError(birthDateError,
                        "You must be at least 18 years old");
                valid = false;
            }
        }

        if (nationality.getText().isBlank()) {
            showFieldError(nationalityError, "Nationality is required");
            valid = false;
        }

        if (gender.getValue() == null) {
            showFieldError(genderError, "Please select your gender");
            valid = false;
        }

        List<String> passwordProblems =
                Validation.passwordProblems(
                        password.getText(), email.getText()
                );

        if (!passwordProblems.isEmpty()) {
            showFieldError(passwordError,
                    String.join("; ", passwordProblems));
            valid = false;
        }

        if (!password.getText().equals(confirmPassword.getText())) {
            showFieldError(confirmPasswordError,
                    "Passwords do not match");
            valid = false;
        }

        if (!valid) {
            return;
        }

        String enteredNationalId = nationalId.getText();
        String enteredFirstName = firstName.getText();
        String enteredLastName = lastName.getText();
        String enteredGender = gender.getValue();
        String enteredEmail = email.getText();
        String enteredPassword = password.getText();
        String enteredNationality = nationality.getText();
        String enteredPhone = phone.getText();
        int enteredAge = age;

        registerButton.setDisable(true);
        message.setText("Creating your account...");

        Task<Guest> task = new Task<>() {
            @Override
            protected Guest call() {
                return userManager.registerGuest(
                        enteredNationalId,
                        enteredFirstName,
                        enteredLastName,
                        enteredGender,
                        enteredAge,
                        enteredEmail,
                        enteredPassword,
                        enteredNationality,
                        enteredPhone
                );
            }
        };

        task.setOnSucceeded(event -> {
            registerButton.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText("Account created successfully!");
            alert.setContentText("You can now log in.");
            alert.showAndWait();

            sceneManager.switchToFXML("/fxml/auth/login.fxml");
        });

        task.setOnFailed(event -> {
            registerButton.setDisable(false);
            message.setText("");

            Throwable error = task.getException();

            if (error instanceof IllegalArgumentException) {
                String errorMessage = error.getMessage();

                if (errorMessage != null
                        && errorMessage.contains("already registered")) {
                    showFieldError(emailError, errorMessage);
                } else {
                    message.setText(errorMessage);
                }
            } else {
                message.setText("Registration failed. Please try again.");
                if (error != null) {
                    error.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void openLogin() {
        sceneManager.switchToFXML("/fxml/auth/login.fxml");
    }

    private void showFieldError(Label label, String text) {
        boolean hasError = text != null && !text.isEmpty();

        label.setText(hasError ? text : "");
        label.setVisible(hasError);
        label.setManaged(hasError);
    }
}
