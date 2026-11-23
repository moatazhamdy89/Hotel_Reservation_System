
package gui.view.auth;

import com.hotel.manager.UserManager;
import com.hotel.model.Guest;
import com.hotel.utils.Validation;
import gui.navigation.SceneManager;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class RegisterView {

    private final UserManager userManager = new UserManager();

    public Parent createView(SceneManager sceneManager) {

        StackPane root = new StackPane();

        // Background
        ImageView background = new ImageView(
                new Image(getClass().getResource(
                        "/images/register.png").toExternalForm())
        );

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        // Logo
        ImageView logo = new ImageView(
                new Image(getClass().getResource(
                        "/images/hotel-logo.png").toExternalForm())
        );
        logo.setFitWidth(150);
        logo.setPreserveRatio(true);

        Label title = new Label("Create a New Account");
        title.getStyleClass().add("register-title");

        // Fields
        TextField firstName = field("First Name");
        TextField lastName = field("Last Name");
        TextField email = field("Email");
        TextField phone = field("Phone Number");
        TextField nationalId = field("National ID (14 digits)");
        TextField nationality = field("Nationality");

        DatePicker birthDate = new DatePicker();
        birthDate.setPromptText("Date of Birth");
        birthDate.setMaxWidth(Double.MAX_VALUE);
        birthDate.getStyleClass().add("register-field");

        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("Male", "Female");
        gender.setPromptText("Gender");
        gender.setMaxWidth(Double.MAX_VALUE);
        gender.getStyleClass().add("register-field");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.getStyleClass().add("register-field");

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm Password");
        confirmPassword.getStyleClass().add("register-field");

        // Error labels
        Label firstNameError = new Label();
        Label lastNameError = new Label();
        Label emailError = new Label();
        Label phoneError = new Label();
        Label nationalIdError = new Label();
        Label birthDateError = new Label();
        Label nationalityError = new Label();
        Label genderError = new Label();
        Label passwordError = new Label();
        Label confirmPasswordError = new Label();

        // Form layout
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(13);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(fieldWithError(firstName, firstNameError), 0, 0);
        grid.add(fieldWithError(lastName, lastNameError), 1, 0);

        grid.add(fieldWithError(email, emailError), 0, 1);
        grid.add(fieldWithError(phone, phoneError), 1, 1);

        grid.add(fieldWithError(nationalId, nationalIdError), 0, 2);
        grid.add(fieldWithError(birthDate, birthDateError), 1, 2);

        grid.add(fieldWithError(nationality, nationalityError), 0, 3);
        grid.add(fieldWithError(gender, genderError), 1, 3);

        grid.add(fieldWithError(password, passwordError), 0, 4);
        grid.add(fieldWithError(confirmPassword, confirmPasswordError), 1, 4);

        // Buttons
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("register-button");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        Label message = new Label();
        message.setWrapText(true);
        message.getStyleClass().add("register-message");

        Label loginText = new Label("Already have an account?");
        loginText.setStyle("-fx-text-fill: #102443; -fx-font-size: 13px;");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("register-link");

        HBox loginRow = new HBox(5, loginText, loginButton);
        loginRow.setAlignment(Pos.CENTER);

        loginButton.setOnAction(e ->
                sceneManager.switchScene(
                        new LoginView().createView(sceneManager)
                )
        );

        // Registration
        registerButton.setOnAction(e -> {

            // Clear previous errors
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

            // First name
            if (firstName.getText().isBlank()) {
                showFieldError(firstNameError, "First name is required");
                valid = false;
            }

            // Last name
            if (lastName.getText().isBlank()) {
                showFieldError(lastNameError, "Last name is required");
                valid = false;
            }

            // Email
            if (!Validation.isValidEmail(email.getText())) {
                showFieldError(emailError, "Invalid email format");
                valid = false;
            }

            // Phone
            if (!phone.getText().trim().matches("^\\+?[0-9]{11}$")) {
                showFieldError(phoneError, "Invalid phone number");
                valid = false;
            }

            // National ID
            if (!nationalId.getText().matches("[0-9]{14}")) {
                showFieldError(nationalIdError,
                        "National ID must be 14 digits");
                valid = false;
            }

            // Date of birth
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

            // Nationality
            if (nationality.getText().isBlank()) {
                showFieldError(nationalityError, "Nationality is required");
                valid = false;
            }

            // Gender
            if (gender.getValue() == null) {
                showFieldError(genderError, "Please select your gender");
                valid = false;
            }

            // Password - existing backend validation
            List<String> passwordProblems =
                    Validation.passwordProblems(
                            password.getText(), email.getText()
                    );

            if (!passwordProblems.isEmpty()) {
                showFieldError(passwordError,
                        String.join("; ", passwordProblems));
                valid = false;
            }

            // Confirm password
            if (!password.getText().equals(confirmPassword.getText())) {
                showFieldError(confirmPasswordError,
                        "Passwords do not match");
                valid = false;
            }

            // Show all errors before saving
            if (!valid) {
                return;
            }

            // Get entered data
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

            // Save using existing backend
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

                sceneManager.switchScene(
                        new LoginView().createView(sceneManager)
                );
            });

            task.setOnFailed(event -> {
                registerButton.setDisable(false);

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
        });

        // Card
        VBox card = new VBox(
                18, title, grid, registerButton, message, loginRow
        );
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("register-card");
        card.setMaxWidth(550);

        // Right panel
        VBox rightPanel = new VBox(15, logo, card);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(25));
        rightPanel.setMaxWidth(650);

        ScrollPane scroll = new ScrollPane(rightPanel);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.getStyleClass().add("register-scroll");
        scroll.setMaxWidth(650);

        // Background below, form above
        root.getChildren().addAll(background, scroll);
        StackPane.setAlignment(scroll, Pos.CENTER_RIGHT);

        return root;
    }

    private TextField field(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.getStyleClass().add("register-field");
        textField.setMaxWidth(Double.MAX_VALUE);
        return textField;
    }

    private VBox fieldWithError(Control field, Label error) {
        error.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        error.setWrapText(true);
        error.setVisible(false);
        error.setManaged(false);

        VBox box = new VBox(3, error, field);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private void showFieldError(Label label, String text) {
        boolean hasError = text != null && !text.isEmpty();

        label.setText(hasError ? text : "");
        label.setVisible(hasError);
        label.setManaged(hasError);
    }
}
